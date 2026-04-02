# Plano de estabilizacao do build

## Diagnostico

O projeto apresenta uma falha estrutural de contexto de teste, nao apenas erros isolados de regra de negocio. O relatorio aponta que a maior parte dos 141 testes falhos entra em cascata a partir da falha de bootstrap do `ApplicationContext`. Antes de corrigir comportamento de endpoint, era necessario restaurar a previsibilidade de configuracao dos perfis.

Tambem havia um problema operacional no profile `postgres`. O sistema dependia de `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` obrigatorios, enquanto o fluxo local do projeto usa `docker-compose` com credenciais previsiveis, mas sem exportar essas variaveis no processo do `bootRun`. Na pratica, isso fazia o profile `postgres` parecer quebrado, mesmo com o banco de dados em execucao.

Por fim, a experiencia de login estava comprometida em banco limpo, porque o profile `postgres` nao populava usuarios minimos. Ou seja, mesmo que a aplicacao conseguisse conectar, nao havia credenciais para autenticar.

## Causa raiz consolidada

1. `PasswordEncoder` estava definido apenas dentro de `SecurityConfig`, mas `SecurityConfig` era desativada no profile `test`.
2. `TestSecurityConfig` estava como `@TestConfiguration`, formato inadequado para o modo como a suite atual sobe o contexto via `@SpringBootTest`.
3. `application-test.properties` excluia `SecurityAutoConfiguration`, tornando o profile de teste mais fragil do que o necessario.
4. `application-postgres.properties` dependia exclusivamente de variaveis externas, sem fallback coerente com o `docker-compose.yml` do projeto.
5. `DataInitializer` era restrito ao profile `h2`, deixando o profile `postgres` sem usuarios e produtos iniciais em ambiente local.

## Plano de acao executado

### Etapa 1. Corrigir arquitetura de seguranca e beans compartilhados

Foi extraido o `PasswordEncoder` para uma configuracao propria, sempre ativa. Com isso, services de negocio que dependem do encoder deixam de quebrar quando a seguranca principal e desabilitada em `test`.

### Etapa 2. Tornar o profile `test` previsivel

A configuracao de teste foi convertida para `@Configuration` normal com `@Profile("test")`, liberando rotas e desabilitando CSRF apenas no profile de teste. Tambem foi removida a exclusao global de `SecurityAutoConfiguration` do `application-test.properties`.

### Etapa 3. Restaurar operacao local do profile `postgres`

Foi ajustado o arquivo `application-postgres.properties` para aceitar valores via ambiente, mas com fallback local coerente com o `docker-compose.yml` do projeto. Isso permite usar:

```bash
gradlew bootRun --args='--spring.profiles.active=postgres'
```

sem depender de export manual de variaveis, desde que o container PostgreSQL do `docker-compose` esteja ativo na maquina local.

### Etapa 4. Garantir credenciais validas e dados minimos

O `DataInitializer` foi ampliado para os perfis `h2` e `postgres`. Agora, em ambiente local limpo, o sistema sobe com usuarios iniciais e produtos basicos.

Credenciais de acesso:

- `admin@pdv.com` / `admin123`
- `operador@pdv.com` / `op123`
- `maria@pdv.com` / `maria123`

## Ordem recomendada de validacao

1. Subir o banco:
   ```bash
   docker compose up -d
   ```
2. Executar a aplicacao com Postgres:
   ```bash
   gradlew bootRun --args='--spring.profiles.active=postgres'
   ```
3. Validar login em `http://localhost:8080/login`.
4. Rodar o build de verificacao:
   ```bash
   gradlew clean test
   gradlew clean check
   ```
5. Avaliar o novo `falhas_detalhadas.txt`.

## Resultado esperado apos esta etapa

O resultado esperado nao e zerar imediatamente todas as falhas de comportamento da suite, mas remover a falha estrutural que derruba o contexto inteiro. Uma vez que o `ApplicationContext` passe a subir corretamente, o proximo conjunto remanescente de erros tende a revelar defeitos reais de regra de negocio, contratos HTTP ou validacao, que podem ser corrigidos de forma cirurgica.
