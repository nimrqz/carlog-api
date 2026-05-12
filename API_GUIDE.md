# Guia da API - CarLog API

Guia completo de todos os endpoints da API com exemplos de requisicoes e respostas.

---

## Base URL

```
http://localhost:8081/api
```

## Content-Type

Todas as requisicoes e respostas utilizam **JSON**:

```
Content-Type: application/json
```

---

## Endpoints de Veiculos

### 1. Cadastrar Veiculo

Cria um novo veiculo no sistema.

**Endpoint:** `POST /vehicles`

**Request Body:**
```json
{
  "plate": "ABC1234",
  "model": "Onix",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 15000
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Descricao | Validacao |
|-------|------|-----------|-----------|
| plate | string | Placa do veiculo | Formato AAA-0000 ou AAA0A00 |
| model | string | Modelo | Min 2, Max 100 caracteres |
| brand | string | Marca | Min 2, Max 100 caracteres |
| year | integer | Ano | 1900 a 2100 |
| mileage | integer | Quilometragem | >= 0 |

**Response 201 Created:**
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

**Response 400 Bad Request (placa invalida):**
```json
{
  "status": 400,
  "message": "Erro de validacao",
  "timestamp": "2026-05-12T16:08:08.0037443",
  "errors": {
    "plate": "Placa invalida. Use o formato AAA-0000 ou AAA0A00"
  }
}
```

**Response 400 Bad Request (placa duplicada):**
```json
{
  "status": 400,
  "message": "Ja existe um veiculo cadastrado com esta placa: ABC1234",
  "timestamp": "2026-05-12T16:08:08.0037443"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8081/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "plate": "ABC1234",
    "model": "Onix",
    "brand": "Chevrolet",
    "year": 2022,
    "mileage": 15000
  }'
```

---

### 2. Listar Todos os Veiculos

Retorna uma lista com todos os veiculos cadastrados.

**Endpoint:** `GET /vehicles`

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "plate": "ABC1234",
    "model": "Onix",
    "brand": "Chevrolet",
    "year": 2022,
    "mileage": 25000
  },
  {
    "id": 2,
    "plate": "XYZ9B99",
    "model": "Corolla",
    "brand": "Toyota",
    "year": 2023,
    "mileage": 8000
  }
]
```

**cURL:**
```bash
curl http://localhost:8081/api/vehicles
```

---

### 3. Buscar Veiculo por ID

Retorna os detalhes de um veiculo especifico.

**Endpoint:** `GET /vehicles/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID do veiculo |

**Response 200 OK:**
```json
{
  "id": 1,
  "plate": "ABC1234",
  "model": "Onix",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 25000
}
```

**Response 404 Not Found:**
```json
{
  "status": 404,
  "message": "Veiculo nao encontrado com ID: 999",
  "timestamp": "2026-05-12T16:08:08.0037443"
}
```

**cURL:**
```bash
curl http://localhost:8081/api/vehicles/1
```

---

### 4. Atualizar Veiculo

Atualiza os dados de um veiculo existente.

**Endpoint:** `PUT /vehicles/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID do veiculo |

**Request Body:**
```json
{
  "plate": "ABC1234",
  "model": "Onix LTZ",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 25000
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "plate": "ABC1234",
  "model": "Onix LTZ",
  "brand": "Chevrolet",
  "year": 2022,
  "mileage": 25000
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8081/api/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "plate": "ABC1234",
    "model": "Onix LTZ",
    "brand": "Chevrolet",
    "year": 2022,
    "mileage": 25000
  }'
```

---

### 5. Deletar Veiculo

Remove um veiculo e todas as suas manutencoes (cascade).

**Endpoint:** `DELETE /vehicles/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID do veiculo |

**Response 204 No Content:** *(sem body)*

**cURL:**
```bash
curl -X DELETE http://localhost:8081/api/vehicles/1
```

---

## Endpoints de Manutencoes

### 6. Registrar Manutencao

Cria um novo registro de manutencao vinculado a um veiculo.

**Endpoint:** `POST /maintenances`

**Request Body:**
```json
{
  "description": "Troca de oleo e filtro de oleo",
  "date": "2026-05-12",
  "value": 350.00,
  "mileageAtTime": 25000,
  "vehicleId": 1
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Descricao | Validacao |
|-------|------|-----------|-----------|
| description | string | Descricao do servico | Min 3, Max 255 caracteres |
| date | string (date) | Data da manutencao | Formato ISO: YYYY-MM-DD |
| value | number | Valor gasto | >= 0 |
| mileageAtTime | integer | KM no momento | >= 0 |
| vehicleId | long | ID do veiculo | Deve existir |

**Response 201 Created:**
```json
{
  "id": 1,
  "description": "Troca de oleo e filtro de oleo",
  "date": "2026-05-12",
  "value": 350.00,
  "mileageAtTime": 25000,
  "nextOilChangeMileage": 35000,
  "vehicleId": 1,
  "vehiclePlate": "ABC1234"
}
```

> O sistema detectou que a descricao contem "oleo" e calculou automaticamente a proxima troca aos **35.000 km**.

**Response 400 Bad Request (KM menor):**
```json
{
  "status": 400,
  "message": "A quilometragem da manutencao (20000) nao pode ser menor que a quilometragem atual do veiculo (25000)",
  "timestamp": "2026-05-12T16:08:08.0037443"
}
```

**Response 404 Not Found (veiculo nao existe):**
```json
{
  "status": 404,
  "message": "Veiculo nao encontrado com ID: 999",
  "timestamp": "2026-05-12T16:08:08.0037443"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Troca de oleo e filtro de oleo",
    "date": "2026-05-12",
    "value": 350.00,
    "mileageAtTime": 25000,
    "vehicleId": 1
  }'
```

---

### 7. Listar Todas as Manutencoes

Retorna todas as manutencoes cadastradas no sistema.

**Endpoint:** `GET /maintenances`

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "description": "Troca de oleo e filtro de oleo",
    "date": "2026-05-12",
    "value": 350.00,
    "mileageAtTime": 25000,
    "nextOilChangeMileage": 35000,
    "vehicleId": 1,
    "vehiclePlate": "ABC1234"
  },
  {
    "id": 2,
    "description": "Revisao dos 20.000 km",
    "date": "2026-04-15",
    "value": 1200.00,
    "mileageAtTime": 20000,
    "nextOilChangeMileage": null,
    "vehicleId": 1,
    "vehiclePlate": "ABC1234"
  }
]
```

**cURL:**
```bash
curl http://localhost:8081/api/maintenances
```

---

### 8. Buscar Manutencao por ID

Retorna os detalhes de uma manutencao especifica.

**Endpoint:** `GET /maintenances/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID da manutencao |

**Response 200 OK:**
```json
{
  "id": 1,
  "description": "Troca de oleo e filtro de oleo",
  "date": "2026-05-12",
  "value": 350.00,
  "mileageAtTime": 25000,
  "nextOilChangeMileage": 35000,
  "vehicleId": 1,
  "vehiclePlate": "ABC1234"
}
```

**cURL:**
```bash
curl http://localhost:8081/api/maintenances/1
```

---

### 9. Listar Manutencoes por Veiculo

Retorna todas as manutencoes de um veiculo especifico.

**Endpoint:** `GET /maintenances/vehicle/{vehicleId}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| vehicleId | long | ID do veiculo |

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "description": "Troca de oleo e filtro de oleo",
    "date": "2026-05-12",
    "value": 350.00,
    "mileageAtTime": 25000,
    "nextOilChangeMileage": 35000,
    "vehicleId": 1,
    "vehiclePlate": "ABC1234"
  }
]
```

**cURL:**
```bash
curl http://localhost:8081/api/maintenances/vehicle/1
```

---

### 10. Resumo de Gastos do Veiculo

Retorna um resumo financeiro e de manutencao de um veiculo.

**Endpoint:** `GET /maintenances/vehicle/{vehicleId}/expenses`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| vehicleId | long | ID do veiculo |

**Response 200 OK:**
```json
{
  "vehicleId": 1,
  "vehiclePlate": "ABC1234",
  "vehicleModel": "Onix",
  "currentMileage": 25000,
  "nextOilChangeMileage": 35000,
  "totalSpent": 1550.00,
  "totalMaintenances": 2
}
```

**Descricao dos campos:**
| Campo | Descricao |
|-------|-----------|
| vehicleId | ID do veiculo |
| vehiclePlate | Placa do veiculo |
| vehicleModel | Modelo do veiculo |
| currentMileage | Quilometragem atual |
| nextOilChangeMileage | Proxima troca de oleo (se houver) |
| totalSpent | Total gasto em manutencoes |
| totalMaintenances | Quantidade de manutencoes |

**cURL:**
```bash
curl http://localhost:8081/api/maintenances/vehicle/1/expenses
```

---

### 11. Atualizar Manutencao

Atualiza os dados de uma manutencao existente.

**Endpoint:** `PUT /maintenances/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID da manutencao |

**Request Body:**
```json
{
  "description": "Troca de oleo, filtro e velas",
  "date": "2026-05-12",
  "value": 450.00,
  "mileageAtTime": 25000,
  "vehicleId": 1
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "description": "Troca de oleo, filtro e velas",
  "date": "2026-05-12",
  "value": 450.00,
  "mileageAtTime": 25000,
  "nextOilChangeMileage": 35000,
  "vehicleId": 1,
  "vehiclePlate": "ABC1234"
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8081/api/maintenances/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Troca de oleo, filtro e velas",
    "date": "2026-05-12",
    "value": 450.00,
    "mileageAtTime": 25000,
    "vehicleId": 1
  }'
```

---

### 12. Deletar Manutencao

Remove um registro de manutencao.

**Endpoint:** `DELETE /maintenances/{id}`

**Path Parameters:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| id | long | ID da manutencao |

**Response 204 No Content:** *(sem body)*

**cURL:**
```bash
curl -X DELETE http://localhost:8081/api/maintenances/1
```

---

## Cenarios Completos de Uso

### Cenario 1: Cadastro Completo de Veiculo e Manutencoes

```bash
# 1. Cadastrar veiculo
curl -X POST http://localhost:8081/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"plate":"XYZ9B99","model":"Corolla","brand":"Toyota","year":2023,"mileage":5000}'

# Resposta: { "id": 2, ... }

# 2. Registrar primeira manutencao (troca de oleo dos 10.000)
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{
    "description":"Troca de oleo sintetico",
    "date":"2026-01-15",
    "value":280.00,
    "mileageAtTime":10000,
    "vehicleId":2
  }'

# Resposta: nextOilChangeMileage = 20000

# 3. Registrar revisao dos 20.000
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{
    "description":"Revisao completa dos 20.000 km",
    "date":"2026-06-20",
    "value":1500.00,
    "mileageAtTime":20000,
    "vehicleId":2
  }'

# 4. Consultar resumo de gastos
curl http://localhost:8081/api/maintenances/vehicle/2/expenses

# Resposta esperada:
# {
#   "vehicleId": 2,
#   "vehiclePlate": "XYZ9B99",
#   "vehicleModel": "Corolla",
#   "currentMileage": 20000,
#   "nextOilChangeMileage": 20000,
#   "totalSpent": 1780.00,
#   "totalMaintenances": 2
# }
```

### Cenario 2: Tentativa de Fraude (KM Menor)

```bash
# Tentar registrar manutencao com KM menor que a atual
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{
    "description":"Troca de oleo",
    "date":"2026-05-12",
    "value":300.00,
    "mileageAtTime":5000,
    "vehicleId":2
  }'

# Resposta 400:
# "A quilometragem da manutencao (5000) nao pode ser menor 
#  que a quilometragem atual do veiculo (20000)"
```

### Cenario 3: Atualizacao Automatica de KM

```bash
# Veiculo esta com 20.000 km
# Registrar manutencao com 22.000 km
curl -X POST http://localhost:8081/api/maintenances \
  -H "Content-Type: application/json" \
  -d '{
    "description":"Alinhamento e balanceamento",
    "date":"2026-07-10",
    "value":180.00,
    "mileageAtTime":22000,
    "vehicleId":2
  }'

# Consultar veiculo
curl http://localhost:8081/api/vehicles/2

# Resposta: mileage agora e 22000 (atualizado automaticamente!)
```

---

## Codigos de Status HTTP

| Status | Significado | Quando Ocorre |
|--------|-------------|---------------|
| 200 | OK | Requisicao bem-sucedida (GET, PUT) |
| 201 | Created | Recurso criado com sucesso (POST) |
| 204 | No Content | Recurso removido com sucesso (DELETE) |
| 400 | Bad Request | Dados invalidos ou regra de negocio violada |
| 404 | Not Found | Recurso nao encontrado |
| 500 | Internal Server Error | Erro inesperado no servidor |

---

## Dicas e Boas Praticas

1. **Sempre verifique o `nextOilChangeMileage`** ao registrar trocas de oleo
2. **A quilometragem do veiculo e atualizada automaticamente** quando a manutencao tem KM maior
3. **Use o endpoint de expenses** para acompanhar seus gastos
4. **A placa e normalizada automaticamente** (maiusculas, sem hifen)
5. **Veiculos excluidos removem todas as manutencoes** em cascade
