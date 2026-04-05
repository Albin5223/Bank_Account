FROM eclipse-temurin:11-alpine

ARG VERSION

WORKDIR /app
COPY target/bank-account-${VERSION}.jar bank-account-${VERSION}.jar

EXPOSE 8080
CMD ["java", "-jar", "bank-account-${VERSION}.jar"]