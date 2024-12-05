"use client"
// import React, {useEffect, useState} from 'react';
import React, {useEffect, useState} from 'react';
import Chart from "react-apexcharts";
import Sidebar from "../components/SideBar";
import axiosInstance from "../utils/axiosInstance";
// import axios from 'axios';
const AlgorithmTrading = () => {
    // const favoriteStocks = ['Stock 1', 'Stock 2', 'Stock 3', 'Stock 4', 'Stock 5'];
    const [favoriteStocks, setFavoriteStocks] = useState<string[]>([]); // 즐겨찾기 목록 상태

    const [symbol, setSymbol] = useState<string>('');
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태
    const [chartData, setChartData] = useState<any[]>([]);

    //get favorites
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
    // Symbol 선택 시 데이터 로드
    // const handleSymbolSelect = async (selectedSymbol: string) => {
    //     try {
    //         setIsLoading(true); // 로딩 시작
    //         setSymbol(selectedSymbol); // 현재 선택된 Symbol 설정
    //
    //         const response = await axiosInstance.get(`/api/portfolio/chart-data/${selectedSymbol}`);
    //
    //         // 데이터를 ApexCharts 형식으로 변환
    //         const formattedData = response.data.map((stock) => ({
    //             name: stock.symbol,
    //             data: stock.prices.map((price) => ({ x: price.date, y: price.price })),
    //         }));
    //
    //         setChartData(formattedData); // 차트 데이터 업데이트
    //     } catch (error) {
    //         console.error("Error loading chart data:", error);
    //         alert("차트 데이터를 가져오는 데 실패했습니다.");
    //     } finally {
    //         setIsLoading(false); // 로딩 종료
    //     }
    // };

    const handleDeleteSymbol = (symbol: string) => {
        setFavoriteStocks((prevStocks) => prevStocks.filter((stock) => stock !== symbol));
    };
    // Apex 차트 옵션
    const chartOptions = {
        chart: { id: "portfolio-chart", zoom: { enabled: true } },
        xaxis: { type: "category", title: { text: "Trade Date" } },
        yaxis: { title: { text: "Profit/Loss" } },
        stroke: { curve: "smooth" },
    };

    // 빈 차트 데이터 생성
    const emptyChartData = [
        {
            name: "Empty Data",
            data: [{ x: "No Data", y: 0 }],
        },
    ];

    // 샘플 데이터를 임의로 생성
    // const generateSampleChartData = (symbol: string) => {
    //     const prices = Array.from({ length: 30 }, (_, i) => ({
    //         x: `Day ${i + 1}`,
    //         y: Math.floor(Math.random() * 100 + 50), // 50~150 범위의 임의 값
    //     }));
    //
    //     return {
    //         name: symbol,
    //         data: prices,
    //     };}
    // Symbol 선택 시 차트 데이터 로드
    const handleSymbolSelect = async (selectedSymbol: string) => {
        try {
            setIsLoading(true);
            setSymbol(selectedSymbol); // 현재 Symbol 설정

            const response = await axiosInstance.get(`/api/al-trade/trade-results?symbol=${selectedSymbol}`);
            const formattedData = [
                {
                    name: selectedSymbol,
                    data: response.data.map((entry: any) => ({
                        x: entry.tradeDate,
                        y: entry.profitLoss !== null ? entry.profitLoss : 0, // null 값을 0으로 처리
                    })),
                },
            ];

            setChartData(formattedData); // 차트 데이터 업데이트
        } catch (error) {
            console.error("Error loading chart data:", error);
            alert("차트 데이터를 가져오는 데 실패했습니다.");
        } finally {
            setIsLoading(false);
        }
    };
    return (

        <div className={"flex"}>
            <Sidebar
                items={favoriteStocks}
                onSymbolSelect={handleSymbolSelect} // 선택 시 호출될 함수
                onDelete={handleDeleteSymbol} // 삭제 핸들러 전달
            />
            <div className={"w-2/3 pl-6"}>
            <div className={"p-6 w-full text-3xl font-bold text-black"}>
                Algorithm Trading test
            </div>
            {/* 차트 섹션 */}
            <div className="bg-blue-800 p-4 rounded-md">
                <h2 className="text-lg font-bold mb-4">주식 차트</h2>
                {isLoading ? (
                    <p>로딩 중...</p>
                ) : (
                    <div className="">
                        {chartData.length > 0
                            ? chartData.map((data, index) => (
                                <div key={index} className="bg-gray-900 p-4 rounded-md">
                                    <h3 className="text-center text-white font-semibold mb-2">{data.name}</h3>
                                    <Chart options={chartOptions} series={[data]} type="line" height={300} />
                                </div>
                            ))
                            : "선택된 종목이 없습니다"}
                    </div>
                )}
            </div>
            </div>


    </div>
    );
};

export default AlgorithmTrading;