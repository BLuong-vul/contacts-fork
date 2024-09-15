# build stage

FROM maven:3.9.9-eclipse-temurin-17 as build
COPY . /middleware_src
RUN mvn -f /middleware_src/pom.xml clean package -Dmaven.test.skip # suppress testing when building for deployment: envvar substitutions make tests throw errors

# running stage

FROM eclipse-temurin:17
COPY --from=build /middleware_src/target/*.jar application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
EXPOSE 8080
