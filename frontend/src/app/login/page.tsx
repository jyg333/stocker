"use client";

import React, { useState } from 'react';
import axios from "axios";
import Image from "next/image";
import { AppDispatch } from '../store/store';
import { useDispatch } from 'react-redux';
import {setMember, setTokens} from '../features/authSlice';
import {setCookie} from '../utils/cookies'
import {useRouter} from "next/navigation";

type LoginFormType = {
    id: string,
    password :string;
}
type LoginResponse = {
    accessToken: string;
    refreshToken: string;
    auth_level: string;
}

const LoginPage: React.FC = () => {
    const router = useRouter();
    const [loginForm, setLoginForm] = useState<LoginFormType>({
        id:"",
        password :""
    })
    const dispatch = useDispatch<AppDispatch>();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;

        setLoginForm((prev) => ({
            ...prev,
            [id]: value, // id는 input의 id 속성 (id 또는 password)
        }));
    };


    // 로그인 요청하는 함수
    const requestLogin = async () => {

        try {
            const response = await axios.post(
                `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/auth/login`,
                loginForm);

            const status_code :number = response.status
            console.log("test",status_code)
            if (status_code === 417) {
                alert(response.data.message);
                return; //실패시 함수 종료
            }
            const data = response.data as LoginResponse;

            // 서버에서 토큰을 받았다면 Redux 상태에 저장
            if (status_code ===200 &&data.accessToken && data.refreshToken) {
                dispatch(setTokens({ accessToken: data.accessToken, auth_level: data.auth_level}));
                dispatch(setMember(loginForm.id))
                // console.log("Redux State after login:", store.getState());

                setCookie('refresh_token', data.refreshToken); // Refresh Token도 저장

                // 쿠키 저장 후 리다이렉트
                setTimeout(() => {
                    router.push('/my-portfolio');
                }, 100); // 쿠키 저장이 반영될 때까지 대기
            }
            else {
                alert('Invalid login credentials');
            }

        }  catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response && error.response.status === 423) {
                    const errorMessage = error.response.data?.message || "계정이 잠겨있습니다.";
                    alert(errorMessage);
                } else {
                    console.error("Login error:", error);
                    alert("로그인 요청 중 문제가 발생했습니다.");
                }
            } else {
                console.error("Unexpected error:", error);
                alert("예기치 못한 오류가 발생했습니다. 다시 시도하세요.");
            }
        }
    };

    const handleFormSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault(); // 기본 제출 동작 방지
        requestLogin(); // 로그인 요청
    };

    return (
        <div className="flex flex-col min-h-screen">
            <a
                href="/"
                className="cursor-pointer flex items-center space-x-3 rtl:space-x-reverse mt-2 mx-2"
            >
                <Image
                    // src={dark ? "/stocker_log.png" : "/stocker_log.png"}
                    src={"/stocker_log.png"}
                    className="h-2"
                    alt="stocker_logo"
                    width={180}
                    height={60}
                    style={{ width: "auto", height: "auto" }}
                    priority
                />
            </a>
            <div className="flex flex-1">
                {/* Login Form */}
                <div className="flex-1 flex items-center justify-center bg-gray-100">
                    <div className="w-full max-w-md bg-white shadow-lg rounded-lg p-8">
                        <h2 className="text-3xl font-bold text-center text-gray-800 mb-6">Login</h2>
                        <form onSubmit={handleFormSubmit}>
                            <div className="mb-4">
                                <label htmlFor="id" className="block text-gray-700 font-medium mb-2">
                                    ID
                                </label>
                                <input
                                    type="id"
                                    id="id"
                                    value={loginForm.id}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="ID"
                                />
                            </div>
                            <div className="mb-4">
                                <label htmlFor="password" className="block text-gray-700 font-medium mb-2">
                                    비밀번호
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    value={loginForm.password}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="비밀번호 입력"
                                />
                            </div>
                            <div className="flex items-center justify-between mb-6">
                                <button
                                    type="submit"
                                    onClick={requestLogin}
                                    className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    Login
                                </button>
                            </div>
                        </form>
                        <p className="text-gray-600 text-center">
                            계정이 없으십니까?  {'   '}
                            <button
                                onClick={() => router.push('/sign-up')} // 회원가입 페이지로 이동
                                className="text-blue-600 hover:underline"
                            >회원가입
                            </button>
                        </p>
                    </div>
                </div>
            </div>

            {/* Footer */}
            <footer className="bg-gray-800 text-white py-4">
                <div className="text-center">
                    <p className="text-sm">&copy; 2024 jyg. All rights reserved.</p>
                    <a href="#" className="text-blue-400 hover:underline">Contact Us : seamus-1@naver.com</a>
                </div>
            </footer>
        </div>
    );
};

export default LoginPage;
