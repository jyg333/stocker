import axios from 'axios';
import { persistor } from '../store/store'; // Redux store import 경로 확인
import {store} from '../store'
type instanceType = {
    baseURL : string;
    timeout : number;
}
// Axios Instance 생성
const axiosInstance  = axios.create({
    baseURL: `${process.env.BACKEND_URL}`, // 서버의 기본 URL
    timeout: 10000, // 요청 타임아웃 설정 (10초)
});

// 요청 인터셉터 설정
axiosInstance.interceptors.request.use(
    (config) => {
        const state = store.getState(); // Redux 상태 가져오기
        const accessToken = state.auth?.accessToken; // Redux에서 accessToken 가져오기

        if (accessToken) {
            // Header에 Authorization 추가
            config.headers['Authorization'] = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        // 요청 에러 처리
        return Promise.reject(error);
    }
);

// 응답 인터셉터 설정
axiosInstance.interceptors.response.use(
    (response) => {
        // 응답 데이터 처리
        return response;
    },
    (error) => {
        // 응답 에러 처리
        console.error('Axios error:', error.response || error.message);
        return Promise.reject(error);
    }
);

export default axiosInstance;