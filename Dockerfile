# ============ Build Stage ============
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# MVNW 우선 복사 → 캐싱
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:resolve -DskipTests

COPY src src

RUN ./mvnw clean package -DskipTests


# ============ Run Stage ============
FROM eclipse-temurin:21-jre

WORKDIR /app

EXPOSE 8080

# JAR만 가져옴
COPY --from=build /app/target/*.jar app.jar

# 보안: non-root 실행
RUN useradd -u 1001 spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]
