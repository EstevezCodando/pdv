# PDV - Ponto de Venda

Sistema web de Ponto de Venda desenvolvido com **Java 21**, **Spring Boot 3.4**, **Thymeleaf** e **H2/MySQL**.
Inclui interface web responsiva com CRUD completo de Usuários e Produtos, além de suíte de testes
automatizados com **Selenium WebDriver**, **JUnit 5** e padrão **Page Object Model (POM)**.

---

## Pré-requisitos

| Ferramenta         | Versão mínima | Observação                                       |
|--------------------|---------------|--------------------------------------------------|
| **Java (JDK)**     | 21            | Obrigatório. Verifique com `java -version`       |
| **Maven**          | 3.9+          | Ou use o Maven Wrapper incluso (`./mvnw`)        |
| **Google Chrome**  | 120+          | Necessário apenas para testes Selenium            |
| **MySQL**          | 8.0+          | Apenas se for usar o perfil de produção           |

> **Nota:** Para desenvolvimento e testes, o banco **H2 em memória** é usado por padrão. Não é
> necessário instalar MySQL para rodar o projeto localmente.

---

## Estrutura do Projeto

```
pdv-system/
├── pom.xml                          # Configuração Maven com todas as dependências
├── mvnw / mvnw.cmd                  # Maven Wrapper (Linux/Mac e Windows)
├── .mvn/wrapper/                    # Configurações do Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/com/pdv/pontovenda/
│   │   │   ├── PontoDeVendaApplication.java    # Classe principal Spring Boot
│   │   │   ├── controller/                      # Controllers MVC (rotas web)
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   └── ProdutoController.java
│   │   │   ├── entity/                          # Entidades JPA (Usuario, Produto)
│   │   │   ├── exception/                       # Exceções customizadas
│   │   │   │   ├── RecursoNaoEncontradoException.java
│   │   │   │   └── RegraDeNegocioException.java
│   │   │   ├── repository/                      # Repositórios Spring Data JPA
│   │   │   └── service/                         # Camada de regras de negócio
│   │   └── resources/
│   │       ├── application.properties           # Configuração principal
│   │       ├── application-h2.properties        # Perfil H2 (desenvolvimento)
│   │       ├── application-mysql.properties     # Perfil MySQL (produção)
│   │       ├── data.sql                         # Dados iniciais (perfil H2)
│   │       └── templates/                       # Templates Thymeleaf
│   │           ├── fragments/layout.html        # Layout compartilhado
│   │           ├── index.html                   # Página inicial
│   │           ├── usuario/                     # Listagem e formulário de usuários
│   │           └── produto/                     # Listagem e formulário de produtos
│   └── test/
│       ├── java/com/pdv/pontovenda/
│       │   ├── page/                            # Page Objects (Selenium POM)
│       │   │   ├── BasePage.java
│       │   │   ├── UsuarioListagemPage.java
│       │   │   ├── UsuarioFormularioPage.java
│       │   │   ├── ProdutoListagemPage.java
│       │   │   └── ProdutoFormularioPage.java
│       │   └── test/                            # Classes de teste
│       │       ├── BaseSeleniumTest.java         # Base para testes Selenium
│       │       ├── UsuarioServiceTest.java       # Unitários - Service de Usuário
│       │       ├── ProdutoServiceTest.java       # Unitários - Service de Produto
│       │       ├── ExceptionTest.java            # Unitários - Exceções
│       │       ├── HomeControllerTest.java       # Integração - Home
│       │       ├── UsuarioControllerIntegrationTest.java  # Integração - Usuário
│       │       ├── ProdutoControllerIntegrationTest.java  # Integração - Produto
│       │       ├── UsuarioSeleniumTest.java      # Selenium E2E - Usuário
│       │       └── ProdutoSeleniumTest.java      # Selenium E2E - Produto
│       └── resources/
│           └── application-test.properties       # Configuração para testes
```

---

## Como Executar a Aplicação

### 1. Clonar/Descompactar o Projeto

```bash
cd pdv-system
```

### 2. Executar com Maven Wrapper (recomendado)

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Ou com Maven instalado globalmente:**
```bash
mvn spring-boot:run
```

### 3. Acessar no Navegador

```
http://localhost:8080
```

A aplicação sobe com perfil **H2** (banco em memória) e já insere dados iniciais
(3 usuários e 5 produtos via `data.sql`).

### 4. Console H2 (opcional, para inspecionar o banco)

```
http://localhost:8080/h2-console
```

- **JDBC URL:** `jdbc:h2:mem:pdvdb`
- **Username:** `sa`
- **Password:** *(vazio)*

---

## Como Executar com MySQL (Produção)

1. Crie o banco de dados:

```sql
CREATE DATABASE pdvdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ajuste as credenciais em `src/main/resources/application-mysql.properties` se necessário.

3. Execute com o perfil MySQL:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## Como Executar os Testes

### Executar todos os testes (unitários + integração + Selenium)

```bash
./mvnw test
```

> **Importante:** Os testes Selenium requerem **Google Chrome** instalado na máquina.
> O WebDriverManager baixa o ChromeDriver automaticamente. Os testes rodam em modo
> **headless** (sem abrir janela do navegador).

### Executar apenas testes unitários (sem Selenium)

```bash
./mvnw test -Dtest="*ServiceTest,ExceptionTest"
```

### Executar apenas testes de integração (MockMvc)

```bash
./mvnw test -Dtest="*IntegrationTest,HomeControllerTest"
```

### Executar apenas testes Selenium

```bash
./mvnw test -Dtest="*SeleniumTest"
```

### Executar um teste específico

```bash
./mvnw test -Dtest="UsuarioSeleniumTest"
./mvnw test -Dtest="ProdutoServiceTest#deveSalvarProdutoComDadosValidos"
```

---

## Relatório de Cobertura (JaCoCo)

Após executar os testes, o relatório JaCoCo é gerado automaticamente:

```bash
./mvnw test
```

Abra o relatório em:

```
target/site/jacoco/index.html
```

O build **falha automaticamente** se a cobertura de linhas ficar abaixo de **85%**,
conforme configurado no `pom.xml` (plugin `jacoco-maven-plugin` com regra `check`).

---

## Build Completo (compilar + testar + empacotar)

```bash
./mvnw clean package
```

O arquivo `.jar` executável será gerado em:

```
target/ponto-de-venda-1.0.0-SNAPSHOT.jar
```

Para executar o JAR diretamente:

```bash
java -jar target/ponto-de-venda-1.0.0-SNAPSHOT.jar
```

---

## Rotas da Interface Web

| Método | Rota                          | Descrição                         |
|--------|-------------------------------|-----------------------------------|
| GET    | `/`                           | Página inicial (dashboard)        |
| GET    | `/usuarios`                   | Listagem de usuários              |
| GET    | `/usuarios/novo`              | Formulário de cadastro            |
| POST   | `/usuarios/salvar`            | Salvar novo usuário               |
| GET    | `/usuarios/editar/{id}`       | Formulário de edição              |
| POST   | `/usuarios/atualizar/{id}`    | Atualizar usuário existente       |
| GET    | `/usuarios/excluir/{id}`      | Excluir usuário                   |
| GET    | `/produtos`                   | Listagem de produtos              |
| GET    | `/produtos/novo`              | Formulário de cadastro            |
| POST   | `/produtos/salvar`            | Salvar novo produto               |
| GET    | `/produtos/editar/{id}`       | Formulário de edição              |
| POST   | `/produtos/atualizar/{id}`    | Atualizar produto existente       |
| GET    | `/produtos/excluir/{id}`      | Excluir produto                   |

### API REST (JSON)

| Método | Rota                          | Descrição                         |
|--------|-------------------------------|-----------------------------------|
| GET    | `/api/usuarios`               | Listar todos os usuários          |
| GET    | `/api/usuarios/{id}`          | Buscar usuário por ID             |
| POST   | `/api/usuarios`               | Criar novo usuário                |
| PUT    | `/api/usuarios/{id}`          | Atualizar usuário                 |
| DELETE | `/api/usuarios/{id}`          | Excluir usuário                   |
| GET    | `/api/produtos`               | Listar todos os produtos          |
| GET    | `/api/produtos/{id}`          | Buscar produto por ID             |
| POST   | `/api/produtos`               | Criar novo produto                |
| PUT    | `/api/produtos/{id}`          | Atualizar produto                 |
| DELETE | `/api/produtos/{id}`          | Excluir produto                   |

---

## Stack Tecnológica

| Componente          | Tecnologia                                   |
|---------------------|----------------------------------------------|
| Linguagem           | Java 21                                      |
| Build               | Maven 3.9 + Maven Wrapper                    |
| Framework           | Spring Boot 3.4.1                            |
| Template Engine     | Thymeleaf                                    |
| Persistência        | Spring Data JPA + Hibernate                  |
| Banco Dev/Teste     | H2 Database (em memória)                     |
| Banco Produção      | MySQL 8+                                     |
| Validação           | Jakarta Bean Validation                      |
| Frontend            | Bootstrap 5.3 + Bootstrap Icons              |
| Testes Unitários    | JUnit 5 + Mockito + AssertJ                  |
| Testes Integração   | Spring MockMvc                               |
| Testes E2E          | Selenium WebDriver 4.27                      |
| Driver Management   | WebDriverManager 5.9                         |
| Cobertura           | JaCoCo 0.8.12                                |
| Utilitários         | Lombok 1.18.34                               |

---

## Organização dos Testes

### Testes Unitários (Mockito)
- `UsuarioServiceTest` — 10 cenários: listar, buscar, salvar, atualizar, excluir, validações de e-mail duplicado
- `ProdutoServiceTest` — 12 cenários: listar, buscar, salvar (com/sem código de barras), atualizar, excluir, validações
- `ExceptionTest` — 3 cenários: formatação de mensagens das exceções customizadas
- `GlobalExceptionHandlerTest` — 8 cenários: todas as ramificações do handler global (API vs MVC) para cada tipo de exceção

### Testes de Integração (MockMvc)
- `HomeControllerTest` — 2 cenários: contexto e página inicial
- `UsuarioControllerIntegrationTest` — 10 cenários com testes parametrizados (`@CsvSource`)
- `ProdutoControllerIntegrationTest` — 10 cenários com testes parametrizados
- `ApiRestIntegrationTest` — 20 cenários: CRUD completo dos endpoints REST `/api/usuarios` e `/api/produtos`

### Testes Selenium E2E (Page Object Model)
- `UsuarioSeleniumTest` — 10 cenários: navegação, cadastro parametrizado, edição, exclusão, e-mail duplicado, dados inválidos, fluxo CRUD completo
- `ProdutoSeleniumTest` — 10 cenários: navegação, cadastro parametrizado, edição, exclusão, código de barras duplicado, dados inválidos, fluxo CRUD completo

### Testes Avançados — Fuzz Testing, Fail Early/Gracefully, Segurança
- `FuzzTestingTest` — ~30 cenários: payloads XSS, SQL injection, JSON malformado, strings gigantes, caracteres Unicode, path traversal, fuzz aleatório com seed fixa
- `FailEarlyGracefullyTest` — 17 cenários: validação antecipada de IDs negativos, Bean Validation pré-service, respostas controladas (404 JSON, 422 JSON, HTML seguro), boundary values (limites min/max de campos)
- `CenariosAdversosTest` — 12 cenários: requisições concorrentes, criação simultânea com dados duplicados, exclusões/listagens concorrentes, dupla exclusão, atualização de recurso excluído, métodos HTTP inválidos
- `SegurancaMensagensTest` — 10 cenários: verifica que NENHUMA resposta de erro expõe stack traces, nomes de classes Java, detalhes de banco, informações do framework

### Page Objects (POM)
- `BasePage` — Classe abstrata com operações comuns (espera, verificação de alertas)
- `UsuarioListagemPage` — Interação com tabela, botões e alertas de usuários
- `UsuarioFormularioPage` — Interação com campos, select, checkbox e submit
- `ProdutoListagemPage` — Interação com tabela e botões de produtos
- `ProdutoFormularioPage` — Interação com campos numéricos e submit

---

## Padrões de Robustez Implementados

### Fail Early
O sistema valida entradas o mais cedo possível, rejeitando dados inválidos antes de atingir camadas profundas:
- **API REST**: IDs negativos ou zero são rejeitados com `IllegalArgumentException` antes de consultar o banco
- **Bean Validation**: campos `@NotBlank`, `@Email`, `@Size`, `@DecimalMin` rejeitam dados inválidos antes de chegar ao Service
- **Service**: regras de negócio (email/código de barras duplicado) são verificadas antes da persistência

### Fail Gracefully
Quando ocorre um erro esperado ou inesperado, o sistema retorna resposta controlada sem expor detalhes internos:
- **GlobalExceptionHandler** (`@ControllerAdvice`): intercepta TODAS as exceções não tratadas
- **API (JSON)**: retorna `{"status": 404, "erro": "mensagem segura"}` — sem stack traces
- **MVC (HTML)**: retorna página `error.html` customizada — sem Whitelabel Error Page
- **Erros 500**: mensagem genérica "Ocorreu um erro interno" — detalhes apenas no log do servidor

### Segurança de Mensagens
Nenhuma resposta de erro ao usuário contém: stack traces, nomes de classes Java, detalhes do banco de dados, versão do framework, credenciais ou paths internos.

---


## Troubleshooting

**Erro: Chrome not found (testes Selenium)**
- Instale o Google Chrome: `sudo apt install google-chrome-stable` (Linux)
- Ou baixe em: https://www.google.com/chrome/

**Erro: Port 8080 already in use**
- Altere a porta em `application.properties`: `server.port=8081`
- Ou pare o processo que usa a porta: `lsof -i :8080`

**Erro: Java version mismatch**
- Verifique: `java -version` (deve ser 21+)
- Defina `JAVA_HOME` apontando para o JDK 21

