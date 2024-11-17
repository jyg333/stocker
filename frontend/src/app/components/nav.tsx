"use client";

import Link from "next/link";
import {useState} from "react";
import Image from "next/image";

const NavBar = () => {
    const [dark, setDark] = useState<boolean>(false); // Initialize dark state to false

    return (
        <div>
        <a
            href="/"
            className="cursor-pointer flex items-center space-x-3 rtl:space-x-reverse mt-2 mx-2"
        >
            <Image
                src={dark ? "/stocker_log.png" : "/stocker_log.png"}
                className="h-2"
                alt="stocker_logo"
                width={180}
                height={60}
                style={{ width: "auto", height: "auto" }}
                priority
            />
        </a>
        <div className="bg-sky-100 h-16 rounded-lg flex items-center m-2 shadow-md">

            <ul className="w-full flex justify-around">
                <NavItem href="/my-portfolio" label="My Portfolio" />
                <NavItem href="/algorithm-trading" label="Algorithm Trading" />
                <NavItem href={null} label={null}/>
                <NavItem href={null} label={null}/>
                <NavItem href={null} label={null}/>
                {/*<NavItem href="/about" label="About Us" />*/}
                {/*<NavItem href="/contact" label="Contact" />*/}
                {/* 필요한 메뉴를 계속 추가 가능 */}
            </ul>
        </div>
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
