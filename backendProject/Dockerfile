#사용할 베이스 이미지 명시 (공식 이미지 권장)
FROM openjdk:17-jdk

#작업 디렉토리 생성 및 설정(컨테이너 내부 경로에 시작점으로 지정 -> 없으면 생성 됨)
WORKDIR /app

#jar파일 등 빌드 결과물을 컨테이너에 복사 (호스트 -> 컨테이너)
COPY build/libs/backendProject-0.0.1-SNAPSHOT.jar /app/backendProject-0.0.1-SNAPSHOT.jar

#컨테이너 실행시 기본으로 실행할 명령
CMD ["java", "-jar", "/app/backendProject-0.0.1-SNAPSHOT.jar"]
