# Documentacao Tecnica - CarLog API

## Indice

1. [Visao Geral](#visao-geral)
2. [Stack Tecnologica](#stack-tecnologica)
3. [Arquitetura do Sistema](#arquitetura-do-sistema)
4. [Modelo de Dados](#modelo-de-dados)
5. [Camadas da Aplicacao](#camadas-da-aplicacao)
6. [Fluxo de Requisicao](#fluxo-de-requisicao)
7. [Regras de Negocio Detalhadas](#regras-de-negocio-detalhadas)
8. [Tratamento de Excecoes](#tratamento-de-excecoes)
9. [Validacoes Customizadas](#validacoes-customizadas)
10. [Configuracoes](#configuracoes)
11. [Testes](#testes)
12. [Deploy](#deploy)

---

## Visao Geral

O CarLog API e uma aplicacao RESTful desenvolvida em Java com Spring Boot, projetada para gerenciar veiculos e suas manutencoes. O sistema oferece funcionalidades completas de CRUD, regras de negocio automatizadas e validacoes robustas.

### Objetivos do Projeto

- Demonstrar dominio do ecossistema Spring
- Aplicar padroes de projeto e boas praticas
- Criar uma API RESTful bem documentada e testavel
- Implementar regras de negocio reais e uteis

---

## Stack Tecnologica

| Camada | Tecnologia | Versao |
|--------|-----------|--------|
| Linguagem | Java | 17 LTS |
| Framework | Spring Boot | 3.2.0 |
| Persistencia | Spring Data JPA | 3.2.0 |
| Web | Spring Web | 3.2.0 |
| Validacao | Hibernate Validator | 8.0.1 |
| Banco Dev | H2 Database | 2.2.224 |
| Banco Prod | PostgreSQL | 15+ |
| Build | Maven | 3.9+ |
| Util | Lombok | 1.18.30 |

### Por que essas tecnologias?

- **Java 17:** Versao LTS com melhorias de performance e novos recursos de linguagem
- **Spring Boot 3:** Framework maduro, com auto-configuracao e grande comunidade
- **Spring Data JPA:** Abstracao poderosa para acesso a dados, reduzindo codigo boilerplate
- **H2:** Banco em memoria ideal para desenvolvimento e testes rapidos
- **PostgreSQL:** Banco relacional robusto e gratuito para producao
- **Lombok:** Reduz significativamente o codigo boilerplate (getters, setters, constructors)

---

## Arquitetura do Sistema

### Padrao em Camadas (Layered Architecture)

```
+-----------------------+
|  Presentation Layer   |  Controller + DTO + Validation
|  (Camada de Apresent) |
+-----------------------+
           |
           v
+-----------------------+
|   Business Layer      |  Service + Rules
|  (Camada de Negocio)  |
+-----------------------+
           |
           v
+-----------------------+
|   Data Access Layer   |  Repository + Entity
|  (Camada de Dados)    |
+-----------------------+
           |
           v
+-----------------------+
|   Database Layer      |  H2 / PostgreSQL
|  (Banco de Dados)     |
+-----------------------+
```

### Beneficios desta arquitetura

1. **Separation of Concerns:** Cada camada tem uma responsabilidade unica
2. **Testabilidade:** Facil testar cada camada isoladamente
3. **Manutencao:** Mudancas em uma camada nao afetam as outras
4. **Escalabilidade:** Facil adicionar novas funcionalidades

---

## Modelo de Dados

### Entidade: Vehicle (Veiculo)

Representa um veiculo cadastrado no sistema.

```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 8)
    private String plate;          // Placa do veiculo
    
    @Column(nullable = false)
    private String model;          // Modelo (ex: Onix)
    
    @Column(nullable = false)
    private String brand;          // Marca (ex: Chevrolet)
    
    @Column(nullable = false, name = "\"year\"")
    private Integer year;          // Ano de fabricacao
    
    @Column(nullable = false)
    private Integer mileage;       // Quilometragem atual
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Maintenance> maintenances;
}
```

### Entidade: Maintenance (Manutencao)

Representa um servico de manutencao realizado em um veiculo.

```java
@Entity
@Table(name = "maintenances")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String description;    // Descricao do servico
    
    @Column(nullable = false)
    private LocalDate date;        // Data da manutencao
    
    @Column(nullable = false, name = "\"value\"")
    private BigDecimal value;      // Valor gasto
    
    @Column(nullable = false)
    private Integer mileageAtTime; // KM no momento do servico
    
    @Column
    private Integer nextOilChangeMileage; // Proxima troca de oleo
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;       // Veiculo vinculado
}
```

### Relacionamento

- **Vehicle 1 : N Maintenance**
- Um veiculo pode ter varias manutencoes
- Uma manutencao pertence a apenas um veiculo
- Cascade ALL: Ao excluir um veiculo, todas as manutencoes sao excluidas

---

## Camadas da Aplicacao

### 1. Controller Layer

Responsavel por receber as requisicoes HTTP, validar os dados de entrada e retornar as respostas apropriadas.

**VehicleController:**
```java
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    
    @PostMapping
    public ResponseEntity<VehicleDTO> create(@Valid @RequestBody VehicleDTO dto) {
        // Valida os dados e delega para o Service
    }
    
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> findAll() {
        // Retorna lista de veiculos
    }
    
    // ... outros endpoints
}
```

**Anotacoes importantes:**
- `@RestController`: Indica que a classe e um controller REST
- `@RequestMapping`: Define o path base
- `@Valid`: Dispara a validacao do Bean Validation
- `@RequestBody`: Converte o JSON do body para o DTO
- `@PathVariable`: Extrai valores da URL

### 2. Service Layer

Contem a logica de negocio da aplicacao. E o "cerebro" do sistema.

**VehicleService:**
```java
@Service
@RequiredArgsConstructor
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    @Transactional
    public VehicleDTO create(VehicleDTO dto) {
        // Normaliza placa, verifica duplicidade, salva no banco
    }
    
    // ... outros metodos
}
```

**MaintenanceService (logica principal):**
```java
@Service
@RequiredArgsConstructor
public class MaintenanceService {
    
    private static final int OIL_CHANGE_INTERVAL_KM = 10000;
    
    @Transactional
    public MaintenanceResponseDTO registerMaintenance(MaintenanceDTO dto) {
        // 1. Busca o veiculo
        // 2. Valida quilometragem
        // 3. Atualiza KM do veiculo se necessario
        // 4. Calcula proxima troca de oleo
        // 5. Salva a manutencao
    }
}
```

**Anotacoes importantes:**
- `@Service`: Indica que a classe e um servico de negocio
- `@Transactional`: Garante atomicidade das operacoes
- `@RequiredArgsConstructor`: Cria construtor com dependencias (boas praticas)

### 3. Repository Layer

Abstracao para acesso ao banco de dados. Utiliza Spring Data JPA.

```java
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlate(String plate);
    boolean existsByPlate(String plate);
}

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByVehicleId(Long vehicleId);
    
    @Query("SELECT COALESCE(SUM(m.value), 0) FROM Maintenance m WHERE m.vehicle.id = :vehicleId")
    BigDecimal sumValuesByVehicleId(@Param("vehicleId") Long vehicleId);
}
```

**Beneficios do Spring Data JPA:**
- Nao precisa escrever queries SQL basicas
- Metodos de consulta gerados automaticamente por convencao de nomes
- Suporte a queries JPQL customizadas
- Paginacao e ordenacao nativas

### 4. DTO Layer

Data Transfer Objects controlam o que entra e sai da API.

**Por que usar DTOs?**
- Evita expor a estrutura interna das entidades
- Permite validacoes diferentes para criacao e atualizacao
- Reduz o trafego de dados na rede
- Previne ataques de mass assignment

**VehicleDTO:**
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleDTO {
    private Long id;
    
    @NotBlank(message = "A placa e obrigatoria")
    @ValidPlate(message = "Placa invalida")
    private String plate;
    
    @NotBlank(message = "O modelo e obrigatorio")
    @Size(min = 2, max = 100)
    private String model;
    
    // ... outros campos com validacoes
}
```

---

## Fluxo de Requisicao

### Exemplo: Registrar uma Manutencao

```
Cliente (Postman)
       |
       | POST /api/maintenances
       | Body: { "description": "Troca de oleo", ... }
       v
+-------------+
|  Controller |  -> @Valid valida o DTO
| Maintenance |  -> Chama o Service
+-------------+
       |
       v
+-------------+
|   Service   |  -> Busca veiculo no Repository
| Maintenance |  -> Valida regra de negocio (KM)
|             |  -> Atualiza KM do veiculo
|             |  -> Calcula proxima troca de oleo
|             |  -> Salva manutencao
+-------------+
       |
       v
+-------------+
|  Repository |  -> Executa INSERT no H2
| Maintenance |  -> Retorna entidade salva
+-------------+
       |
       v
+-------------+
|   Service   |  -> Converte para ResponseDTO
+-------------+
       |
       v
+-------------+
|  Controller |  -> Retorna ResponseEntity (201 Created)
+-------------+
       |
       v
Cliente recebe JSON com dados da manutencao + nextOilChangeMileage
```

---

## Regras de Negocio Detalhadas

### RGN001: Placa Unica
- **Descricao:** Nao e permitido cadastrar dois veiculos com a mesma placa
- **Implementacao:** `VehicleService.create()` verifica `existsByPlate()` antes de salvar
- **Excecao:** `BusinessException` com HTTP 400

### RGN002: Normalizacao de Placa
- **Descricao:** Placas sao armazenadas sem hifen e em maiusculas
- **Implementacao:** `plate.trim().toUpperCase().replace("-", "")`
- **Exemplo:** `abc-1234` vira `ABC1234`

### RGN003: Quilometragem Crescente
- **Descricao:** Uma manutencao so pode ser registrada com KM >= KM atual do veiculo
- **Implementacao:** `MaintenanceService.registerMaintenance()`
- **Excecao:** `BusinessException` com mensagem detalhada

### RGN004: Atualizacao Automatica de KM
- **Descricao:** Se a manutencao tem KM maior que o veiculo, o veiculo e atualizado automaticamente
- **Implementacao:** `if (dto.getMileageAtTime() > vehicle.getMileage())`
- **Beneficio:** Evita desatualizacao da quilometragem

### RGN005: Calculo de Proxima Troca de Oleo
- **Descricao:** Se a descricao contem "oleo", calcula automaticamente proxima troca (atual + 10.000 km)
- **Implementacao:**
```java
if (descLower.contains("oleo")) {
    nextOilChange = dto.getMileageAtTime() + OIL_CHANGE_INTERVAL_KM;
}
```
- **Constante:** `OIL_CHANGE_INTERVAL_KM = 10000`

### RGN006: Valor Minimo Zero
- **Descricao:** O valor da manutencao nao pode ser negativo
- **Implementacao:** `@DecimalMin(value = "0.0", inclusive = true)`
- **Validacao:** Bean Validation automatico

---

## Tratamento de Excecoes

O sistema utiliza `@RestControllerAdvice` para tratamento global de excecoes.

### Tipos de Excecao

| Excecao | Causa | HTTP Status | Resposta |
|---------|-------|-------------|----------|
| `ResourceNotFoundException` | Recurso nao encontrado | 404 | `{ status, message, timestamp }` |
| `BusinessException` | Regra de negocio violada | 400 | `{ status, message, timestamp }` |
| `MethodArgumentNotValidException` | Validacao de campo falhou | 400 | `{ status, message, timestamp, errors: { campo: mensagem } }` |
| `Exception` (generica) | Erro inesperado | 500 | `{ status, message, timestamp }` |

### Estrutura de Resposta de Erro

```json
{
  "status": 400,
  "message": "Erro de validacao",
  "timestamp": "2026-05-12T16:08:08.0037443",
  "errors": {
    "plate": "Placa invalida. Use o formato AAA-0000 ou AAA0A00",
    "year": "Ano deve ser no minimo 1900"
  }
}
```

---

## Validacoes Customizadas

### ValidPlate

Validador customizado para placas brasileiras.

```java
@Documented
@Constraint(validatedBy = PlateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlate {
    String message() default "Placa invalida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Expressao Regular:**
```java
Pattern.compile("^[A-Z]{3}[-]?[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$")
```

**Formatos aceitos:**
- `AAA-0000` (com hifen)
- `AAA0000` (sem hifen)
- `AAA0A00` (Mercosul)

---

## Configuracoes

### application.properties (Desenvolvimento)

```properties
# Perfil ativo
spring.profiles.active=dev

# H2 Database
spring.datasource.url=jdbc:h2:mem:carlogdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Server
server.port=8081
server.error.include-message=always
server.error.include-binding-errors=always
```

### application-prod.properties (Producao - exemplo)

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/carlog
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
```

---

## Testes

### Tipos de Testes Recomendados

1. **Testes Unitarios:** Testar Service em isolamento com Mockito
2. **Testes de Integracao:** Testar Controller com `@WebMvcTest` ou `@SpringBootTest`
3. **Testes de Repositorio:** Testar queries com `@DataJpaTest`

### Exemplo de Teste Unitario (Service)

```java
@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @InjectMocks
    private MaintenanceService maintenanceService;

    @Test
    void shouldCalculateNextOilChange() {
        // Given
        Vehicle vehicle = Vehicle.builder().id(1L).mileage(15000).build();
        MaintenanceDTO dto = MaintenanceDTO.builder()
            .description("Troca de oleo")
            .mileageAtTime(20000)
            .vehicleId(1L)
            .build();
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        
        // When
        MaintenanceResponseDTO result = maintenanceService.registerMaintenance(dto);
        
        // Then
        assertEquals(30000, result.getNextOilChangeMileage());
    }
}
```

---

## Deploy

### Build para Producao

```bash
# Gerar JAR executavel
mvn clean package

# O arquivo sera gerado em:
# target/carlog-api-1.0.0.jar

# Executar o JAR
java -jar target/carlog-api-1.0.0.jar

# Ou com profile de producao
java -jar -Dspring.profiles.active=prod target/carlog-api-1.0.0.jar
```

### Docker (Futuro)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/carlog-api-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USER=postgres
      - DB_PASSWORD=secret
    depends_on:
      - db
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: carlog
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
```

---

## Consideracoes de Seguranca

1. **Validacao de Entrada:** Todas as entradas sao validadas com Bean Validation
2. **SQL Injection:** Protegido pelo uso de JPA/Hibernate (parametros preparados)
3. **Mass Assignment:** Protegido pelo uso de DTOs (campos sensiveis nao sao expostos)
4. **CORS:** Configurado para permitir requisicoes de qualquer origem em desenvolvimento

### Melhorias Futuras de Seguranca

- [ ] Implementar Spring Security com JWT
- [ ] Adicionar rate limiting
- [ ] Implementar audit logging
- [ ] Configurar HTTPS

---

## Performance

### Otimizacoes Implementadas

1. **FetchType.LAZY:** Relacionamentos ManyToOne carregam sob demanda
2. **Indices:** Placa possui indice unico para buscas rapidas
3. **Transaction Boundaries:** Operacoes atomicas com `@Transactional`

### Otimizacoes Futuras

- [ ] Cache com Spring Cache + Redis
- [ ] Paginacao em listagens grandes
- [ ] Indices adicionais em campos de busca frequentes

---

## Conclusao

O CarLog API foi desenvolvido como um projeto pessoal para demonstrar competencias em desenvolvimento backend com Java e Spring Boot. O sistema e funcional, bem estruturado e pronto para evoluir com novas funcionalidades.

Para duvidas ou sugestoes, entre em contato!
