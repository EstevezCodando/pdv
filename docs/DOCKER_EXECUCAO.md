# Execução do PDV em Docker

## Diagnóstico

Sim. Ao rodar `gradlew bootRun --args='--spring.profiles.active=postgres'`, a aplicação deveria permanecer online na porta 8080 enquanto o processo estivesse saudável.

No seu caso, isso não ocorre porque o build do Gradle conclui, mas a aplicação cai durante a inicialização do `ApplicationContext`, principalmente na criação do datasource e do `EntityManagerFactory`. O log mostrou falha de autenticação no PostgreSQL para `pdvuser`, então o processo web não chega a permanecer operacional.

## Estratégia adotada

Para evitar divergência entre ambiente local, banco, variáveis e profile `postgres`, a aplicação foi preparada para rodar inteira em Docker Compose, com dois serviços:

- `postgres`
- `pdv-app`

Assim, o backend passa a conectar no hostname interno `postgres`, em vez de depender de resolução local e variáveis manuais no terminal.

## Arquivos adicionados ou alterados

- `Dockerfile`
- `.dockerignore`
- `docker-compose.yml`
- `src/main/resources/application-postgres.properties`

## Como subir

Na raiz do projeto, execute:

```bash
docker compose down -v
docker compose up --build
```

Depois acesse:

```text
http://localhost:8080
```

## Como validar

Para verificar se a aplicação ficou online:

```bash
docker compose ps
docker compose logs -f pdv-app
docker compose logs -f postgres
```

## Observações

Se o banco tiver sido criado antes com outra senha, o volume persistido pode manter credenciais antigas. Por isso o `down -v` é importante na primeira subida após a correção.

Se houver um `DataInitializer` no projeto, ele poderá semear os usuários automaticamente. Caso não exista ou esteja restrito a outro profile, o login ainda poderá falhar por ausência de dados, mas a aplicação deverá permanecer online.

## Próximo passo recomendado

Depois de confirmar a aplicação online no container, o ideal é estabilizar login e suíte de testes usando o mesmo profile `postgres` em ambiente consistente.
