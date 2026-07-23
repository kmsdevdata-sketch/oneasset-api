import http from "k6/http"; // k6에서 HTTP 요청을 보내기 위한 모듈
import { check, sleep } from "k6"; // 응답 검증(check), 대기(sleep) 함수
import exec from "k6/execution"; // 현재 시나리오의 전체 iteration 번호를 알기 위한 k6 실행 컨텍스트
import { Rate, Trend } from "k6/metrics"; // 커스텀 성공률/시간 지표를 만들기 위한 모듈

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || "https://api.oneasset.tech"; // 테스트 대상 API 주소
const API_KEY = __ENV.ONEASSET_API_KEY; // 테스트용 raw API key
const TOTAL_UPLOADS = Number(__ENV.ONEASSET_UPLOAD_BURST_TOTAL || 300); // 전체 업로드 횟수. 기본 300개
const UPLOAD_VUS = Number(__ENV.ONEASSET_UPLOAD_BURST_VUS || 30); // 동시에 업로드를 수행할 가상 사용자 수. 기본 30명
const SAMPLE_READY_EVERY = Number(__ENV.ONEASSET_READY_SAMPLE_EVERY || 25); // 몇 개마다 READY 상태를 샘플링할지. 기본 25개마다 1개
const RUN_ID = __ENV.ONEASSET_LOAD_TEST_RUN_ID || `${Date.now()}`; // 이번 테스트 실행을 구분하기 위한 ID

const imageFile = open("../fixtures/sample-profile.jpg", "b"); // 업로드에 사용할 이미지 파일을 바이너리로 읽음

const uploadSuccessRate = new Rate("upload_success_rate"); // 업로드 API 성공률 커스텀 지표
const readySampleSuccessRate = new Rate("ready_sample_success_rate"); // 샘플링한 에셋의 READY 전환 성공률
const readySampleDuration = new Trend("ready_sample_duration"); // 업로드 후 READY까지 걸린 시간 샘플 지표

export const options = { // k6 실행 옵션
  scenarios: { // 테스트 시나리오 정의
    upload_burst: { // 시나리오 이름
      executor: "shared-iterations", // 전체 iteration 수를 여러 VU가 나눠서 처리하는 방식
      vus: UPLOAD_VUS, // 동시에 실행할 가상 사용자 수
      iterations: TOTAL_UPLOADS, // 총 업로드 횟수
      maxDuration: "10m", // 테스트가 비정상적으로 오래 걸릴 때 최대 10분에서 중단
    },
  },
  thresholds: { // 테스트 성공/실패 기준
    http_req_failed: ["rate<0.02"], // HTTP 실패율 2% 미만
    http_req_duration: ["p(95)<2000", "p(99)<5000"], // API 응답 p95 2초 미만, p99 5초 미만
    checks: ["rate>0.98"], // check 성공률 98% 초과
    upload_success_rate: ["rate>0.98"], // 업로드 성공률 98% 초과
    ready_sample_success_rate: ["rate>0.95"], // 샘플 READY 성공률 95% 초과
    ready_sample_duration: ["p(95)<30000"], // 샘플 READY 전환 p95 30초 미만
  },
};

export default function () { // 각 iteration마다 실행되는 함수
  const iteration = exec.scenario.iterationInTest; // 전체 테스트 기준 현재 iteration 번호
  const key = `load-test/burst/${RUN_ID}/${iteration}-${__VU}-${__ITER}-profile.jpg`; // 매 업로드마다 충돌하지 않는 고유 key 생성
  const uploadStartedAt = Date.now(); // READY 전환 시간 측정을 위한 업로드 시작 시각

  const upload = http.post( // 에셋 업로드 API 호출
    `${BASE_URL}/v1/assets`, // 개발자용 에셋 업로드 엔드포인트
    {
      file: http.file(imageFile, "sample-profile.jpg", "image/jpeg"), // multipart file 파트
      key, // 사용자가 지정하는 에셋 key
      fileName: "sample-profile.jpg", // 원본 파일명
    },
    {
      headers: { // 요청 헤더
        "X-OneAsset-Api-Key": API_KEY, // 개발자 API key 인증 헤더
      },
      tags: { // k6 결과에서 업로드 요청을 구분하기 위한 태그
        endpoint: "asset_upload_burst", // 업로드 burst 요청 태그
      },
    }
  );

  const uploadOk = check(upload, { // 업로드 응답 검증
    "upload status is 200": (res) => res.status === 200, // HTTP 200인지 확인
    "upload response has PROCESSING or READY": (res) => { // 응답 상태가 처리 중 또는 완료인지 확인
      try {
        const body = res.json(); // 응답 JSON 파싱
        const status = body.data && body.data.status; // 응답의 data.status 값 추출
        return status === "PROCESSING" || status === "READY"; // 허용 가능한 상태인지 확인
      } catch (e) {
        return false; // JSON 파싱 실패 시 check 실패
      }
    },
  });

  uploadSuccessRate.add(uploadOk); // 업로드 성공 여부를 커스텀 지표에 기록

  if (!uploadOk) { // 업로드 자체가 실패하면
    readySampleSuccessRate.add(false); // READY 샘플도 실패로 기록
    return; // 이번 iteration 종료
  }

  if (iteration % SAMPLE_READY_EVERY !== 0) { // 모든 업로드를 polling하면 조회 부하가 섞이므로 일부만 샘플링
    return; // 샘플 대상이 아니면 업로드만 하고 종료
  }

  let becameReady = false; // 샘플 에셋이 READY가 되었는지 여부

  for (let attempt = 0; attempt < 30; attempt += 1) { // 최대 30초 동안 READY 여부 확인
    sleep(1); // Lambda/SQS 비동기 처리를 기다리기 위해 1초 대기

    const detail = http.get(`${BASE_URL}/v1/assets?key=${encodeURIComponent(key)}`, { // 방금 업로드한 에셋 단일 조회
      headers: { // 요청 헤더
        "X-OneAsset-Api-Key": API_KEY, // 개발자 API key 인증 헤더
      },
      tags: { // k6 결과에서 polling 요청을 구분하기 위한 태그
        endpoint: "asset_ready_sample_poll", // READY 샘플 polling 요청 태그
      },
    });

    const detailOk = check(detail, { // 단일 조회 응답 검증
      "ready sample detail status is 200": (res) => res.status === 200, // HTTP 200인지 확인
    });

    if (!detailOk) { // 조회가 실패하면
      continue; // 다음 polling 시도
    }

    const body = detail.json(); // 응답 JSON 파싱
    const status = body.data && body.data.status; // 현재 에셋 상태 추출

    if (status === "READY") { // Lambda 후처리와 callback까지 완료된 상태면
      becameReady = true; // READY 성공 표시
      readySampleDuration.add(Date.now() - uploadStartedAt); // 업로드부터 READY까지 걸린 시간 기록
      break; // polling 종료
    }
  }

  readySampleSuccessRate.add(becameReady); // 샘플 에셋의 최종 READY 성공 여부 기록
}
