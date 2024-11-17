import type {Config} from "tailwindcss";

const config : Config = {
    content: [
        "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
        './app/**/*.{js,ts,jsx,tsx,mdx}'

    ],
    theme: {
        extend: {
            // colors: {
            //     background: "var(--background)",
            //     foreground: "var(--foreground)",
            // },
            colors: {
                customBg: "#000000", // 사용자 정의 색상
            },
        },
    },
    plugins: [],
};
/* eslint-disable import/no-unused-modules */
export default config;
// satisfies
// Config;
