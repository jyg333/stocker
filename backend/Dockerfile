# Java 17 이미지 사용 (필요한 버전으로 교체)
FROM openjdk:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/app.jar

# 키 파일 복사
COPY /keys /app/keys

# db-config.properties 파일 복사
COPY configs/backend_conf/db-config.properties /app/config/db-config.properties
# 환경 변수 설정
ENV KEYS_PRIVATE=/app/keys/privateKey.pem
ENV KEYS_PUBLIC=/app/keys/publicKey.pem

# 컨테이너 실행 시 JAR 파일 실행
CMD ["java", "-jar", "/app/app.jar"]
