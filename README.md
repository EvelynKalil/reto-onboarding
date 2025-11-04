# Proyecto Onboarding Reactivo

Microservicio reactivo desarrollado en **Java 17 + Spring Boot 3 + WebFlux**, siguiendo la arquitectura limpia de **Bancolombia Scaffold**.  
Implementa integración con **PostgreSQL (R2DBC)**, **Redis** como caché reactivo, **AWS SQS** (emulado con LocalStack) y un consumidor HTTP (`ReqresAdapter`) para poblar datos de usuarios.

---

## Estructura de carpetas

```
onboarding-reactivo/
├── applications/
│   └── app-service/             # Configuración de arranque (Spring Boot)
├── domain/
│   ├── model/                   # Entidades de dominio (User)
│   └── usecase/                 # Lógica de negocio (UserUseCase)
├── infrastructure/
│   ├── entry-points/
│   │   ├── reactive-web/        # Routers y Handlers WebFlux
│   │   └── sqs-listener/        # Escucha mensajes SQS
│   └── driven-adapters/
│       ├── dynamo-db/           #Persistencia de eventos en tabla DynamoDB (user-events-table)
│       ├── r2dbc-postgresql/    # Repositorio reactivo
│       ├── redis/               # Cache reactivo con Redis
│       ├── rest-consumer/       # Cliente HTTP Reqres
│       └── sqs-sender/          # Emisor de eventos SQS
└
└── docker-compose.yml
```

---

## Tecnologías principales

| Componente | Tecnología | Descripción |
|-------------|-------------|--------------|
| Backend | **Spring Boot 3 + WebFlux** | Framework reactivo no bloqueante |
| DB | **PostgreSQL + R2DBC** | Conexión totalmente reactiva |
| Cache | **Redis** | Caché reactivo para usuarios |
| Mensajería | **AWS SQS (LocalStack)** | Comunicación asíncrona y eventos |
| Persistencia de eventos | **AWS DynamoDB (LocalStack)** | Guarda el historial de eventos recibidos desde SQS en la tabla `user-events-table` |
| Consumer | **ReqresAdapter** | Consulta usuarios desde `reqres.in` |
| Contenedores | **Podman Compose / Docker Compose** | Orquestación local |
| Arquitectura | **Clean Architecture (Bancolombia Scaffold)** | Separación por capas y adaptadores |

---

## 🧩 Arquitectura lógica

```
                ┌──────────────────────────────┐
                │         Entry Point           │
                │  (WebFlux Router + Handler)   │
                └─────────────┬────────────────┘
                              │
                              ▼
                ┌──────────────────────────────┐
                │        Use Case Layer         │
                │  (UserUseCase, reglas core)   │
                └─────────────┬────────────────┘
                              │
                              ▼
        ┌──────────────────────────────────────────────────────────┐
        │                    Driven Adapters                       │
        │ ┌────────┬────────┬──────────┬────────────┬───────────┐  │
        │ │ R2DBC   │ Redis  │ Reqres   │ SQS Sender │ DynamoDB  │  │
        │ │ Postgres│ Cache  │ Consumer │ + Listener │ Eventos   │  │
        │ └────────┴────────┴──────────┴────────────┴───────────┘  │
        └──────────────────────────────────────────────────────────┘
```

---

## Configuración de entorno

Archivo `.env` para entorno local:

```
POSTGRES_DB=usersdb
POSTGRES_USER=***
POSTGRES_PASSWORD=***
SPRING_PROFILES_ACTIVE=local
AWS_SQS_QUEUE_URL=http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/user-events-table
```

---

## Despliegue con Podman Compose

### 1️ Construir la aplicación

```bash
./gradlew clean build
```

Genera el `.jar` en `app-service/build/libs/`.

---

### 2️ Levantar contenedores

```bash
podman-compose up -d
```

Servicios disponibles:

| Servicio | Puerto | Descripción |
|-----------|---------|-------------|
| onboarding-postgres | 5432 | Base de datos reactiva |
| redis_onboarding | 6379 | Caché reactiva |
| onboarding_localStack | 4566 | Emulador de AWS (SQS) |
| onboarding-reactivo | 8080 | Aplicación principal |

---

### 3️ Inicializar la cola SQS (LocalStack)

Cada vez que reinicies el contenedor de LocalStack, se pierden las colas.  
Creala manualmente:

#### Manual:
```bash
podman exec -it onboarding-localstack awslocal sqs create-queue --queue-name user-events-table
```

---

### 4️ Verificar recursos

```bash
aws --endpoint-url=http://localhost:4566 sqs list-queues
```

Debe mostrar:
```
http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/user-events-table
```

---

### 5. lanzar app

```bash
./gradlew bootRun 
```

## Endpoints principales

### Crear usuario por ID

```bash
POST http://localhost:8080/users/1
```

Respuesta:

```json
{
  "id": 1,
  "email": "george.bluth@reqres.in",
  "firstName": "George",
  "lastName": "Bluth",
  "avatar": "https://reqres.in/img/faces/1-image.jpg"
}
```
Si el usuario no existe localmente:
1. Se busca en Redis.
2. Si no está, se consulta PostgreSQL.
3. Si tampoco, se obtiene de `reqres.in`.
4. Se guarda en DB y cache.
5. Se envía un evento a SQS.
6. El listener de SQS procesa el mensaje y lo almacena en DynamoDB (user-events-table), como registro histórico del evento.
---

### Obtener usuario por ID

```bash
GET http://localhost:8080/users/1
```
```json
{
  "id": 1,
  "email": "george.bluth@reqres.in",
  "firstName": "George",
  "lastName": "Bluth",
  "avatar": "https://reqres.in/img/faces/1-image.jpg"
}
```

### Obtener usuario por nombre

```bash
GET http://localhost:8080/users?name={nombre}
```
```json
[
  {
    "id": 1,
    "email": "george.bluth@reqres.in",
    "firstName": "George",
    "lastName": "Bluth",
    "avatar": "https://reqres.in/img/faces/1-image.jpg"
  }
]

```

### Obtener todos los usuarios

```bash
GET http://localhost:8080/users
```
```json
[
  {
    "id": 1,
    "email": "george.bluth@reqres.in",
    "firstName": "George",
    "lastName": "Bluth",
    "avatar": "https://reqres.in/img/faces/1-image.jpg"
  },
  {
    "id": 2,
    "email": "janet.weaver@reqres.in",
    "firstName": "Janet",
    "lastName": "Weaver",
    "avatar": "https://reqres.in/img/faces/2-image.jpg"
  }
]

```


## Flujo de caché Redis

```text
[Cliente] → [Handler] → [UserUseCase]
  ↳ RedisRepositoryAdapter.getUserFromCache(id)
      ↳ si vacío → repository.findById(id)
          ↳ si vacío → ReqresAdapter.fetchUserById(id)
              ↳ guardar en Postgres + Redis
                ↳ emitir evento a SQS
                  ↳ listener procesa evento y guarda en DynamoDB (user-events-table)
```

---

## Tests

### Tipos de pruebas

| Nivel | Framework | Archivo |
|-------|------------|----------|
| Unitarias | **JUnit 5 + Mockito** | `UserUseCaseTest`, `RedisRepositoryAdapterTest` |
| Router/Handler | **WebFluxTest** | `RouterRestTest`, `HandlerTest` |
| Integración | **SpringBootTest + WebTestClient** | `MainApplicationIntegrationTest` |

Ejecución:

```bash
./gradlew test
```

> Usa base de datos en memoria (H2 R2DBC) y mocks de SQS/Redis.

---

## Errores comunes

| Problema | Causa | Solución |
|-----------|--------|-----------|
| `QueueDoesNotExistException` | LocalStack sin cola SQS | Crear cola con AWS CLI o usar `SqsAutoCreateConfig` |
| `Connection refused` Redis | Contenedor no iniciado | `podman ps` → verificar `redis` activo |


---
 
---

## Autoría

**Evelyn Rendón** — Desarrolladora Backend

Proyecto de Onboarding backend para nequi
