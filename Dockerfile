FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
COPY --from=build /target/*.jar recipemanager.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","recipemanager.jar"]