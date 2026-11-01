# OdontoConnect

Protótipo de uma plataforma omnichannel de atendimento e ouvidoria odontológica.
Este repositório é um ambiente inicial de desenvolvimento e demonstração, com dados fictícios.

## Stack inicial

- Java 21
- Spring Boot 3.5.7 / Maven
- PostgreSQL 16
- React + TypeScript + Vite
- Docker Compose

## Pré-requisitos

Instale e verifique:

- JDK 21 (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Node.js 22 LTS e npm (`node -v` / `npm -v`)
- Docker Desktop com Docker Compose (`docker compose version`)
- IntelliJ IDEA para o backend
- VS Code para o frontend

## 1. Preparar variáveis locais

Na raiz do projeto, copie `.env.example` para `.env`. O backend importa esse arquivo
quando executado com `backend` como diretório de trabalho, e o Docker Compose também o utiliza.

PowerShell:

```powershell
Copy-Item .env.example .env
```

O Compose possui valores locais de fallback, então o banco também pode ser iniciado sem `.env`. Não use essas credenciais em produção.

## 2. Iniciar PostgreSQL

Na raiz:

```bash
docker compose up -d postgres
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f postgres
```

O banco ficará disponível em `localhost:5432`.

## 3. Executar backend no IntelliJ

1. Abra a pasta `backend` no IntelliJ IDEA.
2. Aguarde o Maven importar as dependências.
3. Confirme que o projeto está usando o JDK 21.
4. Execute `OdontoConnectApplication`.

Ou pelo terminal, dentro de `backend`:

```bash
mvn spring-boot:run
```

API: `http://localhost:8080`

Verificações:

- `GET http://localhost:8080/actuator/health`
- `GET http://localhost:8080/api/v1/status`

## 4. Executar frontend no VS Code

Abra a pasta `frontend` no VS Code e execute:

```bash
npm install
npm run dev
```

Acesse o endereço mostrado pelo Vite, normalmente `http://localhost:5173`.

O frontend inicial contém uma tela de boas-vindas e um indicador que consulta o endpoint de status do backend.

## 5. Parar o ambiente

```bash
docker compose down
```

Para apagar também os dados locais do PostgreSQL (ação destrutiva):

```bash
docker compose down -v
```

## Organização

- `backend/`: API Java.
- `frontend/`: aplicação web React.
- `docs/`: decisões e documentação do projeto.
- `docker-compose.yml`: serviços locais de desenvolvimento.

## Próximas etapas

1. Fundação e ambiente local (esta etapa).
2. Autenticação, usuários e perfis.
3. Cadastro de pacientes, setores e escalas.
4. Conversas, fila e histórico de atendimento.
5. Ouvidoria e protocolos.
6. Chat web e atualizações em tempo real.
7. Integrações externas, testes e preparação para piloto.

## 