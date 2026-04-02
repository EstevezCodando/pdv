# Plano de Estabilização Total do PDV

## Diagnóstico consolidado

O projeto apresenta uma falha estrutural em cascata. O problema não está concentrado em um único controller ou service, mas na combinação entre configuração de segurança, profiles de ambiente, inicialização de dados e configuração do banco PostgreSQL.

Os sintomas observados se agrupam em quatro blocos principais.

O primeiro bloco está no profile `test`. A suíte está falhando em massa porque o `ApplicationContext` não está estável. A configuração de testes foi modelada com `@TestConfiguration`, o que não está sendo incorporado de forma confiável ao contexto principal da suíte. Além disso, o profile de teste ainda exclui `SecurityAutoConfiguration`, o que torna o contexto mais frágil do que o necessário.

O segundo bloco está no profile `postgres`. O sistema sobe o build, mas não mantém a aplicação online porque a conexão com o banco falha por autenticação. A configuração atual depende exclusivamente de variáveis de ambiente e não possui fallback coerente com o `docker-compose.yml`. Isso torna o ambiente local inconsistente.

O terceiro bloco está no login. Mesmo quando o banco estiver acessível, o login continua inviável em um banco limpo se não houver dados seedados. O `DataInitializer` atual só roda no profile `h2`, o que impede um fluxo mínimo de autenticação no profile `postgres`.

O quarto bloco está na coesão da segurança. O `PasswordEncoder` está acoplado à `SecurityConfig` principal, quando na prática ele é uma dependência transversal de serviços e inicializadores. Isso prejudica tanto a estabilidade dos testes quanto a clareza da configuração.

## Plano de ação

A correção deve seguir esta ordem.

Primeiro, estabilizar a infraestrutura de beans. O `PasswordEncoder` precisa sair da `SecurityConfig` e ir para uma configuração dedicada, sempre ativa, evitando sumiço de bean por profile.

Segundo, estabilizar a suíte de testes. A configuração de teste deve ser transformada em `@Configuration` normal com `@Profile("test")`, mantendo `permitAll` e `csrf.disable()`. Em paralelo, `application-test.properties` deve deixar de excluir a autoconfiguração de segurança.

Terceiro, estabilizar o profile `postgres`. O arquivo `application-postgres.properties` deve passar a usar fallback compatível com o `docker-compose.yml`, permitindo subir localmente sem obrigar export manual de variáveis.

Quarto, tornar o login operacional em ambiente local. O `DataInitializer` deve rodar também no profile `postgres`, criando usuários padrão e produtos mínimos de forma idempotente.

Quinto, endurecer o comportamento de autenticação. O `PdvUserDetailsService` deve bloquear usuários inativos.

## Resultado esperado

Após essas alterações, o projeto deve apresentar três ganhos imediatos.

O primeiro é permitir que `gradlew bootRun --args='--spring.profiles.active=postgres'` suba de forma consistente contra o banco local do `docker-compose`.

O segundo é tornar o login utilizável em ambiente local, com credenciais seedadas.

O terceiro é reduzir drasticamente o efeito cascata da suíte de testes, permitindo que as falhas remanescentes representem defeitos reais de comportamento, e não mais colapso do bootstrap do contexto.
