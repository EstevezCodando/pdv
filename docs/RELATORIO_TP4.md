# TP4 - Relatorio de refatoracao, integracao e automacao

## Visao geral

O projeto foi preparado para operar como uma aplicacao unica, com os modulos de usuarios e produtos compartilhando convencoes de validacao, componentes reutilizaveis e uma visao integrada do estado operacional do sistema.

## Melhorias de refatoracao aplicadas

Foram removidos pontos de duplicacao de regra nas APIs REST por meio da classe `ValidadorDeIdentificador`, que centraliza a validacao fail early de IDs.

Os perfis aceitos para usuario foram centralizados em `PerfisUsuario`, evitando repeticao literal de valores e risco de divergencia entre telas.

Os services de usuario e produto receberam metodos de contagem e extracao de comportamento repetido de atualizacao para metodos privados menores, deixando a regra mais legivel e testavel.

O `HomeController` deixou de ser apenas um redirecionador visual e passou a consumir `ResumoIntegradoService`, tornando a home uma tela real de integracao entre os dois sistemas.

Foi criado o endpoint `GET /api/integracao/resumo`, que consolida informacoes de usuarios e produtos em um unico contrato REST para uso futuro por dashboards, automacoes ou outros modulos.

## Cobertura e testes

Foram adicionados testes direcionados para a consolidacao do resumo integrado e para o endpoint/controller responsavel pela exposicao desse resumo.

A cobertura minima de 85% foi configurada no Gradle por meio do JaCoCo com falha automatica do pipeline quando o limite nao for atingido.

Os testes Selenium foram separados em uma tarefa dedicada para reduzir fragilidade da pipeline principal e preservar previsibilidade no CI.

## Automacao CI/CD

A pipeline principal executa `clean check`, validando compilacao, testes e cobertura.

O relatorio HTML do JaCoCo e publicado como artefato para facilitar auditoria e depuracao.

Os testes end-to-end com Selenium ficaram em workflow separado, disparado manualmente, para evitar falsos negativos recorrentes em validacoes de PR.

## Runners

Foi adotado `ubuntu-latest` hospedado pelo GitHub por simplicidade operacional, previsibilidade e menor custo de manutencao. Para este projeto, runner auto-hospedado so faria sentido se houvesse dependencia obrigatoria de navegador customizado, banco persistente especifico ou rede interna.
