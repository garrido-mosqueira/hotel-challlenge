FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY ./price-api/target/price-api-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
