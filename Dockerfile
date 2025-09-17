FROM maven:3.9.11-amazoncorretto-21-al2023 AS build

WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-al2023

WORKDIR /app

COPY --from=build /build/target/*.jar ./app.jar

EXPOSE 8080
EXPOSE 9090

ENV DATABASE_URL=jdbc:postgresql://localhost:5433/reservalab
ENV TZ=America/Sao_Paulo

ENTRYPOINT ["java", "-jar", "./app.jar"]