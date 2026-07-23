import http from "k6/http"; 
import { check } from "k6"; 

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || "https://api.oneasset.tech"; 
const API_KEY = __ENV.ONEASSET_API_KEY; 

export const options = { 
  scenarios: { // 테스트 시나리오 정의
    api_stress_rps: { // 시나리오 이름
      executor: "ramping-arrival-rate", // VU 수가 아니라 초당 요청 도착률(RPS)을 단계적으로 조절하는 executor
      startRate: 10, // 시작 시 초당 10회 iteration으로 시작
      timeUnit: "1s", // rate 기준 단위. 여기서는 1초당 몇 번 실행할지 의미
      preAllocatedVUs: 80, // k6가 미리 준비해둘 가상 사용자 수. 요청 지연이 생겨도 목표 RPS를 유지하기 위한 여유분
      maxVUs: 400, // 목표 RPS를 유지하기 위해 필요하면 최대 400 VU까지 늘릴 수 있음
      stages: [ // 시간에 따라 목표 RPS를 변경하는 단계
        { duration: "1m", target: 50 }, // 1분 동안 50 req/s까지 증가
        { duration: "1m", target: 100 }, // 다음 1분 동안 100 req/s까지 증가
        { duration: "1m", target: 200 }, // 다음 1분 동안 200 req/s까지 증가
        { duration: "1m", target: 300 }, // 다음 1분 동안 300 req/s까지 증가
        { duration: "30s", target: 0 }, // 30초 동안 요청을 0으로 줄이며 종료
      ],
    },
  },
  thresholds: { // 테스트 성공/실패 기준
    http_req_failed: ["rate<0.01"], // HTTP 실패율이 1% 미만이어야 통과
    http_req_duration: ["p(95)<1000", "p(99)<2000"], // 95% 요청은 1초 미만, 99% 요청은 2초 미만이어야 통과
    checks: ["rate>0.99"], // 아래 check 검증 성공률이 99% 초과여야 통과
  },
};

export default function () { // k6가 각 iteration마다 실행하는 함수
  const response = http.get(`${BASE_URL}/v1/assets`, { // 에셋 목록 조회 API 호출
    headers: { // 요청 헤더
      "X-OneAsset-Api-Key": API_KEY, // 개발자 API key 인증 헤더
    },
    tags: { // k6 결과에서 이 요청을 구분하기 위한 태그
      endpoint: "asset_list_stress", // endpoint 이름을 asset_list_stress로 표시
    },
  });

  check(response, { // 응답 검증
    "list status is 200": (res) => res.status === 200, // HTTP 상태 코드가 200인지 확인
    "list response is JSON": (res) => // 응답 Content-Type이 JSON인지 확인
      (res.headers["Content-Type"] || "").includes("application/json"),
  });
}