"use client";
import React from 'react';
import Sidebar from "../components/SideBar";
import {RootState} from "../store/store";
import {useSelector} from "react-redux";
import {useEffect} from "react";



const MyPortfolio = () => {

    // Axios를 사용해서 getData
    const favoriteStocks = ['Stock 1', 'Stock 2', 'Stock 3', 'Stock 4', 'Stock 5'];
    // const accessToken = useSelector((state: RootState) => state.auth.accessToken);

    // useEffect(() =>console.log(accessToken),[])
    // React.useEffect(() => {
    //     console.log('Access Token in Redux:', accessToken);
    // }, [accessToken]);
    return (
        <div className="flex">
            {/* FavoriteSidebar에 리스트 데이터를 전달 */}
            <Sidebar items={favoriteStocks}/>

            {/* 메인 콘텐츠 ml-20 고정 -> sidebar 길이 변경*/}
            <div className="ml-20 p-8 w-full">
                <h1 className="text-2xl font-bold">Main Content</h1>
                <p>Here is the main content of the page.</p>
            </div>
        </div>
    );

};

export default MyPortfolio;