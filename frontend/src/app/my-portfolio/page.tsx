"use client";
import React, {useState} from 'react';
import Sidebar from "../components/SideBar";
import  {RootState} from "../store/store";
import {useSelector} from "react-redux";
import axiosInstance from "../utils/axiosInstance";
import SavePopup from "./SavePopup";



const MyPortfolio = () => {


    const [searchResults, setSearchResults] = useState<any[]>([]);
    // Axios를 사용해서 getData
    const favoriteStocks = ['Stock 1', 'Stock 2', 'Stock 3', 'Stock 4', 'Stock 5'];
    const [symbol, setSymbol] = useState<string>('');
    const [isPopupOpen, setIsPopupOpen] = useState(false); // 팝업 열림 상태 관리


    const handleSearch = async () => {
        if (!symbol.trim()) {
            console.log("test",symbol)
            alert("Please enter a search term.");
            return;
        }
        try {
            const response = await axiosInstance.get(
                `/api/portfolio/search/${symbol}`, // 백엔드 URL
            );
            const status_code = response.status
            if (status_code === 200){
                setIsPopupOpen(true);
            }
            // setSearchResults(response.data);
        } catch (error) {
            console.error("Error fetching search results:", error);
            alert("Failed to fetch search results. Please try again.");
        }
    };

    const handleSave = () => {
        // 저장 로직 실행
        console.log("Saving results:", searchResults);
        setIsPopupOpen(false); // 팝업 닫기
    };

    const handleCancel = () => {
        // 저장하지 않음
        console.log("Save canceled.");
        setIsPopupOpen(false); // 팝업 닫기
    };





    return (
        <div className="flex text-black">

            {/* FavoriteSidebar에 리스트 데이터를 전달 */}
            <Sidebar items={favoriteStocks}/>


            {/* 메인 콘텐츠 ml-20 고정 -> sidebar 길이 변경*/}
            <div className="ml-10 p-4 w-full">
                <div className="p-4 bg-gray-100 border-b border-gray-300">
                    <h1 className="text-xl text-black font-bold mb-4">Search Stocks</h1>
                    <div className="flex items-center space-x-4">
                        <input
                            type="text"
                            value={symbol}
                            onChange={(e) => setSymbol(e.target.value)}
                            placeholder="Enter stock name or symbol"
                            className="flex-1 text-black px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            onClick={handleSearch}
                            className="px-4 py-2 bg-blue-600 text-black rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            Search
                        </button>
                    </div>
                </div>
                <div className={"mt-4"}>
                    <h1 className="text-2xl font-bold">Graph Content</h1>
                    <p>Here is the main content of the page.</p>
                </div>
            </div>

            {/* 저장 팝업 */}
            <SavePopup
                isOpen={isPopupOpen}
                onClose={handleCancel}
                onConfirm={handleSave}
                message="주식 검색 결과를 저장하시겠습니까?"
            />
        </div>
    );

};

export default MyPortfolio;