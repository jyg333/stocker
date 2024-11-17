import React from 'react';
import Sidebar from "../components/SideBar";

const AlgorithmTrading = () => {
    const favoriteStocks = ['Stock 1', 'Stock 2', 'Stock 3', 'Stock 4', 'Stock 5'];

    return (

        <div className={"flex"}>
            <Sidebar items={favoriteStocks}/>

            <div className={"ml-20 ml-64 p-8 w-full text-3xl font-bold underline text-red-700"}>
                Algorithm Trading test
            </div>
    </div>
    );
};

export default AlgorithmTrading;