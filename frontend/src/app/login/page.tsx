"use client";

import React, { useState } from 'react';
import axios from "axios";
import Image from "next/image";
import { AppDispatch } from '../store/store';
import { useDispatch } from 'react-redux';
import { setTokens } from '../features/authSlice';
import {setCookie} from '../utils/cookies'
import {useRouter} from "next/navigation";

type LoginFormType = {
    id: string,
    password :string;
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
        // console.log("handle input" ,id, value)
        setLoginForm((prev) => ({
            ...prev,
            [id]: value, // id는 input의 id 속성 (id 또는 password)
        }));
    };


    const requestLogin = async () => {
        // 로그인 요청하는 함수
        console.log("test :",loginForm)
        try {
            const response = await axios.post(
                `http://192.168.219.101:8080/api/auth/login`,
                loginForm);
            // ).then((res: any) => {
            if (response.data.status_code === 417) {
                alert(response.data.message);
            }
            const data: any = response.data;


            // 서버에서 토큰을 받았다면 Redux 상태에 저장
            if (data.accessToken && data.refreshToken) {
                dispatch(setTokens({ accessToken: data.accessToken}));
                console.log('Tokens saved in Redux:', data.accessToken, data.refreshToken);


                setCookie('refresh_token', data.refreshToken); // Refresh Token도 저장
                console.log('Tokens saved in Redux and Cookies (via cookies-next)');

                // router.push("/my-portfolio")
            } else {
                alert('Invalid login credentials');
            }
            console.log(data)

            // const data: any = response.data;
            // setBtnClickable(true);
            // return data;
            // // });

        } catch (error) {
            console.error(error);
            // setBtnClickable(true);
            return null;
        }
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
                        <form onSubmit={(e) => e.preventDefault()}>
                            <div className="mb-4">
                                <label htmlFor="email" className="block text-gray-700 font-medium mb-2">
                                    Email
                                </label>
                                <input
                                    type="email"
                                    id="id"
                                    value={loginForm.id}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Enter your email"
                                />
                            </div>
                            <div className="mb-4">
                                <label htmlFor="password" className="block text-gray-700 font-medium mb-2">
                                    Password
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    value={loginForm.password}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Enter your password"
                                />
                            </div>
                            <div className="flex items-center justify-between mb-6">
                                <button
                                    type="button"
                                    onClick={requestLogin}
                                    className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    Login
                                </button>
                            </div>
                        </form>
                        <p className="text-gray-600 text-center">
                            Don't have an account? <a href="#" className="text-blue-600 hover:underline">Sign up</a>
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
