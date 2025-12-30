# Dockerfile para autenticacao - VERSÃO CORRIGIDA
FROM maven:3.9.11-amazoncorretto-21 AS build

# 1. Definir o diretório de trabalho na raiz do projeto dentro do container
WORKDIR /workspace

# 2. Copiar primeiro APENAS o POM pai e os POMs dos módulos para otimizar cache
COPY pom.xml .
COPY servicos/autenticacao/pom.xml servicos/autenticacao/

# 3. ETAPA CRÍTICA: Instalar o projeto pai no repositório local do container
#    O parâmetro -N (--non-recursive) garante que apenas o POM pai seja processado
RUN mvn install -N -DskipTests

# 4. Copiar o código-fonte do módulo autenticacao
COPY servicos/autenticacao/src servicos/autenticacao/src

# 5. Agora podemos compilar e empacotar apenas o módulo autenticacao
#    -pl = projeto específico, -am = também constrói dependências
RUN mvn clean package -pl servicos/autenticacao -am -DskipTests

# 6. Etapa de runtime
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /workspace/servicos/autenticacao/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]