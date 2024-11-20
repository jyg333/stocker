


// src/app/api/get-ip/route.ts

import { NextRequest, NextResponse } from 'next/server';

// GET 메서드에 대한 named export
export async function GET(req: NextRequest) {
    console.log('Headers:');
    // req.headers.forEach((value, key) => {
    //     console.log(`${key}: ${value}`);
    // });

    const clientIp = req.headers.get('x-client-ip') || 'Unknown IP';
    return NextResponse.json({ clientIp });
}
