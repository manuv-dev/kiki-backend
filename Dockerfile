# === Stage 1: Build avec Maven ===
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier pom.xml et télécharger les dépendances (cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# === Stage 2: Image d'exécution légère ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier uniquement le JAR depuis le stage de build
COPY --from=build /app/target/api_kikitraiteur-0.0.1-SNAPSHOT.jar app.jar

# Port exposé (Render injecte $PORT)
EXPOSE 8080

# Démarrage de l'application
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
