"use client";

import React, { useState, useEffect } from "react";
import axiosInstance from "../utils/axiosInstance";

interface User {
    id: string;
    name: string;
    activation: boolean;
    failCount: number;
    fail_dt: string | null;
    join_dt: string | null;
    join_ip: string | null;
    create_dt: string;
    created_by: string;
    updated_dt: string | null;
    updated_by: string | null;
    roles: string[];
}

const UserManagementPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [editUserId, setEditUserId] = useState<string | null>(null);
    const [editedData, setEditedData] = useState<Partial<User>>({});

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const response = await axiosInstance.get<User[]>("/api/member/v2/member-list");
                setUsers(response.data);
            } catch (error) {
                console.log(error)
                alert("사용자 데이터를 가져오는 데 실패했습니다.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, []);

    const updateUser = async (id: string, updatedData: Partial<User>) => {
        try {
            const response = await axiosInstance.put(`/api/member/update/${id}`, updatedData);
            if (response.status === 200) {
                alert("사용자 정보가 성공적으로 업데이트되었습니다.");
                setUsers((prevUsers) =>
                    prevUsers.map((user) => (user.id === id ? { ...user, ...updatedData } : user))
                );
            }
        } catch (error) {
            console.log(error)

            alert("사용자 정보를 업데이트하는 데 실패했습니다.");
        }
    };

    const deleteUser = async (id: string) => {
        try {
            const response = await axiosInstance.delete(`/api/member/${id}`, {
                data: { command: "delete" },
            });
            if (response.status === 200) {
                alert("사용자가 성공적으로 삭제되었습니다.");
                setUsers((prevUsers) => prevUsers.filter((user) => user.id !== id));
            }
        } catch (error) {
            console.log(error)

            alert("사용자를 삭제하는 데 실패했습니다.");
        }
    };

    const formatDateTime = (dateTime: string | null): string => {
        return dateTime?.split(".")[0].replace("T", " ") || "-";
    };

    const handleEditClick = (user: User) => {
        setEditUserId(user.id);
        setEditedData({
            id:user.id,
            name: user.name,
            failCount: user.failCount,
            activation: user.activation,
        });
    };

    const handleSaveEdit = async () => {
        if (editUserId) {
            await updateUser(editUserId, editedData);
            setEditUserId(null);
        }
    };

    const handleChange = (field: keyof User, value: string | number | boolean) => {
        setEditedData((prev) => ({
            ...prev,
            [field]: value,
        }));
    };
    const formatRole = (roles: string[]): string => {
        return roles
            .map((role) =>
                role
                    .replace("ROLE_ADMIN", "관리자")
                    .replace("ROLE_USER", "사용자")
            )
            .join(", "); // 배열을 쉼표로 구분된 문자열로 변환
    };

    return (
        <div className="p-8 text-black">
            <h1 className="text-2xl font-bold mb-6">사용자 관리</h1>
            {isLoading ? (
                <p>로딩 중...</p>
            ) : (
                <table className="w-full border-collapse border border-gray-300">
                    <thead>
                    <tr className="bg-gray-100">
                        <th className="border border-gray-300 px-4 py-2">ID</th>
                        <th className="border border-gray-300 px-4 py-2">이름</th>
                        <th className="border border-gray-300 px-4 py-2">로그인 실패</th>
                        <th className="border border-gray-300 px-4 py-2">활성화</th>
                        <th className="border border-gray-300 px-4 py-2">생성 날짜</th>
                        <th className="border border-gray-300 px-4 py-2">권한</th>

                        <th className="border border-gray-300 px-4 py-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user) => (
                        <tr key={user.id} className="text-center">
                            <td className="border border-gray-300 px-4 py-2">{user.id}</td>
                            <td className="border border-gray-300 px-4 py-2">
                                {editUserId === user.id ? (
                                    <input
                                        type="text"
                                        value={editedData.name || ""}
                                        onChange={(e) =>
                                            handleChange("name", e.target.value)
                                        }
                                        className="border px-2 py-1"
                                    />
                                ) : (
                                    user.name
                                )}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {editUserId === user.id ? (
                                    <input
                                        type="number"
                                        value={editedData.failCount || 0}
                                        onChange={(e) =>
                                            handleChange("failCount", Number(e.target.value))
                                        }
                                        className="border px-2 py-1"
                                    />
                                ) : (
                                    user.failCount
                                )}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {editUserId === user.id ? (
                                    <select
                                        value={editedData.activation ? "true" : "false"}
                                        onChange={(e) =>
                                            handleChange(
                                                "activation",
                                                e.target.value === "true"
                                            )
                                        }
                                        className="border px-2 py-1"
                                    >
                                        <option value="true">활성화</option>
                                        <option value="false">비활성화</option>
                                    </select>
                                ) : (
                                    user.activation ? "활성화" : "비활성화"
                                )}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {formatDateTime(user.create_dt)}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {formatRole(user.roles)}
                            </td>
                            <td className="border border-gray-300 px-4 py-2 space-x-2">
                                {editUserId === user.id ? (
                                    <button
                                        onClick={handleSaveEdit}
                                        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                                    >
                                        저장
                                    </button>
                                ) : (
                                    <button
                                        onClick={() => handleEditClick(user)}
                                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                    >
                                        수정
                                    </button>
                                )}
                                <button
                                    onClick={() => deleteUser(user.id)}
                                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                >
                                    삭제
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default UserManagementPage;
