"use client";
import React, {useEffect, useState} from 'react';
import Sidebar from "../components/SideBar";
import axiosInstance from "../utils/axiosInstance";
import SavePopup from "./SavePopup";
import Chart from "react-apexcharts";
import Comment from "./Comment";
import {ApexOptions} from "apexcharts";

interface StockData {
    symbol: string;
    prices: { date: string; price: number }[];
}


interface ChartData {
    name: string;
    data: { x: string | number; y: number }[];
}
const MyPortfolio = () => {

    // Axios를 사용해서 getData
    const [favoriteStocks, setFavoriteStocks] = useState<string[]>([]); // 즐겨찾기 목록 상태
    const [symbol, setSymbol] = useState<string>('');
    const [isPopupOpen, setIsPopupOpen] = useState(false); // 팝업 열림 상태 관리
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태
    const [chartData, setChartData] = useState<ChartData[]>([]);


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

    // Symbol 선택 시 데이터 로드
    const handleSymbolSelect = async (selectedSymbol: string) => {
        try {
            setIsLoading(true); // 로딩 시작
            setSymbol(selectedSymbol); // 현재 선택된 Symbol 설정

            const response = await axiosInstance.get(`/api/portfolio/chart-data/${selectedSymbol}`);

            // 데이터를 ApexCharts 형식으로 변환
            const formattedData = response.data.map((stock :StockData) => ({
                name: stock.symbol,
                data: stock.prices.map((price) => ({ x: price.date, y: price.price })),
            }));

            setChartData(formattedData); // 차트 데이터 업데이트
        } catch (error) {
            console.error("Error loading chart data:", error);
            alert("차트 데이터를 가져오는 데 실패했습니다.");
        } finally {
            setIsLoading(false); // 로딩 종료
        }
    };

    const handleDeleteSymbol = (symbol: string) => {
        setFavoriteStocks((prevStocks) => prevStocks.filter((stock) => stock !== symbol));
    };


    // 차트 옵션 설정
    const chartOptions :ApexOptions= {
        chart: {
            id: "portfolio-chart",
            zoom: { enabled: true },
        },
        xaxis: { type: "category", title: { text: "Years" } },
        yaxis: { title: { text: "Price" } },
        tooltip: { shared: true, intersect: false },
        stroke: { curve: "smooth" }, // 변경된 부분
    };

    return (
        <div>
        <div className="flex text-black">

            {/* FavoriteSidebar에 리스트 데이터를 전달 */}
            <Sidebar
                items={favoriteStocks}
                onSymbolSelect={handleSymbolSelect} // 선택 시 호출될 함수
                onDelete={handleDeleteSymbol} // 삭제 핸들러 전달
            />


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
                    {/*<h1 className="text-2xl font-bold">Graph Content</h1>*/}
                    {/*<p>Here is the main content of the page.</p>*/}
                    {/* 차트 표시 */}
                    <div className="mt-4">
                        <h2 className="text-2xl font-bold mb-4">주식 차트</h2>
                        {isLoading ? (
                            <p>Loading charts...</p>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {chartData.map((data, index) => (
                                    <div key={index} className="bg-white shadow-md rounded-lg p-4">
                                        <h3 className="text-lg font-bold text-gray-800 mb-2">
                                            {data.name}
                                        </h3>
                                        <Chart
                                            options={chartOptions}
                                            series={[{ name: data.name, data: data.data }]}
                                            type="line"
                                            height={300}
                                        />
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>

                {/*/!* 댓글 컴포넌트 *!/*/}
                {/*{symbol && <Comment symbol={symbol} />}*/}

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
        <div className={"text-black px-4 pb-4"}>
            {/* 댓글 컴포넌트 */}
            {symbol && (
                <div className="w-full border-t border-gray-300 mt-4">
                    <Comment symbol={symbol} />
                </div>
            )}
        </div>
    </div>
    );

};

export default MyPortfolio;