"use client"

import React, {useEffect, useState} from 'react';
import Chart from "react-apexcharts";
import Sidebar from "../components/NewSideBar";
import axiosInstance from "../utils/axiosInstance";
import chartOptions from "../components/chartOptions2";
import UpdateStatusPopup from './UpdateStatusPopup';
import {AxiosError} from "axios";

interface ChartDataItem {
    name: string;
    data: { x: number; y: number }[];
}
interface TradeResult {
    tradeDate: string;
    profitLoss: number | null;
}

const AlgorithmTrading = () => {

    // const [favoriteStocks, setFavoriteStocks] = useState<string[]>([]); // 즐겨찾기 목록 상태
    const [favoriteStocks, setFavoriteStocks] = useState<{ symbol: string; alStatus: boolean }[]>([]);

    const [symbol, setSymbol] = useState<string>('');
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태
    const [chartData, setChartData] = useState<ChartDataItem[]>([]);

    const [tradeAmountData, setTradeAmountData] = useState<{
        initAmount: number;
        curAmount: number;
        ratio: number;
    } | null>(null); // API에서 받은 거래 금액 정보

    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [selectedSymbol, setSelectedSymbol] = useState<string | null>(null);

    //update status popup
    const openPopup = (symbol: string) => {

        setSelectedSymbol(symbol);
        setIsPopupOpen(true);
    };
    const handleConfirm = () => {
        console.log('Symbol status updated');
        setIsPopupOpen(false); // 팝업 닫기
    };
    //get favorites
    useEffect(() => {
        const fetchFavoriteStocks = async () => {
            try {
                setIsLoading(true); // 로딩 시작
                const response = await axiosInstance.get(`/api/portfolio/get-al-favorite`);
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


    const handleDeleteSymbol = (symbol: string) => {
        setFavoriteStocks((prevStocks) => prevStocks.filter((stock) => stock.symbol !== symbol));

    };


    const handleSymbolSelect = async (selectedSymbol: string) => {
        const selectedStock = favoriteStocks.find((stock) => stock.symbol === selectedSymbol);
        if (selectedStock && !selectedStock.alStatus) {
            openPopup(selectedStock.symbol)
        }else {

            try {
                setIsLoading(true);
                setSymbol(selectedSymbol); // 현재 Symbol 설정

                const response = await axiosInstance.get(`/api/al-trade/trade-results?symbol=${selectedSymbol}`);
                const formattedData = [
                    {
                        name: selectedSymbol,
                        data: response.data.map((entry: TradeResult) => ({
                            // x: entry.tradeDate,
                            x: new Date(entry.tradeDate).getTime(),
                            y: entry.profitLoss !== null ? entry.profitLoss : 0, // null 값을 0으로 처리
                        })),
                    },
                ];
                // console.log("response : ",response)

                setChartData(formattedData); // 차트 데이터 업데이트

                // 거래 금액 데이터 가져오기
                const amountResponse = await axiosInstance.get(
                    `/api/al-trade/trade-amount?symbol=${selectedSymbol}`
                );
                setTradeAmountData(amountResponse.data);
            } catch (error) {
                const axiosError = error as AxiosError; // AxiosError로 단언
                if (axiosError.response) {
                    const statusCode = axiosError.response.status;
                    if (statusCode === 417) {
                        alert("알고리즘 매매 등록 에러");
                    }
                } else {
                    console.error("Error loading chart data:", error);
                    alert("차트 데이터를 가져오는 데 실패했습니다.");
                }
            } finally {
                setIsLoading(false);
            }
        }
    };

    const handleCancel = () => {
        setIsPopupOpen(false); // 팝업 닫기
    };
    return (

        <div className={"flex w-full" }>
            <div className={"w-1/4"}>
            <Sidebar
                items={favoriteStocks}
                onSymbolSelect={handleSymbolSelect} // 선택 시 호출될 함수
                onDelete={handleDeleteSymbol} // 삭제 핸들러 전달
            />
            {/* 거래 금액 정보 섹션 */}
            {tradeAmountData && (
                <div className="rounded-lg ml-4 mt-4 mb-4 w-60  mb-4 text-black">
                    <h2 className="text-xl font-semibold mb-2">{symbol} 거래 금액 정보</h2>
                    <p className="text-lg">시작금액: {tradeAmountData.initAmount.toLocaleString()} 원</p>
                    <p className="text-lg">현재금액: {tradeAmountData.curAmount.toLocaleString()} 원</p>
                    <p className="text-lg">
                        변화율:{" "}
                        <span
                            className={
                                tradeAmountData.ratio > 0
                                    ? "text-green-600"
                                    : tradeAmountData.ratio < 0
                                        ? "text-red-600"
                                        : "text-black"
                            }
                        >
                                {tradeAmountData.ratio.toFixed(2)}%
                            </span>
                    </p>
                </div>
            )}
            </div>

            <div className={"w-full pl-6 mr-10"}>
            <div className={"p-6 w-full text-3xl font-bold text-black"}>
                Algorithm Trade Info
            </div>


            {/* 차트 섹션 */}
            <div className=" w-full text-gray-800  p-4 rounded-md">
                <h2 className="text-lg font-bold mb-4">수익률 차트</h2>
                {isLoading ? (
                    <p>로딩 중...</p>
                ) : (
                    <div className="bg-white shadow-md rounded-lg p-4">
                        {chartData.length > 0
                            ? chartData.map((data, index) => (
                                <div key={index} className=" p-4 rounded-md">
                                    <h3 className="text-center text-white font-semibold mb-2">{data.name}</h3>
                                    <Chart options={chartOptions} series={[data]} type="line" height={300} />
                                </div>
                            ))
                            : "선택된 종목이 없습니다"}
                    </div>
                )}
            </div>
            </div>
            {isPopupOpen && selectedSymbol && (
                <UpdateStatusPopup
                    symbol={selectedSymbol}
                    onConfirm={handleConfirm}
                    onCancel={handleCancel}
                />
            )}


    </div>
    );
};

export default AlgorithmTrading;