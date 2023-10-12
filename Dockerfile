# 서버를 구동시킬 자바를 받아옵니다.
FROM openjdk:17

# `JAR_FILE` 이라는 이름으로 build 한 jar 파일을 지정합니다.
ARG JAR_FILE=./build/libs/p2k-0.0.1-SNAPSHOT.jar

# 지정한 jar 파일을 p2k.jar 라는 이름으로 Docker Container에 추가합니다.
ADD ${JAR_FILE} p2k.jar

# p2k.jar 파일을 실행합니다.
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/p2k.jar"]