import axios, {CreateAxiosDefaults} from 'axios';
import store from '../store/store'
import {RootState} from "../store/store";
import {setTokens} from "../features/authSlice";
// type instanceType = {
//     baseURL : string;
//     timeout : number;
// }
// Axios Instance 생성
const axiosInstance  = axios.create({
    baseURL: `${process.env.NEXT_PUBLIC_BACKEND_URL}` as string, // 서버의 기본 URL
    timeout: 10000, // 요청 타임아웃 설정 (10초)x
} as CreateAxiosDefaults);

// 요청 인터셉터 설정
axiosInstance.interceptors.request.use(
    (config) => {
        const state :RootState = store.getState(); // Redux 상태 가져오기
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
// axiosInstance.interceptors.response.use(
//     (response) => {
//         // 응답 데이터 처리
//         return response;
//     },
//     (error) => {
//         // 응답 에러 처리
//         console.error('Axios error:', error.response || error.message);
//         return Promise.reject(error);
//     }
// );
axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshResponse = await axios.post('/api/auth/refresh', null, {
                    withCredentials: true,
                });
                const newAccessToken = refreshResponse.data.accessToken;

                // Redux 또는 로컬 상태에 새로운 Access Token 저장
                store.dispatch(setTokens({ accessToken: newAccessToken }));

                // 기존 요청에 새로운 토큰 추가
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

                // 기존 요청 재전송
                return axios(originalRequest);
            } catch (refreshError) {
                console.error("Refresh Token Error:", refreshError);
                return Promise.reject(refreshError); // 갱신 실패 시 에러 반환
            }
        }

        return Promise.reject(error);
    }
);


export default axiosInstance;