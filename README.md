# microservices-lab

Loja online construída como laboratório de estudo de microsserviços com Java e Angular. O projeto segue a stack Spring Boot 4, Angular 22,
Keycloak 26, Kafka em modo KRaft.

> **Status:** em construção. As seções marcam o que já existe (✅) e o que está planejado (🔜).
> Portas e detalhes de itens planejados podem mudar durante o desenvolvimento.

## Visão geral da arquitetura

```
                        ┌────────────┐
   Angular (SPA) ─────► │ API Gateway│ ──── valida o token (Keycloak)
                        └─────┬──────┘
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   product-service     order-service ───────► inventory-service
      (MongoDB)          (MySQL)   chamada HTTP    (MySQL)
                            │       + Resilience4j
                            ▼
                          Kafka ───► notification-service
```

Fluxo principal: o cliente autentica no Keycloak, o front-end chama o gateway com o token, o `order-service` consulta o `inventory-service` antes de aceitar o pedido e, ao concluir, publica um evento no Kafka que o notification-service consome.

## Serviços

| Serviço | Porta | Armazenamento | Responsabilidade | Status |
| --- | --- | --- | --- | --- |
| `product-service` | 8080 | MongoDB | Cadastro e listagem de produtos | ✅ |
| `order-service` | 8081 | MySQL (`order_service`) | Criação de pedidos | ✅ (consulta ao estoque 🔜) |
| `inventory-service` | 8082 | MySQL (`inventory_service`) | Consulta de estoque por SKU | ✅ |
| `api-gateway` | a definir | — | Ponto único de entrada, roteamento e segurança | 🔜 |
| `notification-service` | a definir | — | Consome eventos de pedido e envia notificações | 🔜 |
| `frontend` (Angular) | 4200 | — | Interface web com login | 🔜 |

### Endpoints atuais

| Serviço | Endpoint |
| --- | --- |
| `product-service` | `POST /api/product`, `GET /api/product` |
| `order-service` | `POST /api/order` |
| `inventory-service` | `GET /api/inventory?skuCode=...&quantity=...` |

Cada serviço é dono do próprio banco. As tabelas do MySQL são criadas pelo Flyway (`src/main/resources/db/migration`), e o Hibernate apenas valida o esquema (`ddl-auto=validate`).

## Infraestrutura

| Componente | Uso | Status |
| --- | --- | --- |
| MongoDB 7.0.5 | Dados do `product-service` | ✅ |
| MySQL 8.4 | Um servidor com dois bancos (`order_service`, `inventory_service`) | ✅ |
| Keycloak 26.8 | Autenticação e autorização (OAuth2 / OpenID Connect) | 🔜 |
| Kafka (KRaft, sem ZooKeeper) | Eventos de pedido | 🔜 |
| Stack de observabilidade (OpenTelemetry, métricas, logs e traces) | Monitoramento | 🔜 |
| Kubernetes local (kind) | Implantação dos serviços | 🔜 |

## Tecnologias

- Java 21, Spring Boot 4.1.2, Maven (wrapper) ✅
- Spring Cloud 2025.1.x: Gateway, clientes HTTP declarativos 🔜
- Flyway, Bean Validation, Lombok ✅
- Resilience4j: circuit breaker, retry e timeout 🔜
- OpenAPI (documentação das APIs) 🔜
- Angular 22 com login via OIDC 🔜
- Testcontainers, Rest Assured, RestTestClient e WireMock (testes) ✅ / 🔜 (WireMock)
- Docker e Docker Compose ✅, Kubernetes 🔜

## Roteiro de desenvolvimento

| Aula | Tema | Pontos-chave | Status |
| --- | --- | --- | --- |
| 1 | `product-service` com MongoDB e testes | Camadas, DTOs, validação, testes com Testcontainers | ✅ |
| 2 | `order-service` e `inventory-service` com MySQL e Flyway | Um `docker-compose.yml` na raiz, migrações versionadas, `ddl-auto=validate` | ✅ |
| 3 | Comunicação entre serviços, WireMock, validação e erros | Order consulta o Inventory por cliente HTTP; respostas de erro padronizadas (nada de `RuntimeException` virando 500); `skuCode` no produto | 🔜 |
| 4 | API Gateway | Roteamento para os três serviços; ponto único de entrada | 🔜 |
| 5 | Keycloak | Gateway valida o token; discussão do desvio: os serviços ainda ficam acessíveis direto nas portas 8080 a 8082 | 🔜 |
| 6 | Resilience4j | Circuit breaker, retry e timeout; prova das transições de estado e dos contadores de chamadas falhas e rejeitadas | 🔜 |
| 7 | OpenAPI | Swagger agregado; liberar `/swagger-ui/**`, `/v3/api-docs/**` e `/aggregate/**` na segurança | 🔜 |
| 8 | Angular: fundamentos | Listagem de produtos e criação de pedidos consumindo a API | 🔜 |
| 9 | Angular: login OIDC | Interceptor de token, CORS no gateway, dados do usuário (e-mail) no pedido | 🔜 |
| 10 | Kafka (KRaft) e `notification-service` | Evento de pedido publicado e consumido; e-mail do usuário vindo do pedido | 🔜 |
| 11 | Observabilidade | Starter oficial do OpenTelemetry (no lugar de Brave e Zipkin); propagação do trace no `RestClient` e no Kafka | 🔜 |
| 12 | Imagens e stack completa no Compose | Dockerfile dos serviços e do Angular (nginx); URLs externalizadas em propriedades | 🔜 |
| 13 | Kubernetes com kind | `kind load docker-image` em vez de registry; manifestos; endereço do Keycloak acessível dentro e fora do cluster | 🔜 |

## Pré-requisitos

- JDK 21 (`java -version`)
- Docker Desktop em execução
- Git
- Node.js e npm (somente a partir da Aula 8, para o Angular)

O Maven não precisa estar instalado: cada serviço traz o Maven Wrapper (`mvnw`).

## Como executar (estado atual)

### 1. Subir a infraestrutura

Na raiz do repositório (igual no Mac e no Windows):

```bash
docker compose up -d
docker compose ps
```

Espere o contêiner `mysql` aparecer como `healthy`. O script `infra/mysql/init.sql`
cria os bancos `order_service` e `inventory_service` na primeira subida.

### 2. Subir os serviços

Abra um terminal para cada serviço.

Mac:

```bash
cd product-service
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
cd product-service
.\mvnw.cmd spring-boot:run
```

Repita em `order-service` e `inventory-service`.

### 3. Testar manualmente

Mac:

```bash
curl -i -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{"name":"Pixel 8","description":"Smartphone","price":900}'

curl http://localhost:8080/api/product

curl -i -X POST http://localhost:8081/api/order \
  -H "Content-Type: application/json" \
  -d '{"skuCode":"iphone_15","price":1000,"quantity":1}'

curl "http://localhost:8082/api/inventory?skuCode=iphone_15&quantity=100"
```

Windows (PowerShell):

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/product `
  -ContentType "application/json" `
  -Body '{"name":"Pixel 8","description":"Smartphone","price":900}'

Invoke-RestMethod http://localhost:8080/api/product

Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/order `
  -ContentType "application/json" `
  -Body '{"skuCode":"iphone_15","price":1000,"quantity":1}'

Invoke-RestMethod "http://localhost:8082/api/inventory?skuCode=iphone_15&quantity=100"
```

O estoque inicial é de 100 unidades para `iphone_15`, `pixel_8`, `galaxy_24` e
`oneplus_12`.

## Testes

Os testes sobem os próprios bancos com Testcontainers. O Docker precisa estar
ligado; o `docker compose` não.

Mac:

```bash
cd inventory-service
./mvnw verify
```

Windows (PowerShell):

```powershell
cd inventory-service
.\mvnw.cmd verify
```

## Comandos úteis

```bash
# Parar a infraestrutura, mantendo os dados
docker compose down

# Parar e apagar os dados (necessário após alterar o init.sql)
docker compose down -v

# Ver os bancos criados
docker exec mysql mysql -uroot -pmysql -e "SHOW DATABASES;"

# Ver as migrações aplicadas
docker exec mysql mysql -uroot -pmysql -e "SELECT version, description, success FROM inventory_service.flyway_schema_history;"
```

## Estrutura

```
microservices-lab/
├── docker-compose.yml        # MongoDB e MySQL (Keycloak, Kafka etc. serão adicionados)
├── infra/mysql/init.sql      # criação dos bancos
├── product-service/          ✅
├── order-service/            ✅
├── inventory-service/        ✅
├── api-gateway/              🔜
├── notification-service/     🔜
├── frontend/                 🔜 (Angular)
└── k8s/                      🔜 (manifestos Kubernetes)
```

Cada serviço Spring segue as camadas `controller`, `service`, `repository`, `model` e `dto`.

## Decisões de projeto

- **Um banco por serviço (lógico):** cada serviço é o único dono do seu esquema e os
  serviços se comunicam por API ou evento, nunca por consulta direta ao banco do outro.
  Localmente os dois bancos MySQL compartilham um servidor; em produção seriam separados.
- **Migrações versionadas (Flyway):** o esquema nunca é gerado pelo Hibernate. Uma
  migração aplicada não é editada; correções entram em uma nova versão.
- **DTOs na borda da API:** entidades não são expostas, e o cliente não envia campos
  controlados pelo servidor (`id`, `orderNumber`).
- **Commits pequenos** com mensagens convencionais em português.

## Observações

As credenciais em `docker-compose.yml` e nos arquivos `application.properties` servem apenas ao ambiente local de estudo. Não use esses valores fora dele.
