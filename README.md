# PDV Integrado em Java com Gradle

Sistema de ponto de venda em Java 21 com Spring Boot, Gradle, H2/MySQL, interface web com Thymeleaf, APIs REST, integracao entre modulos de usuarios, produtos e vendas e pipeline completo de CI/CD.

## Principais evolucoes do TP5

- consolidacao do fluxo de vendas com baixa transacional de estoque
- resumo integrado ampliado com faturamento, ticket medio e total de vendas
- logs com correlacao por requisicao para facilitar depuracao local e em workflows
- pipeline com CI principal, seguranca com CodeQL e revisao de dependencias, CD com validacao pos-deploy, Selenium e DAST
- upload de artefatos de build, testes e cobertura

## Como executar localmente

### Requisitos

- Java 21
- Gradle 8.12 ou compativel

### Subir a aplicacao

```bash
gradle bootRun
```

A aplicacao sobe por padrao com perfil `h2`.

### Executar testes e cobertura

```bash
gradle clean check
```

### Executar Selenium local

```bash
gradle seleniumTest
```

### Executar validacao pos-deploy contra uma URL existente

```bash
set PDV_BASE_URL=http://127.0.0.1:8080
gradle postDeployTest
```

## Endpoints principais

- `GET /api/usuarios`
- `GET /api/produtos`
- `GET /api/integracao/resumo`
- `GET /api/vendas`
- `POST /api/vendas`

### Exemplo de venda

```json
{
  "usuarioId": 1,
  "formaPagamento": "PIX",
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 2, "quantidade": 1 }
  ]
}
```

## Workflows GitHub Actions

### CI Principal

Executa build, testes, cobertura e publica artefatos de relatorios e do jar.

### Seguranca

Executa revisao de dependencias em PR e CodeQL na branch principal.

### CD e Validacao Pos-Deploy

Empacota o sistema, realiza deploy remoto via webhook quando configurado ou sobe uma instancia local de homologacao no proprio runner, executa smoke tests, Selenium pos-deploy e DAST com ZAP.

## Segredos opcionais

- `PDV_BASE_URL`: URL do ambiente remoto publicado
- `PDV_DEPLOY_WEBHOOK`: webhook de deploy remoto do provedor escolhido

Sem esses segredos, o workflow de CD sobe uma instancia local no runner para homologacao automatizada.
