import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const clientIp = request.headers.get('x-forwarded-for') || request.url || 'Unknown IP';

    const url = request.nextUrl.clone();
    const isLoggedIn = request.cookies.get('auth_token')?.value;


    const authToken = request.cookies.get('auth_token');

    //middleware Test
    // console.log(`[Middleware] Request received: ${request.method} ${request.nextUrl.pathname}`);
    // console.log(`[Middleware] Headers:`, request.headers);
    console.log(`[Middleware] Auth token: ${authToken ?? 'No token found'}`);

    if (!isLoggedIn) {
        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        url.pathname = '/login';
            return NextResponse.redirect(url);
    }



    const response = NextResponse.next();
    // response.headers.set('X-Client-IP', clientIp); // 클라이언트 IP를 응답 헤더에 추가
    // return response;
}

export const config = {
    matcher: ['/'], // 원하는 경로에만 적용
};