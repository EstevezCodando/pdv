# Manual de execucao

## Executar aplicacao integrada localmente

Com Maven:

```bash
./mvnw spring-boot:run
```

Com Gradle:

```bash
gradle bootRun
```

A aplicacao sobe com a home integrada em `http://localhost:8080/`.

## Principais rotas

Interface web:

- `/`
- `/usuarios`
- `/produtos`

API REST:

- `/api/usuarios`
- `/api/produtos`
- `/api/integracao/resumo`

## Executar testes

Testes principais com cobertura:

```bash
gradle clean check
```

Testes end-to-end com Selenium:

```bash
gradle seleniumTest
```

## Interpretacao dos workflows

O workflow `CI` executa build, testes e verificacao de cobertura. Se falhar, a aba Actions mostrara exatamente a etapa quebrada.

O workflow `E2E Selenium` foi isolado e deve ser executado quando houver necessidade de validar a interface completa com navegador.

## Evidencias de cobertura

Apos a execucao do Gradle, o relatorio HTML do JaCoCo fica em:

```text
build/reports/jacoco/test/html/index.html
```
