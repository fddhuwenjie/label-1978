FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-11 AS builder
WORKDIR /app
COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM tomcat:9-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
# 配置 Tomcat URI 编码为 UTF-8
RUN sed -i 's/Connector port="8080"/Connector port="8080" URIEncoding="UTF-8"/' /usr/local/tomcat/conf/server.xml
COPY --from=builder /app/target/student-api.war /usr/local/tomcat/webapps/ROOT.war
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
ENV LANG=C.UTF-8
EXPOSE 8080
CMD ["catalina.sh", "run"]
