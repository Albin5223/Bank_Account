FROM eclipse-temurin:11-alpine

ARG VERSION

WORKDIR /app
COPY target/bank-account-${version}.jar bank-account-${version}.jar

EXPOSE 8080
CMD ["java", "-jar", "bank-account-${version}.jar"]