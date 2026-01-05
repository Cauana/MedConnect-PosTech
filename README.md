# MedConnect

MedConnect é um sistema de agendamento de consultas médicas desenvolvido com arquitetura de microsserviços. O sistema permite o cadastro de pacientes e médicos, agendamento de consultas, notificação assíncrona e consulta de histórico médico.

## 🏗️ Arquitetura

O projeto é dividido em microsserviços independentes, comunicando-se de forma síncrona (HTTP/REST/GraphQL) e assíncrona (Kafka).

### Serviços

| Serviço | Porta | Descrição | Tecnologia Principal |
|---------|-------|-----------|----------------------|
| **Autenticação** | 8080 | Gerencia usuários (Pacientes/Médicos/Enfermeiros) e emite tokens JWT. | Spring Security, JWT |
| **Agendamento** | 8081 | Realiza o agendamento de consultas e publica eventos. | Spring Web, Kafka Producer |
| **Notificação** | 8082 | Consome eventos de agendamento e simula envio de notificações. | Kafka Consumer |
| **Histórico** | 8083 | Fornece consulta flexível do histórico de pacientes e agenda de médicos. | Spring GraphQL |

### Infraestrutura
- **PostgreSQL**: Banco de dados relacional compartilhado (em ambiente dev) ou dedicado por serviço.
- **Apache Kafka**: Broker de mensagens para comunicação assíncrona entre Agendamento e Notificação.
- **Zookeeper**: Gerenciador para o Kafka.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security** (Autenticação/Autorização via JWT)
- **Spring Data JPA** (Persistência)
- **Spring GraphQL** (API de consultas)
- **Apache Kafka** (Mensageria)
- **PostgreSQL** (Banco de dados)
- **Docker & Docker Compose** (Orquestração de containers)
- **OpenAPI / Swagger** (Documentação da API)
- **Lombok** (Redução de boilerplate)

## 📋 Pré-requisitos

- **Java 21** ou superior
- **Maven 3.8+**
- **Docker** e **Docker Compose**

## ⚙️ Configuração e Execução

### Opção 1: Docker Compose (Recomendado)

Esta opção sobe todo o ambiente (Banco, Kafka, Zookeeper e os 4 serviços) automaticamente.

1. Na raiz do projeto, execute:
   ```bash
   docker-compose up --build
   ```
2. Aguarde todos os serviços iniciarem.
3. Configure o arquivo `.env` na raiz com:
   ```
   JWT_SECRET=<chave_base64_compartilhada_entre_todos_os_serviços>
   JWT_EXPIRATION=86400000
   ```
   O `docker-compose` carrega esse arquivo para Autenticação, Agendamento, Notificação e Histórico.

### Opção 2: Execução Local (Maven)

Caso prefira rodar os serviços individualmente:

1. Suba a infraestrutura (Postgres e Kafka):
   ```bash
   # Você pode usar o docker-compose apenas para a infra ou instalar localmente
   docker-compose up -d postgres kafka zookeeper
   ```
2. Em terminais separados, navegue até a pasta de cada serviço e execute:
   
   **Autenticação:**
   ```bash
   cd servicos/autenticacao
   mvn spring-boot:run
   ```
   
   **Agendamento:**
   ```bash
   cd servicos/servico-agendamento
   mvn spring-boot:run
   ```

   **Notificação:**
   ```bash
   cd servicos/servico-notificacao
   mvn spring-boot:run
   ```

   **Histórico:**
   ```bash
   cd servicos/servico-historico
   mvn spring-boot:run
   ```

## 📖 Documentação da API (Swagger)

Após iniciar os serviços, a documentação interativa está disponível em:

- **Autenticação**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Agendamento**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Histórico**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **Notificação**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

> **Nota**: Para utilizar os endpoints protegidos no Swagger, é necessário autenticar-se. Realize o login no serviço de Autenticação, copie o token JWT gerado e insira no botão "Authorize" (Value: `Bearer <seu_token>`) no Swagger dos outros serviços.

## 🔐 Níveis de Acesso

- Médicos: podem visualizar e editar o histórico de consultas (editar status em Agendamento).
- Enfermeiros: podem registrar consultas e acessar o histórico.
- Pacientes: podem visualizar apenas as suas consultas.
- Cadastro de usuários em Agendamento (`POST /usuarios`): somente ADMIN.

Detalhes técnicos:
- Em Agendamento, `GET /consultas` retorna apenas as consultas do paciente autenticado, quando a role é PACIENTE.
- Em Agendamento, `PUT /consultas/{id}/status` exige role MÉDICO.
- Em Histórico (GraphQL), PACIENTE só acessa o próprio histórico; MÉDICO e ENFERMEIRO podem consultar.

## 🧪 Testes com Postman

Uma collection do Postman está incluída no projeto para facilitar os testes.

1. Importe o arquivo `MedConnect_Collection.json` no Postman.
2. A collection está configurada com variáveis de ambiente automáticas.
   - Execute a requisição **Login** primeiro. O script de teste capturará o token JWT e o salvará automaticamente na variável `token`.
   - As requisições subsequentes (Agendamento, Histórico) usarão esse token automaticamente.

### Fluxo de Teste Sugerido:
1. **Registrar usuário** (Autenticação): Crie um novo usuário (Paciente, Médico, Enfermeiro). O endpoint retorna `{"id": ...}` do registro de login.
2. **Login** (Autenticação): Autentique-se com o usuário criado para obter o `token`.
3. **Cadastro de dados pessoais** (Agendamento → `POST /usuarios`): realizado pelo ADMIN. Use token de ADMIN e informe os dados pessoais; a resposta retorna `{"id": ...}` do usuário na tabela `usuarios`.
4. **Criar Consulta** (Agendamento → `POST /consultas`): 
   - PACIENTE: cria consulta apenas para si próprio; informe `idPaciente` retornado no passo 3 e um `idMedico` válido.
   - A criação valida existência e tipo de paciente e médico.
5. **Listar Consultas** (Agendamento → `GET /consultas`): 
   - PACIENTE: retorna apenas as suas consultas.
   - MÉDICO/ENFERMEIRO: acesso geral às consultas.
6. **Editar Status** (Agendamento → `PUT /consultas/{id}/status`): exige token de MÉDICO.
7. **Histórico Paciente (GraphQL)** (Histórico → `POST /graphql`): 
   - PACIENTE: consultar somente seu próprio histórico.
   - MÉDICO/ENFERMEIRO: acesso permitido conforme perfil.

## 🛠️ Dicas de Ambiente e Logs

- JWT: todos os serviços devem compartilhar o mesmo `JWT_SECRET` via `.env`.
- Logs:
  - Agendamento está configurado com `org.springframework.security=DEBUG` e logs de filtro JWT para diagnosticar autorização.
  - Hibernate está em `WARN` para reduzir ruído.

## 📦 Exemplos por Perfil

### ADMIN
- Cadastrar dados pessoais em Agendamento:

```bash
curl -X POST http://localhost:8081/usuarios \
  -H "Authorization: Bearer <token_admin>" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Wellington",
    "cpf": "22211122233",
    "email": "wellingtonfc95@gmail.com",
    "dataNascimento": "1990-01-01",
    "tipo": "PACIENTE"
  }'
```

Resposta:
```json
{ "id": 1 }
```

### PACIENTE
- Registrar login (Autenticação) e fazer login para obter `<token_paciente>`.
- Criar consulta (para si próprio):

```bash
curl -X POST http://localhost:8081/consultas \
  -H "Authorization: Bearer <token_paciente>" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idMedico": 2,
    "dataHora": "2026-01-10T10:00:00",
    "observacoes": "Dor de cabeça"
  }'
```

- Listar consultas (retorna apenas as do paciente autenticado):

```bash
curl -X GET http://localhost:8081/consultas \
  -H "Authorization: Bearer <token_paciente>"
```

- Histórico via GraphQL (somente do próprio paciente):

```bash
curl -X POST http://localhost:8083/graphql \
  -H "Authorization: Bearer <token_paciente>" \
  -H "Content-Type: application/json" \
  -d '{"query":"query { historicoPaciente(idPaciente: 1) { id dataHora status observacoes } }"}'
```

### MÉDICO
- Atualizar status de consulta:

```bash
curl -X PUT "http://localhost:8081/consultas/1/status?status=CONFIRMADA" \
  -H "Authorization: Bearer <token_medico>"
```

- Consultar histórico:

```bash
curl -X POST http://localhost:8083/graphql \
  -H "Authorization: Bearer <token_medico>" \
  -H "Content-Type: application/json" \
  -d '{"query":"query { consultasMedico(idMedico: 2) { id dataHora status observacoes } }"}'
```

### ENFERMEIRO
- Registrar consultas:

```bash
curl -X POST http://localhost:8081/consultas \
  -H "Authorization: Bearer <token_enfermeiro>" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idMedico": 2,
    "dataHora": "2026-01-10T11:00:00",
    "observacoes": "Rotina"
  }'
```

- Acessar histórico:

```bash
curl -X POST http://localhost:8083/graphql \
  -H "Authorization: Bearer <token_enfermeiro>" \
  -H "Content-Type: application/json" \
  -d '{"query":"query { consultasMedico(idMedico: 2) { id dataHora status observacoes } }"}'
```
