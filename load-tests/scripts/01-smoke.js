/*
- 서버가 살아있는지 확인
- API Key가 맞는지 확인
- k6 스크립트 정상 동작 확인

- 큰 부하를 걸기전 실패 원인이 서버성능 인지 스크립트/인증/환경변수 실수 인지 분리 위해
 */

import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || "https://api.oneasset.tech";
const API_KEY = __ENV.ONEASSET_API_KEY;

export const options = {
    vus: 1, // 가상 사용자 1명
    duration: "30s", // 30초 동안 실행
    thresholds: { // 테스트 성공/실패 기준
        http_req_failed: ["rate<0.01"], // 실패율 1% 미만
        http_req_duration: ["p(95)<1000"], // 95% 요청이 1초 이내에 완료
        checks: ["rate>0.99"], // 체크 성공률 99% 이상
    },
};

export default function () { // 각 가상 사용자가 반복 실행하는 본문 
    const health = http.get(`${BASE_URL}/actuator/health`); // health 체크

    check(health, { // health 응답 검증 
        "health status is 200": (r) => r.status === 200, // HTTP 200 인지 확인 
        "health body says UP": (r) => r.json().status === "UP", // 응답본문 UP포함 확인
    });

    const assets = http.get(`${BASE_URL}/v1/assets`,{ // 에셋 목록 API 호출 
        headers: {
            "X-OneAsset-Api-Key": API_KEY, // API Key 헤더 추가
        },
    });

    check(assets,{ // 목록 응답 검증
        "assets list status is 200": (r) => r.status === 200, // HTTP 200 인지 확인
        "assets list response is JSON": (r) => // 응답본문 JSON 확인
        (r.headers["Content-Type"] || "").includes("application/json"), 

    });

    sleep(1); // 1초 대기
}
