import type {NextConfig} from "next";

const nextConfig: NextConfig = {
    env: {
        NEXT_PUBLIC_BACKEND_URL: process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost',
    },
};

export default nextConfig;

// const nextConfig: NextConfig = {
//     /* config options here */
// };
//
// export default nextConfig;
