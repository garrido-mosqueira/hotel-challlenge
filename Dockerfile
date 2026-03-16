FROM amazoncorretto:21-alpine
VOLUME /tmp
EXPOSE 8080
COPY ./hotel-api/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]