# PDV - Ponto de Venda

Aplicação web única desenvolvida em Java 21 com Spring Boot, Thymeleaf, JPA e Gradle. O projeto integra os módulos de usuários e produtos em uma única base coesa, com API REST, interface web, cobertura mínima de testes e pipeline de CI no GitHub Actions.

## Decisões estruturais do TP4

O projeto foi consolidado para usar apenas Gradle como ferramenta de build. A dualidade Maven + Gradle foi removida para reduzir ruído operacional, evitar documentação conflitante e simplificar a esteira de CI/CD. Os artefatos gerados pelo build também deixaram de fazer parte do projeto versionado.

## Pré-requisitos

É necessário ter Java 21 e Gradle instalados na máquina.

```bash
java -version
gradle -v
```

## Executar a aplicação

Na raiz do projeto, execute:

```bash
gradle bootRun
```

A aplicação ficará disponível em `http://localhost:8080`.

## Principais rotas

Na interface web, as rotas principais são `/`, `/usuarios` e `/produtos`. Na API REST, as rotas principais são `/api/usuarios`, `/api/produtos` e `/api/integracao/resumo`.

## Executar testes

Para rodar os testes unitários, de integração e a verificação de cobertura:

```bash
gradle clean check
```

Para rodar os testes end-to-end com Selenium:

```bash
gradle seleniumTest
```

## Cobertura

O projeto usa JaCoCo com cobertura mínima de 85% em linhas. Após a execução do build, os relatórios ficam em `build/reports/jacoco/test/html` e `build/reports/tests/test`.

## GitHub Actions

O workflow principal executa build, testes e cobertura em `push`, `pull_request` e `workflow_dispatch`. O workflow de Selenium é executado manualmente para reduzir falsos negativos em validações rotineiras de PR.

## Estrutura principal

```text
src/main/java/com/pdv/pontovenda
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── validation
```
