# Changelog

Todas as mudancas notaveis deste projeto serao documentadas neste arquivo.

O formato e baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semantico](https://semver.org/lang/pt-BR/).

---

## [1.0.0] - 2026-05-12

### Adicionado

#### Veiculos
- Endpoint para cadastro de veiculos com validacao de placa (formato antigo e Mercosul)
- Endpoint para listagem de todos os veiculos
- Endpoint para busca de veiculo por ID
- Endpoint para atualizacao de dados do veiculo
- Endpoint para exclusao de veiculo (com cascade para manutencoes)
- Validacao de placa unica no sistema
- Normalizacao automatica de placas (maiusculas, sem hifen)

#### Manutencoes
- Endpoint para registro de manutencoes vinculadas a veiculos
- Endpoint para listagem de todas as manutencoes
- Endpoint para busca de manutencao por ID
- Endpoint para listagem de manutencoes por veiculo
- Endpoint para atualizacao de manutencao
- Endpoint para exclusao de manutencao

#### Regras de Negocio Inteligentes
- Atualizacao automatica da quilometragem do veiculo quando a manutencao tem KM maior
- Bloqueio de manutencoes com quilometragem menor que a atual do veiculo
- Calculo automatico da proxima troca de oleo (km atual + 10.000)
- Deteccao de servicos de troca de oleo por palavras-chave na descricao

#### Relatorios
- Endpoint de resumo de gastos por veiculo (total gasto, quantidade de manutencoes, proxima troca de oleo)

#### Validacoes
- Validacao customizada de placa brasileira (formatos AAA-0000 e Mercosul)
- Validacao de ano do veiculo (1900 a 2100)
- Validacao de quilometragem nao negativa
- Validacao de valor de manutencao nao negativo
- Validacao de campos obrigatorios com Bean Validation

#### Arquitetura e Infraestrutura
- Arquitetura em camadas (Controller, Service, Repository, DTO, Model)
- Uso de DTOs para seguranca e controle de dados
- Tratamento global de excecoes com @RestControllerAdvice
- Excecoes customizadas (ResourceNotFoundException, BusinessException)
- Configuracao de banco H2 para desenvolvimento
- Suporte a PostgreSQL para producao (via profiles)
- CORS habilitado para desenvolvimento frontend
- Console H2 acessivel em /h2-console

#### Documentacao
- README.md completo com badges e instrucoes
- DOCUMENTATION.md com documentacao tecnica detalhada
- API_GUIDE.md com todos os endpoints e exemplos
- Este arquivo CHANGELOG.md

---

## [Planned] - Futuro

### Em Planejamento

#### Seguranca
- [ ] Autenticacao e autorizacao com JWT (JSON Web Tokens)
- [ ] Spring Security integration
- [ ] Controle de acesso baseado em roles (ADMIN, USER)
- [ ] Rate limiting para prevenir abuso da API

#### Documentacao Automatica
- [ ] Integracao com SpringDoc OpenAPI (Swagger UI)
- [ ] Documentacao interativa dos endpoints

#### Testes
- [ ] Testes unitarios com JUnit 5 e Mockito
- [ ] Testes de integracao com @SpringBootTest
- [ ] Testes de repositorio com @DataJpaTest
- [ ] Cobertura de codigo com JaCoCo

#### DevOps
- [ ] Dockerfile para containerizacao
- [ ] docker-compose.yml com PostgreSQL
- [ ] CI/CD pipeline com GitHub Actions
- [ ] Deploy automatizado

#### Funcionalidades
- [ ] Paginacao em listagens
- [ ] Filtros e busca avancada
- [ ] Ordenacao de resultados
- [ ] Exportacao de relatorios em PDF
- [ ] Notificacoes de proxima manutencao (email/push)
- [ ] Upload de notas fiscais/comprovantes
- [ ] Multiplos veiculos por usuario
- [ ] Historico de precos de pecas

#### Performance
- [ ] Cache com Spring Cache + Redis
- [ ] Indices adicionais no banco de dados
- [ ] Otimizacao de queries N+1

#### Frontend
- [ ] Aplicacao web em React ou Angular
- [ ] Dashboard com graficos de gastos
- [ ] Calendario de manutencoes

---

## Notas de Versao

### Como Versionar

Este projeto segue o **Versionamento Semantico (SemVer)**:

- **MAJOR** (X.0.0): Mudancas incompativeis na API
- **MINOR** (0.X.0): Adicao de funcionalidades de forma compativel
- **PATCH** (0.0.X): Correcoes de bugs de forma compativel

---

## Contribuindo

Se voce deseja contribuir com este projeto:

1. Verifique a secao [Planned] para ver o que esta em planejamento
2. Abra uma issue para discutir novas funcionalidades
3. Envie um Pull Request com suas modificacoes

---

**Ultima atualizacao:** 12 de Maio de 2026
