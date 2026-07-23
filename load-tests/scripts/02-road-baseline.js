import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.ONEASSET_API_BASE_URL || 'https://api.oneasset.tech';
const API_KEY = __ENV.ONEASSET_API_KEY;
const EXISTING_ASSET_KEY = __ENV.ONEASSET_EXISTING_ASSET_KEY;

export const options = {
    stages:[ // 부하를 단계적으로 변화 
        {duration: '30s', target: 5}, // 30초 동안 5명의 가상 사용자로 증가
        {duration: '2m', target: 10}, // 2분 동안 10명의 가상 사용자 유지
        {duration: '30s', target: 0}, // 30초 동안 0명의 가상 사용자로 감소
    ],
    thresholds: { // 성공 기준
        http_req_failed: ['rate<0.01'], // 실패율 1% 미만
        http_req_duration: ["p(95)<800"], // 95요청이 800ms 미만
        checks: ["rate>0.99"], // 체크 성공률 99% 초과
    },
};

export default function () { // 각 가상 사용자가 반복 실행하는 본문
    const headers = {
        "X-OneAsset-Api-Key": API_KEY, // API Key 헤더 추가
    };

    const list = http.get(`${BASE_URL}/v1/assets`, { // 목록 조회
         headers, // 위에서 만든 headers 사용
         tags: {endpoint: "assets-list"}, // 결과를 endpoint 이름으로 분류 
         }); 

    check(list, { // 목록 조회 응답 검증
        "list status is 200": (r) => r.status === 200, // HTTP 200 인지 확인
    });

    if(EXISTING_ASSET_KEY){ // 기존 에셋 key가 있을때만 단일 조회 실행 
        const detail = http.get( //단일에셋 조회
            `${BASE_URL}/v1/assets?key=${encodeURIComponent(EXISTING_ASSET_KEY)}`, // KEY를 URL 안전하게 인코딩 
            {
                headers, // API key 헤더 추가 
                tags: {endpoint: "assets-detail"}, // 상세 조회 태그 
            }
        );

        check(detail, { // 상세 조회 응답 검증 
            "detail status is 200": (r) => r.status === 200, // HTTP 200 인지 확인
        });
    }

    sleep(1); // 1초 대기
}


