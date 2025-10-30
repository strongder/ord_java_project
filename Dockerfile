# =========================
# Stage 1: Build ứng dụng
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy file cấu hình và dependencies trước
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source và build
COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy file jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Expose port (ví dụ 8080)
EXPOSE 8080

# Biến môi trường (tùy chọn)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
