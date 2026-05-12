# CarLog API

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot 3.2">
  <img src="https://img.shields.io/badge/Maven-3.9-blue?style=for-the-badge&logo=apachemaven" alt="Maven">
  <img src="https://img.shields.io/badge/H2-Database-informational?style=for-the-badge" alt="H2 Database">
  <img src="https://img.shields.io/badge/PostgreSQL-Production-336791?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License MIT">
</p>

<p align="center">
  <b>Sistema de Gerenciamento de Manutencao Veicular</b>
  <br>
  Uma REST API completa para cadastro de veiculos, registro de manutencoes, calculo automatico de revisoes e acompanhamento de gastos.
</p>

---

## Sumario

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Como Executar](#como-executar)
- [API Endpoints](#api-endpoints)
- [Exemplos de Uso](#exemplos-de-uso)
- [Validacoes e Regras de Negocio](#validacoes-e-regras-de-negocio)
- [Banco de Dados](#banco-de-dados)
- [Screenshots / Testes](#screenshots--testes)
- [Licenca](#licenca)
- [Autor](#autor)

---

## Sobre o Projeto

O **CarLog API** e um sistema backend completo desenvolvido como projeto pessoal para demonstrar habilidades em desenvolvimento Java com Spring Boot. O sistema permite que oficinas mecanicas ou proprietarios de veiculos gerenciem o historico de manutencao de seus carros de forma organizada e inteligente.

Este projeto foi construido seguindo as melhores praticas do mercado, com arquitetura em camadas, DTOs para seguranca, validacoes robustas, tratamento global de excecoes e persistencia de dados com JPA/Hibernate.

### Motivacao

A ideia surgiu da necessidade de ter um controle digital sobre os gastos e cuidados com o veiculo pessoal. Muitas vezes acabamos esquecendo quando foi a ultima troca de oleo, ou quanto ja gastamos em manutencao ao longo do ano. O CarLog resolve isso de forma simples e eficiente.

---

## Tecnologias

| Tecnologia | Versao | Descricao |
|---|---|---|
| Java | 17+ | Linguagem principal |
| Spring Boot | 3.2.0 | Framework principal |
| Spring Data JPA | 3.2.0 | Persistencia de dados |
| Spring Web | 3.2.0 | Criacao dos endpoints REST |
| Spring Validation | 3.2.0 | Validacao de dados de entrada |
| H2 Database | 2.2.224 | Banco em memoria para desenvolvimento |
| PostgreSQL | - | Banco para producao |
| Lombok | - | Reducao de boilerplate code |
| Maven | 3.9+ | Gerenciamento de dependencias |

---

## Funcionalidades

### Veiculos
- [x] Cadastro de veiculos com validacao de placa
- [x] Listagem de todos os veiculos
- [x] Consulta de veiculo por ID
- [x] Atualizacao de dados do veiculo
- [x] Exclusao de veiculo (com cascade para manutencoes)
- [x] Normalizacao automatica de placas

### Manutencoes
- [x] Registro de manutencoes vinculadas a veiculos
- [x] Listagem de todas as manutencoes
- [x] Consulta de manutencao por ID
- [x] Listagem de manutencoes por veiculo
- [x] Atualizacao de manutencao
- [x] Exclusao de manutencao

### Regras de Negocio Inteligentes
- [x] **Atualizacao automatica de quilometragem** do veiculo
- [x] **Calculo automatico da proxima troca de oleo** (km atual + 10.000)
- [x] **Bloqueio de manutencao com KM menor** que a atual do veiculo
- [x] **Validacao de placa** nos formatos AAA-0000 e Mercosul (AAA0A00)

### Relatorios
- [x] Resumo de gastos por veiculo (total gasto, quantidade de manutencoes, proxima troca de oleo)

---

## Arquitetura

O projeto segue a **arquitetura em camadas**, padrao amplamente utilizado em aplicacoes Spring Boot:

```
+---------------------+
|     Controller      |  <- Recebe as requisicoes HTTP (GET, POST, PUT, DELETE)
|   (@RestController) |
+----------+----------+
           |
           v
+---------------------+
|       Service       |  <- Contem as regras de negocio
|    (@Service)       |
+----------+----------+
           |
           v
+---------------------+
|     Repository      |  <- Comunicacao com o banco de dados
|  (@Repository /     |
|   JpaRepository)    |
+----------+----------+
           |
           v
+---------------------+
|        Model        |  <- Entidades JPA (mapeamento das tabelas)
|     (@Entity)       |
+---------------------+
```

### Padrao DTO (Data Transfer Object)

Utilizamos DTOs para controlar exatamente quais dados trafegam entre a API e o cliente, aumentando a seguranca e evitando exposicao acidental de dados internos.

### Tratamento Global de Excecoes

Todas as excecoes sao tratadas de forma centralizada pelo `GlobalExceptionHandler`, garantindo respostas padronizadas e amigaveis para o cliente da API.

---

## Como Executar

### Pre-requisitos

- Java 17 ou superior instalado
- Maven 3.9 ou superior instalado
- (Opcional) Postman ou Insomnia para testar a API

### Passo a Passo

1. **Clone o repositorio:**
   ```bash
   git clone https://github.com/seu-usuario/carlog-api.git
   cd carlog-api
   ```

2. **Compile o projeto:**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicacao:**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a API:**
   - A aplicacao estara disponivel em: `http://localhost:8081`
   - Console do H2: `http://localhost:8081/h2-console`
     - JDBC URL: `jdbc:h2:mem:carlogdb`
     - User: `sa`
     - Password: *(deixe em branco)*

### Perfil de Producao (PostgreSQL)

Para usar PostgreSQL em producao, crie um arquivo `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/carlog
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

E execute com o profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## API Endpoints

### Veiculos

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| `POST` | `/api/vehicles` | Cadastrar novo veiculo |
| `GET` | `/api/vehicles` | Listar todos os veiculos |
| `GET` | `/api/vehicles/{id}` | Buscar veiculo por ID |
| `PUT` | `/api/vehicles/{id}` | Atualizar veiculo |
| `DELETE` | `/api/vehicles/{id}` | Remover veiculo |

### Manutencoes

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| `POST` | `/api/maintenances` | Registrar nova manutencao |
| `GET` | `/api/maintenances` | Listar todas as manutencoes |
| `GET` | `/api/maintenances/{id}` | Buscar manutencao por ID |
| `GET` | `/api/maintenances/vehicle/{vehicleId}` | Listar manutencoes de um veiculo |
| `GET` | `/api/maintenances/vehicle/{vehicleId}/expenses` | Resumo de gastos do veiculo |
| `PUT` | `/api/maintenances/{id}` | Atualizar manutencao |
| `DELETE` | `/api/maintenances/{id}` | Remover manutencao |

---

## Exemplos de Uso

### 1. Cadastrar um Veiculo

**Request:**
```http
POST /api/vehicles
Content-Type: application/json

{
  "plate": "ABC1234",
  "model": "Onix",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 15000
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "plate": "ABC1234",
  "model": "Onix",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 15000
}
```

---

### 2. Registrar uma Manutencao (Troca de Oleo)

**Request:**
```http
POST /api/maintenances
Content-Type: application/json

{
  "description": "Troca de oleo e filtro",
  "date": "2026-05-12",
  "value": 350.00,
  "mileageAtTime": 25000,
  "vehicleId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "description": "Troca de oleo e filtro",
  "date": "2026-05-12",
  "value": 350.00,
  "mileageAtTime": 25000,
  "nextOilChangeMileage": 35000,
  "vehicleId": 1,
  "vehiclePlate": "ABC1234"
}
```

> O sistema calculou automaticamente que a proxima troca de oleo sera aos **35.000 km**!

---

### 3. Consultar Resumo de Gastos

**Request:**
```http
GET /api/maintenances/vehicle/1/expenses
```

**Response (200 OK):**
```json
{
  "vehicleId": 1,
  "vehiclePlate": "ABC1234",
  "vehicleModel": "Onix",
  "currentMileage": 25000,
  "nextOilChangeMileage": 35000,
  "totalSpent": 350.00,
  "totalMaintenances": 1
}
```

---

### 4. Tentar Cadastrar Manutencao com KM Invalida

**Request:**
```http
POST /api/maintenances
Content-Type: application/json

{
  "description": "Revisao",
  "date": "2026-05-12",
  "value": 500.00,
  "mileageAtTime": 20000,
  "vehicleId": 1
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "A quilometragem da manutencao (20000) nao pode ser menor que a quilometragem atual do veiculo (25000)",
  "timestamp": "2026-05-12T16:08:08.0037443"
}
```

---

## Validacoes e Regras de Negocio

### Validacao de Placa
- Formato antigo: `AAA-0000` ou `AAA0000`
- Formato Mercosul: `AAA0A00`
- Placas sao normalizadas para maiusculas e sem hifen no banco

### Validacao de Dados
| Campo | Regra |
|-------|-------|
| `plate` | Obrigatoria, formato valido |
| `model` | Obrigatorio, 2 a 100 caracteres |
| `brand` | Obrigatorio, 2 a 100 caracteres |
| `year` | Obrigatorio, entre 1900 e 2100 |
| `mileage` | Obrigatoria, nao pode ser negativa |
| `value` | Obrigatorio, nao pode ser negativo |
| `date` | Obrigatoria |

### Regras de Negocio
1. **Placa unica:** Nao e permitido cadastrar dois veiculos com a mesma placa
2. **KM crescente:** A manutencao so pode ser registrada com quilometragem igual ou maior que a atual do veiculo
3. **Atualizacao automatica:** Se a manutencao tiver KM maior, o veiculo e atualizado automaticamente
4. **Troca de oleo:** Se a descricao contem "oleo", o sistema calcula a proxima troca automaticamente

---

## Banco de Dados

### Diagrama ER

```
+----------------+         +------------------+
|    vehicles    |         |  maintenances    |
+----------------+         +------------------+
| id (PK)        |<------->| id (PK)          |
| plate (unique) |    1:N  | description      |
| model          |         | date             |
| brand          |         | value            |
| year           |         | mileage_at_time  |
| mileage        |         | next_oil_change  |
+----------------+         | vehicle_id (FK)  |
                           +------------------+
```

### Tabelas

**vehicles:**
| Coluna | Tipo | Restricoes |
|--------|------|------------|
| id | BIGINT | PK, Auto Increment |
| plate | VARCHAR(8) | NOT NULL, UNIQUE |
| model | VARCHAR(255) | NOT NULL |
| brand | VARCHAR(255) | NOT NULL |
| year | INTEGER | NOT NULL |
| mileage | INTEGER | NOT NULL |

**maintenances:**
| Coluna | Tipo | Restricoes |
|--------|------|------------|
| id | BIGINT | PK, Auto Increment |
| description | VARCHAR(255) | NOT NULL |
| date | DATE | NOT NULL |
| value | NUMERIC(10,2) | NOT NULL |
| mileage_at_time | INTEGER | NOT NULL |
| next_oil_change_mileage | INTEGER | NULL |
| vehicle_id | BIGINT | NOT NULL, FK |

---

## Screenshots / Testes

Voce pode testar a API utilizando:
- **Postman:** Importe as requisicoes da pasta `/docs/postman`
- **Insomnia:** Importe o arquivo `/docs/insomnia.json`
- **cURL:** Veja exemplos abaixo

### Teste rapido com cURL

```bash
# Cadastrar veiculo
curl -X POST http://localhost:8081/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"plate":"ABC1234","model":"Onix","brand":"Chevrolet","year":2022,"mileage":15000}'

# Registrar manutencao
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{"description":"Troca de oleo","date":"2026-05-12","value":350.00,"mileageAtTime":25000,"vehicleId":1}'

# Ver resumo de gastos
curl http://localhost:8081/api/maintenances/vehicle/1/expenses
```

---

## Estrutura de Pastas

```
carlog-api/
├── src/
│   ├── main/
│   │   ├── java/com/carlog/
│   │   │   ├── CarlogApplication.java
│   │   │   ├── config/              # Configuracoes
│   │   │   ├── controller/          # Controllers REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Excecoes e handlers
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Repositorios Spring Data
│   │   │   ├── service/             # Regras de negocio
│   │   │   └── validation/          # Validacoes customizadas
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/carlog/         # Testes unitarios e integracao
├── pom.xml
└── README.md
```

---

## Proximos Passos / Roadmap

- [ ] Autenticacao e autorizacao com JWT
- [ ] Documentacao automatica com Swagger/OpenAPI
- [ ] Testes unitarios e de integracao com JUnit e Mockito
- [ ] Docker e Docker Compose
- [ ] CI/CD com GitHub Actions
- [ ] Frontend em React ou Angular
- [ ] Exportacao de relatorios em PDF
- [ ] Notificacoes de proxima manutencao

---

## Licenca

Este projeto esta licenciado sob a licenca MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">
  Feito com dedicacao para demonstrar habilidades em desenvolvimento backend com Java e Spring Boot.
</p>
