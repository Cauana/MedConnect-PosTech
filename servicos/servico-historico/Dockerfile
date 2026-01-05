# Etapa 1 — Compilar o projeto
FROM maven:3.9.11-amazoncorretto-21 AS build
WORKDIR /workspace

# 1. Copia o POM pai
COPY pom.xml .

# 2. Copia TODOS os arquivos pom.xml do projeto para satisfazer o Maven
COPY servicos/autenticacao/pom.xml servicos/autenticacao/
COPY servicos/servico-agendamento/pom.xml servicos/servico-agendamento/
COPY servicos/servico-notificacao/pom.xml servicos/servico-notificacao/
COPY servicos/servico-historico/pom.xml servicos/servico-historico/

# 3. Instala o POM pai e baixa dependências
RUN mvn install -N -DskipTests

# 4. Copia o código-fonte do módulo
COPY servicos/servico-historico/src servicos/servico-historico/src

# 5. Compila e empacota
RUN mvn clean package -pl servicos/servico-historico -am -DskipTests

# 6. Etapa de runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

COPY --from=build /workspace/servicos/servico-historico/target/*.jar app.jar

RUN rm -f app.jar.original || true

ENTRYPOINT ["java", "-jar", "app.jar"]
