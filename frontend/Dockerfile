# Node.js 20.10.0 이미지 사용
FROM node:20.10.0-alpine3.18

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package.json package-lock.json /app/

# 의존성 설치
RUN npm install

# tzdata 설치
RUN apk add --no-cache tzdata

# 소스 파일 복사
COPY ./ /app

# .env.production 파일 복사
COPY .env.production /app/.env.production

# 로컬 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo "Asia/Seoul" > /etc/timezone

# Next.js 애플리케이션 빌드 시 .env.production 사용
ENV NODE_ENV=production
RUN npm run build

# 컨테이너 시작 시 실행 명령은 docker-compose.yml에서 정의
