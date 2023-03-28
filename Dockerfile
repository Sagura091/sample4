FROM openjdk:8-alpine

COPY target/uberjar/sample4.jar /sample4/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/sample4/app.jar"]
