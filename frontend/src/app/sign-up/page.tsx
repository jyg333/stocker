"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

const SignUpPage: React.FC = () => {
    const router = useRouter();
    const [form, setForm] = useState({
        id: "",
        password: "",
        confirmPassword: "",
        name: "",
        email: "",
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setForm((prev) => ({ ...prev, [id]: value }));
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        // 비밀번호 확인
        if (form.password !== form.confirmPassword) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        try {
            // 회원가입 요청
            const response = await axios.post(
                `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/auth/sign-up`,
                {
                    id: form.id,
                    password: form.password,
                    validPassword: form.confirmPassword,
                    name: form.name,
                    email: form.email,
                }
            );
            console.log(response.data)
            if (response.status === 201) {
                alert("회원가입이 완료되었습니다!\n가입하신 아이디로 로그인 해주세요");
                router.push("/"); // 로그인 페이지로 이동
            }
        } catch (error) {
            console.error(error);
            alert("회원가입에 실패했습니다.");
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md bg-white shadow-lg rounded-lg p-8">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-6">회원가입</h2>
                <form onSubmit={handleSubmit}>
                    {/* ID */}
                    <div className="mb-4">
                        <label htmlFor="id" className="block text-gray-700 font-medium mb-2">ID</label>
                        <input
                            type="text"
                            id="id"
                            value={form.id}
                            onChange={handleInputChange}
                            className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="ID를 입력하세요"
                        />
                    </div>

                    {/* Name */}
                    <div className="mb-4">
                        <label htmlFor="name" className="block text-gray-700 font-medium mb-2">이름</label>
                        <input
                            type="text"
                            id="name"
                            value={form.name}
                            onChange={handleInputChange}
                            className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="이름을 입력하세요"
                        />
                    </div>

                    {/* Email */}
                    <div className="mb-4">
                        <label htmlFor="email" className="block text-gray-700 font-medium mb-2">이메일</label>
                        <input
                            type="email"
                            id="email"
                            value={form.email}
                            onChange={handleInputChange}
                            className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="이메일을 입력하세요"
                        />
                    </div>

                    {/* Password */}
                    <div className="mb-4">
                        <label htmlFor="password" className="block text-gray-700 font-medium mb-2">비밀번호</label>
                        <input
                            type="password"
                            id="password"
                            value={form.password}
                            onChange={handleInputChange}
                            className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="비밀번호를 입력하세요"
                        />
                    </div>

                    {/* Confirm Password */}
                    <div className="mb-4">
                        <label htmlFor="confirmPassword" className="block text-gray-700 font-medium mb-2">비밀번호 확인</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            value={form.confirmPassword}
                            onChange={handleInputChange}
                            className="w-full px-4 py-2 text-black border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="비밀번호를 다시 입력하세요"
                        />
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        회원가입
                    </button>
                </form>
            </div>
        </div>
    );
};

export default SignUpPage;
