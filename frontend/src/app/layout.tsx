"use client";
import localFont from "next/font/local";
// import { Inter } from "next/font/google";
import "./globals.css";
import { Provider } from "react-redux";
import store, {persistor} from "./store/store"; // Redux store 경로
import { PersistGate } from 'redux-persist/integration/react';
import Navbar from "./components/nav"
import {usePathname} from "next/navigation";


export default function RootLayout({children,}: Readonly<{
    children: React.ReactNode;
}>) {

    //For hide Navbar in Login page
    const pathname = usePathname();
    const hideNavbarRoutes = ['/login'];
    return (

        <html className="flex flex-col min-h-screen " >
        {/*<body className={inter.className}>*/}
        <body className="bg-sky-50 min-h-screen antialiased">
        <Provider store={store}>
            <PersistGate loading={null} persistor={persistor}>

            {!hideNavbarRoutes.includes(pathname) && <Navbar />}
            {children}
            </PersistGate>
        </Provider>
        </body>
        </html>

    );
}
