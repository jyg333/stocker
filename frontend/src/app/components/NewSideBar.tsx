import React, { useState } from 'react';
import axiosInstance from "../utils/axiosInstance";
import { FaCheck } from 'react-icons/fa';

const Sidebar = ({
                     items,
                     onSymbolSelect,
                     onDelete,
                 }: {
    items: { symbol: string; alStatus: boolean }[];
    onSymbolSelect: (symbol: string) => void;
    onDelete: (symbol: string) => void;
}) => {

    const [selectedSymbol, setSelectedSymbol] = useState<string | null>(null); // 선택된 Symbol 상태

    const handleSelect = (symbol: string) => {
        setSelectedSymbol(symbol); // 선택된 Symbol 업데이트
        onSymbolSelect(symbol); // 상위 콜백 호출
    };

    const handleDelete = async () => {
        if (!selectedSymbol) {
            alert("삭제할 Symbol을 선택하세요.");
            return;
        }

        try {
            // DELETE 요청 전송
            const response = await axiosInstance.delete(`/api/portfolio/delete/${selectedSymbol}`);
            console.log(response)
            if (response.status === 202) {
                alert("해당 종목이 성공적으로 삭제돼었습니다");
                onDelete(selectedSymbol); // 삭제된 Symbol로 상태 업데이트
                setSelectedSymbol(null); // 선택 초기화
            }
        } catch (error) {
            if (error.response) {
                // HTTP 응답이 있는 경우 상태 코드 확인
                const status = error.response.status;
                if (status === 400) {
                    alert("잘못된 요청입니다. 다시 시도하세요.");
                } else if (status === 404) {
                    alert("Symbol을 찾을 수 없습니다.");
                } else if (status === 401) {
                    alert("권한이 없습니다. 로그인 후 다시 시도하세요.");
                } else {
                    alert("알 수 없는 에러가 발생했습니다.");
                }
            } else {
                // 네트워크 에러 등
                console.error("Network or server error:", error.message);
                alert("네트워크 오류가 발생했습니다. 다시 시도하세요.");
            }
        }
    };


    return (
        <div className="sticky top-4 h-[66vh] bg-sky-300 text-white p-4 shadow-lg overflow-y-auto rounded-lg ml-4 mt-4 w-60">
            <h2 className="text-xl font-bold mb-4">Stock List</h2>

            <ul className="space-y-2">
                {items.map((item, index) => (
                    <li
                        key={index}
                        className={`p-2 rounded cursor-pointer flex items-center justify-between ${
                            selectedSymbol === item.symbol ? 'bg-sky-500 font-bold' : 'hover:bg-sky-400'
                        }`}
                        onClick={() => handleSelect(item.symbol)}
                    >
                        <span>{item.symbol}</span>
                        <span>
                {item.alStatus ? (
                    <FaCheck className="text-green-500" />
                ) : (""
                    // <FaTimes className="text-red-500" />
                )}
            </span>
                    </li>
                ))}
            </ul>

            <button
                className="mt-4 bg-red-300 hover:bg-red-600 text-white p-2 rounded"
                onClick={handleDelete} // 삭제 핸들러
            >
                Delete
            </button>
        </div>
    );
};

export default Sidebar;
