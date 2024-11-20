import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const clientIp = request.headers.get('x-forwarded-for') || request.url || 'Unknown IP';

    const response = NextResponse.next();
    response.headers.set('X-Client-IP', clientIp); // 클라이언트 IP를 응답 헤더에 추가
    return response;
}

export const config = {
    matcher: ['/'], // 원하는 경로에만 적용
};