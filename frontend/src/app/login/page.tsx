"use client";

import React, { useState } from 'react';
import axios from "axios";
import Image from "next/image";

const LoginPage: React.FC = () => {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');

    const requestLogin = async (form_data: FormData) => {
        // 로그인 요청하는 함수
        try {
            const response = await axios.post(
                `${process.env.BACKEND_URL}/auth/login`,
                form_data);
            // ).then((res: any) => {
            if (response.data.status_code === 417) {
                alert(response.data.message);
            }


            const data: any = response.data;
            setBtnClickable(true);
            return data;
            // });

        } catch (error) {
            console.error(error);
            setBtnClickable(true);
            return null;
        }
    };
    const handleLogin = () => {
        console.log('Email:', id);
        console.log('Password:', password);
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
                                    id="email"
                                    value={id}
                                    onChange={(e) => setId(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
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
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Enter your password"
                                />
                            </div>
                            <div className="flex items-center justify-between mb-6">
                                <button
                                    type="button"
                                    onClick={handleLogin}
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
