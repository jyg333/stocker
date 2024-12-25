import React, { useState } from 'react';
import axiosInstance from "../utils/axiosInstance";
import { FaCheck } from 'react-icons/fa';
import axios from "axios";
// import {symbol} from "prop-types";

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
            console.log("symbol : ",selectedSymbol)
            const response = await axiosInstance.post(`/api/al-trade/delete/trading`,{
                symbol : selectedSymbol
            });
            // console.log(response)
            if (response.status ===     202) {
                alert("해당 종목이 성공적으로 삭제돼었습니다");
                onDelete(selectedSymbol); // 삭제된 Symbol로 상태 업데이트
                setSelectedSymbol(null); // 선택 초기화
            }
        } catch (error) {

            if (axios.isAxiosError(error)) {
                // AxiosError로 확인된 경우 처리
                if (error.response) {
                    const status = error.response.status;
                    if (status === 400) {
                        alert("잘못된 요청입니다. 다시 시도하세요.");
                    } else if (status === 406) {
                        alert("해당 Symbol 삭제 실패");
                    } else if (status === 401) {
                        alert("권한이 없습니다. 로그인 후 다시 시도하세요.");
                    } else {
                        alert("알 수 없는 에러가 발생했습니다.");
                    }
                } else {
                    alert("서버와의 통신 중 문제가 발생했습니다. 다시 시도하세요.");
                }
            } else {
                // AxiosError가 아닌 경우 처리
                console.error("Unknown error:", error);
                alert("예기치 못한 오류가 발생했습니다. 다시 시도하세요.");
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
