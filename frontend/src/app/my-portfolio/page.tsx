"use client";
import React, {useEffect, useState} from 'react';
import Sidebar from "../components/SideBar";
import axiosInstance from "../utils/axiosInstance";
import SavePopup from "./SavePopup";



const MyPortfolio = () => {



    // Axios를 사용해서 getData
    const [favoriteStocks, setFavoriteStocks] = useState<string[]>([]); // 즐겨찾기 목록 상태
    const [symbol, setSymbol] = useState<string>('');
    const [isPopupOpen, setIsPopupOpen] = useState(false); // 팝업 열림 상태 관리
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태

    useEffect(() => {
        const fetchFavoriteStocks = async () => {
            try {
                setIsLoading(true); // 로딩 시작
                const response = await axiosInstance.get(`/api/portfolio/get-favorite`);
                setFavoriteStocks(response.data); // 데이터 설정
            } catch (error) {
                console.error("Failed to fetch favorite stocks:", error);
                alert("즐겨찾기 데이터를 가져오는 데 실패했습니다.");
            } finally {
                setIsLoading(false); // 로딩 종료
            }
        };

        fetchFavoriteStocks();
    }, []);

    const handleSearch = async () => {
        if (!symbol.trim()) {

            alert("Please enter a search term.");
            return;
        }
        try {
            const response = await axiosInstance.get(
                `/api/portfolio/search/${symbol}`, // 백엔드 URL
            );
            console.log(response.data.symbol)
            const status_code = response.status
            if (status_code === 202){
                setIsPopupOpen(true);
            }

        } catch (error) {
            console.error("Error fetching search results:", error);
            alert("Failed to fetch search results. Please try again.");
        }
    };

    const handleSave = async () => {
        // 저장 로직 실행
        console.log("Saving results:", symbol);
        try {
            const response = await axiosInstance.post(
                `/api/portfolio/add-favorite`, // 백엔드 URL
                {symbol}
            );

            const status_code = response.status
            if (status_code === 202) {
                setIsPopupOpen(false);
                alert("즐겨찾기 등록 성공!")
            } else if (status_code === 208) {
                setIsPopupOpen(false);
                alert("중복된 종목이 즐겨찾기에 있습니다!")
            }
            // setSearchResults(response.data);
        } catch (error) {
            console.error("Error fetching search results:", error);
            alert("Failed to fetch search results. Please try again.");
        }
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
                    <h1 className="text-xl text-black font-bold mb-4">주식 검색</h1>
                    <div className="flex items-center space-x-4">
                        <input
                            type="text"
                            value={symbol}
                            onChange={(e) => setSymbol(e.target.value)}
                            placeholder="주식 심볼을 입력하세요 (e.g. Apple :  AAPL)"
                            className="flex-1 text-black px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            onClick={handleSearch}
                            className="px-4 py-2 bg-blue-600 text-black rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            검색
                        </button>
                    </div>
                </div>
                <div className={"mt-4"}>
                    <h1 className="text-2xl font-bold">Graph Content</h1>
                    <p>Here is the main content of the page.</p>
                </div>
            </div>
            {/* 로딩 상태 표시 */}
            <div className="mt-4">
                {isLoading ? (
                    <p>Loading your favorite stocks...</p>
                ) : (
                    <div>
                        <h2 className="text-lg font-bold">즐겨찾기 목록</h2>
                        {favoriteStocks.length > 0 ? (
                            <ul>
                                {favoriteStocks.map((stock, index) => (
                                    <li key={index} className="py-2">
                                        {stock}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>즐겨찾기 목록이 비어 있습니다.</p>
                        )}
                    </div>
                )}
            </div>

            {/* 저장 팝업 */}
            <SavePopup
                isOpen={isPopupOpen}
                onClose={handleCancel}
                onConfirm={handleSave}
                message="주식 검색 결과를 저장하시겠습니까?"
                symbol={symbol}
            />
        </div>
    );

};

export default MyPortfolio;