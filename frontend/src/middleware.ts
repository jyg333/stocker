import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    // const clientIp = request.headers.get('x-forwarded-for') || request.url || 'Unknown IP';

    const url = request.nextUrl.clone();
    const isLoggedIn = request.cookies.get('refresh_token')?.value;


    //middleware Test
    // console.log(`[Middleware] Request received: ${request.method} ${request.nextUrl.pathname}`);
    // console.log(`[Middleware] Headers:`, request.headers);
    // console.log(`[Middleware] Auth token: ${authToken ?? 'No token found'}`);

    // 로그인한 사용자가 로그인 페이지로 이동하려고 하면 리다이렉트
    if (url.pathname === '/login' && isLoggedIn) {
        console.log("Redirecting logged-in user away from login page");
        url.pathname = '/my-portfolio'; // 로그인한 사용자의 기본 경로
        return NextResponse.redirect(url);
    }
    if (!isLoggedIn && url.pathname !== '/login') {
        console.log("Redirecting unauthenticated user to login page");
        url.pathname = '/login';
        return NextResponse.redirect(url);
    }

    // const response = NextResponse.next();
    // response.headers.set('X-Client-IP', clientIp); // 클라이언트 IP를 응답 헤더에 추가
    // return response;
}

export const config = {
    matcher: ['/','/login','/my-portfolio','/algorithm-trading'], // 원하는 경로에만 적용
};