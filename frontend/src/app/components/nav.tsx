"use client";
import React from 'react';
import Link from "next/link";
import {useState} from "react";
import Image from "next/image";
import store from "../store/store";
import {clearTokens} from "../features/authSlice";
import Popup from "./Popup";
import {useSelector} from "react-redux";
interface AuthState {
    auth_level: string;
    member: string;
}
interface RootState {
    auth: AuthState;
}
const NavBar = () => {
    // const [dark, setDark] = useState<boolean>(false); // Initialize dark state to false
    const [isLogoutPopupOpen, setIsLogoutPopupOpen] = useState(false);


    // Redux에서 auth_level 가져오기
    const authLevel = useSelector((state: RootState) => state.auth.auth_level);
    const memberId = useSelector((state: RootState) => state.auth.member);
    // 권한에 따라 버튼 표시 여부 결정
    const canViewManagementButton = ["301", "201"].includes(authLevel);

    const handleLogout = () => {
        // 로그아웃 로직 호출
        console.log("Logging out...");
        deleteTokens();
    };
    const deleteTokens = () => {
        // 쿠키 삭제
        document.cookie = "refresh_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
        // Redux 상태 초기화
        store.dispatch(clearTokens());
        console.log("Tokens cleared.");
        // 홈으로 리다이렉트
        window.location.href = "/login";
    };

    return (
        <div className={"relative z-50"}>
            <div className="flex justify-between items-center mt-2 mx-2">
                {/* 로고 - 왼쪽 */}
                <a
                    href="/my-portfolio"
                    className="cursor-pointer flex items-center space-x-3 rtl:space-x-reverse"
                >
                    <Image
                        // src={dark ? "/stocker_log.png" : "/stocker_log.png"}
                        src={"/stocker_log.png" }
                        className="h-2"
                        alt="stocker_logo"
                        width={180}
                        height={60}
                        style={{ width: "auto", height: "auto" }}
                        priority
                    />
                </a>

                {/* 로그아웃 버튼 - 오른쪽 */}
                {/*<button*/}
                {/*    onClick={goToManagementUserPage}*/}
                {/*    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md"*/}
                {/*>*/}
                {/*    사용자 관리*/}
                {/*</button>*/}
                {/*<a className="text-xl font-bold text-black hover:text-white hover:bg-red-400 rounded-lg transition duration-300 px-4 py-2 cursor-pointer mr-2">*/}
                {/*    <button onClick={() => setIsLogoutPopupOpen(true)}>Logout</button>*/}
                {/*</a>*/}
                {/* 사용자 관리 버튼과 로그아웃 버튼 - 오른쪽 */}
                <div className="flex items-center space-x-4">
                    <span className="text-xl font-medium text-black">
                        {memberId}
                    </span>
                    {/* 사용자 관리 버튼: 권한이 맞는 경우에만 렌더링 */}
                    {canViewManagementButton && (
                        <a
                            href="/management-user"
                            className="text-xl font-bold text-black hover:text-white hover:bg-blue-400 rounded-lg transition duration-300 px-4 py-2 cursor-pointer"
                        >
                            사용자 관리
                        </a>
                    )}

                    <a
                        className="text-xl font-bold text-black hover:text-white hover:bg-red-400 rounded-lg transition duration-300 px-4 py-2 cursor-pointer"
                    >
                        <button onClick={() => setIsLogoutPopupOpen(true)}>Logout</button>
                    </a>
                </div>
            </div>
        <div className="bg-sky-100 h-16 rounded-lg flex items-center m-2 shadow-md">

            <ul className="w-full flex justify-around">
                <NavItem href="/my-portfolio" label="My Stock Portfolio" />
                <NavItem href="/algorithm-trading" label="Algorithm Trading" />
                <NavItem href={null} label={null}/>
                <NavItem href={null} label={null}/>
                <NavItem href={null} label={null}/>
                {/*<NavItem href="/about" label="About Us" />*/}
                {/*<NavItem href="/contact" label="Contact" />*/}
                {/* 필요한 메뉴를 계속 추가 가능 */}

            </ul>
        </div>
            {/* 로그아웃 팝업 제어를 logout Popup에 전달*/}
            {isLogoutPopupOpen && (
                <Popup
                    onConfirm={() => {
                        setIsLogoutPopupOpen(false);
                        handleLogout();
                    }}
                    onCancel={() => setIsLogoutPopupOpen(false)}
                />
            )}

        </div>

    );
};

// 개별 Nav 아이템 컴포넌트
const NavItem = ({ href, label }: { href: string | null; label: string | null }) => {
    if (!href || !label) return (
        <li className="text-black hover:text-white hover:bg-emerald-500 rounded-lg transition duration-300 py-2">

        </li>
    );
    return (
        <li className="text-xl font-bold text-black hover:text-white hover:bg-sky-300 rounded-lg transition duration-300 px-4 py-2">
            <Link href={href}>{label}</Link>
        </li>
    );
};

export default NavBar;
