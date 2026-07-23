import http from "k6/http"; // HTTP 요청 도구
import { check, sleep } from "k6"; // 검증과 대기 함수
import { Trend, Rate } from "k6/metrics"; // 커스텀 지표 타입 가져옴

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || "https://api.oneasset.tech"; // API 주소
const API_KEY = __ENV.ONEASSET_API_KEY; // raw API key

const imageFile = open("../fixtures/sample-profile.jpg", "b"); // 테스트용 jpg 파일을 바이너리로 읽음

const readyDuration = new Trend("asset_ready_duration"); // 업로드 후 READY까지 걸린 시간 지표
const readySuccessRate = new Rate("asset_ready_success_rate"); // READY 성공률 지표

export const options = { // 테스트 설정
  scenarios: { // 실행 시나리오 정의
    upload_pipeline: { // 시나리오 이름
      executor: "shared-iterations", // 전체 반복 횟수를 VU들이 나눠 실행
      vus: 2, // 가상 사용자 2명
      iterations: 20, // 총 20번 업로드
      maxDuration: "5m", // 최대 5분까지만 실행
    },
  },
  thresholds: { // 성공 기준
    http_req_failed: ["rate<0.02"], // HTTP 실패율 2% 미만
    checks: ["rate>0.98"], // check 성공률 98% 초과
    asset_ready_success_rate: ["rate>0.95"], // READY 성공률 95% 초과
    asset_ready_duration: ["p(95)<15000"], // READY까지 95%가 15초 미만
  },
};

export default function () { // 각 반복 실행 본문
  const key = `load-test/${Date.now()}-${__VU}-${__ITER}-profile.jpg`; // 충돌 방지용 고유 key 생성

  const uploadStartedAt = Date.now(); // 업로드 시작 시간 기록

  const upload = http.post( // multipart 업로드 요청
    `${BASE_URL}/v1/assets`, // 업로드 API
    {
      file: http.file(imageFile, "sample-profile.jpg", "image/jpeg"), // Multipart file 파트
      key, // 사용자 지정 에셋 key
      fileName: "sample-profile.jpg", // 원본 파일명
    },
    {
      headers: {
        "X-OneAsset-Api-Key": API_KEY, // API key 전달
      },
      tags: { endpoint: "asset_upload" }, // 업로드 요청 태그
    }
  );

  const uploadOk = check(upload, { // 업로드 응답 검증
    "upload status is 200": (res) => res.status === 200, // 200 확인
    "upload response has PROCESSING or READY": (res) => { // 상태값 확인
      try {
        const body = res.json(); // JSON 파싱
        const status = body.data && body.data.status; // data.status 접근
        return status === "PROCESSING" || status === "READY"; // 허용 상태 확인
      } catch (e) {
        return false; // JSON 파싱 실패 시 검증 실패
      }
    },
  });

  if (!uploadOk) { // 업로드 실패면
    readySuccessRate.add(false); // READY 실패로 기록
    return; // 현재 반복 종료
  }

  let becameReady = false; // READY 도달 여부

  for (let attempt = 0; attempt < 15; attempt += 1) { // 최대 15번 조회
    sleep(1); // 1초 기다린 뒤 조회

    const detail = http.get(`${BASE_URL}/v1/assets?key=${encodeURIComponent(key)}`, { // 방금 올린 에셋 조회
      headers: {
        "X-OneAsset-Api-Key": API_KEY, // API key 전달
      },
      tags: { endpoint: "asset_detail_poll" }, // 폴링 요청 태그
    });

    const detailOk = check(detail, { // 조회 응답 검증
      "detail poll status is 200": (res) => res.status === 200, // 200 확인
    });

    if (!detailOk) { // 조회 실패면
      continue; // 다음 폴링으로 넘어감
    }

    const body = detail.json(); // JSON 파싱
    const status = body.data && body.data.status; // 상태값 추출

    if (status === "READY") { // Lambda 후처리 완료 상태면
      becameReady = true; // 성공 표시
      readyDuration.add(Date.now() - uploadStartedAt); // READY까지 걸린 시간 기록
      break; // 폴링 종료
    }
  }

  readySuccessRate.add(becameReady); // 최종 READY 성공 여부 기록
}