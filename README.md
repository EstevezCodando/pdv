# PDV System — Ponto de Venda

Sistema de gerenciamento de ponto de venda desenvolvido como trabalho prático (TP5) na disciplina de Projeto De Bloco

---

## Sobre

Aplicação web Spring Boot para gerenciamento de usuarios, produtos e registro de vendas, com controle de estoque integrado.

Funcionalidades:

- Cadastro e gerenciamento de usuarios com perfis (ADMIN, OPERADOR)
- Cadastro e controle de estoque de produtos
- Registro de vendas com baixa automática de estoque
- Dashboard com métricas consolidadas (faturamento, estoque, vendas)
- Autenticação com login/logout e remember-me
- API REST com autenticação HTTP Basic

---

## Pré-requisitos

- Java 21
- Gradle 8.12+ (ou use o wrapper `./gradlew`)
- Docker e Docker Compose (para PostgreSQL local)

---

## Configuração

### Perfil H2 (desenvolvimento — banco em memória)

Nenhuma configuração necessária. Os dados iniciais (usuarios e produtos) são criados automaticamente ao subir.

### Perfil PostgreSQL (produção)

1. Copie o arquivo de exemplo e preencha as credenciais reais:

   ```bash
   cp .env.example .env
   ```

2. Suba o banco PostgreSQL local:

   ```bash
   docker-compose up -d
   ```

3. Carregue as variáveis de ambiente antes de executar:
   ```bash
   export $(cat .env | xargs)
   ```

---

## Executando

### Com banco H2 (padrão para desenvolvimento)

```bash
./gradlew bootRun
```

Acesse: http://localhost:8080

### Com PostgreSQL

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

---

## Testes

### Testes unitários e de integração

```bash
./gradlew clean check
```

O relatório de cobertura JaCoCo é gerado em `build/reports/jacoco/test/html/index.html`.

### Testes Selenium (pós-deploy)

Requerem a aplicação rodando em `http://localhost:8080` e as variáveis de ambiente `PDV_ADMIN_USER` e `PDV_ADMIN_PASS`:

```bash
PDV_ADMIN_USER=admin@pdv.com PDV_ADMIN_PASS=admin123 ./gradlew seleniumTest
```

---

## CI/CD

### `ci.yml` — Integração Contínua

Executado em todo push e pull request para `main`:

1. Build e testes com Gradle
2. Relatório de cobertura JaCoCo (mínimo 85%)
3. OWASP Dependency Check (falha em CVEs com CVSS >= 9)
4. Upload de artefatos de relatório

### `cd.yml` — Entrega Contínua

Executado em push para `main` após CI passar:

1. Empacota o JAR versionado com SHA do commit
2. Deploy local/remoto via SSH
3. Smoke tests com autenticação HTTP Basic
4. Testes Selenium pós-deploy
5. ZAP DAST (baseline scan)

### Secrets necessários no GitHub

| Secret           | Descrição                                |
| ---------------- | ---------------------------------------- |
| `DB_URL`         | URL JDBC do banco PostgreSQL de produção |
| `DB_USERNAME`    | Usuário do banco                         |
| `DB_PASSWORD`    | Senha do banco                           |
| `DEPLOY_HOST`    | Host do servidor de deploy               |
| `DEPLOY_USER`    | Usuário SSH do servidor                  |
| `DEPLOY_SSH_KEY` | Chave privada SSH                        |
| `PDV_ADMIN_USER` | Email do admin para smoke/Selenium tests |
| `PDV_ADMIN_PASS` | Senha do admin para smoke/Selenium tests |

---

## Credenciais de desenvolvimento

Criadas automaticamente ao subir com o perfil `h2`:

| Email            | Senha    | Perfil   |
| ---------------- | -------- | -------- |
| admin@pdv.com    | admin123 | ADMIN    |
| operador@pdv.com | op123    | OPERADOR |
| maria@pdv.com    | maria123 | OPERADOR |

> **Atenção:** credenciais para TESTE ! não são para produção.

---

## Estrutura principal

```
src/main/java/com/pdv/pontovenda/
├── config/          # SecurityConfig, DataInitializer, filtros, listeners
├── controller/      # MVC controllers (web) e ApiControllers (REST)
├── dto/             # Request/Response e form DTOs
├── entity/          # Entidades JPA
├── exception/       # Exceções de negócio e handlers
├── repository/      # Spring Data JPA repositories
└── service/         # Lógica de negócio
```

## Execucao padronizada via Docker

Para garantir build e validacao da suite no mesmo ambiente, use a sequencia abaixo.

### 1. Build do projeto com Gradle em container

```bash
docker compose --profile build run --rm pdv-build
```

### 2. Testes unitarios e de integracao

```bash
docker compose --profile test run --rm pdv-test
```

### 3. Testes end-to-end com Selenium

```bash
docker compose --profile selenium run --rm pdv-selenium
```

### 4. Aplicacao publicada para validacao pos-deploy

```bash
docker compose up -d postgres pdv-app
docker compose --profile postdeploy run --rm pdv-postdeploy-test
```

Os containers de teste publicam logs customizados no inicio e no fim da execucao e exibem automaticamente os arquivos `build/reports/tests/resumo.txt` e `build/reports/tests/falhas_detalhadas.txt` quando existirem.
