import http from "k6/http"; // HTTP 요청 도구
import { check, sleep } from "k6"; // 검증과 대기 함수

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || "https://api.oneasset.tech"; // API 주소
const API_KEY = __ENV.ONEASSET_API_KEY; // raw API key

export const options = { // 테스트 설정
  stages: [ // 순간 트래픽 변화 시나리오
    { duration: "30s", target: 5 }, // 30초 동안 VU 5명까지 증가
    { duration: "30s", target: 20 }, // 30초 동안 VU 20명까지 증가
    { duration: "30s", target: 50 }, // 30초 동안 VU 50명까지 증가
    { duration: "30s", target: 5 }, // 30초 동안 VU 5명으로 감소
    { duration: "30s", target: 0 }, // 30초 동안 종료
  ],
  thresholds: { // 성공 기준
    http_req_failed: ["rate<0.03"], // 실패율 3% 미만
    http_req_duration: ["p(95)<1500"], // 95% 요청이 1.5초 미만
    checks: ["rate>0.97"], // check 성공률 97% 초과
  },
};

export default function () { // VU 반복 실행 본문
  const res = http.get(`${BASE_URL}/v1/assets`, { // 에셋 목록 조회
    headers: {
      "X-OneAsset-Api-Key": API_KEY, // API key 전달
    },
    tags: { endpoint: "asset_list_spike" }, // 스파이크 테스트 태그
  });

  check(res, { // 응답 검증
    "list status is 200": (res) => res.status === 200, // 200 확인
  });

  sleep(1); // 1초 대기해서 완전한 무한 폭격 방지
}