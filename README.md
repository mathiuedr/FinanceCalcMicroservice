# Personal Finance Tracker 💰

Микросервисное headless приложение для учета личных финансов с Spring Boot.

## Технологии
- **Java 21** + **Spring Boot 3.5.4**
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Security** + **JWT** - Аутентификация
- **Spring Data JPA** + **H2/PostgreSQL** - База данных
- **Maven** - Сборка проекта

### Архитектура
- Микросервисная архитектура
- Единая точка входа через API Gateway
- Service Discovery с Eureka
- JWT аутентификация
- Reactive programming (WebFlux) в Gateway

## Структура проекта

FinanceCalcMicroservice/
- discovery-service/ # Eureka Server (порт 8761)
- api-gateway/ # API Gateway (порт 8080)
- user-service/ # Сервис пользователей
- transaction-service/ # Сервис транзакций
- common-library/ # Общие утилиты


## Установка и запуск

### Предварительные требования
- Java 21
- Maven 3.6+
- (Опционально) PostgreSQL

### 1. Клонирование репозитория
```bash
git clone https://github.com/yourusername/finance-tracker.git
cd finance-tracker
```

### 2. Сборка проекта
```bash
mvn clean install
```

### 3. Запуск сервисов (в отдельных терминалах)
```bash
# Сервис Discovery (Eureka)
cd discovery-service
mvn spring-boot:run

# User Service
cd user-service
mvn spring-boot:run

# Transaction Service
cd transaction-service
mvn spring-boot:run

# API Gateway
cd api-gateway
mvn spring-boot:run
```

### 4. Проверка работы
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080

## API Endpoints

### Аутентификация

- Все защищённые эндпоинты требуют заголовок:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```
- JWT выдаётся при успешной аутентификации через `/api/v1/user/login`.
- Время жизни токена: **24 часа**.
- Секрет (для разработки):  
  `yoursveryssecuressecretskeysminimums32scharacters`

#### Открытые эндпоинты (без аутентификации)
- `POST /api/v1/user/register`
- `POST /api/v1/user/login`

---

### User Service (`/api/v1/user/**`)

| Метод   | Эндпоинт                     | Тело запроса (JSON)                                      | Ответ (успех)                  | Требует JWT |
|---------|------------------------------|----------------------------------------------------------|--------------------------------|-------------|
| POST    | `/api/v1/user/register`       | ```{ "login": "string", "password": "string", "name": "string" }``` | `201 Created` + сообщение      | ❌          |
| POST    | `/api/v1/user/login`          | ```{ "login": "string", "password": "string" }```        | `200 OK` + JWT-токен (строка)  | ❌          |
| PATCH   | `/api/v1/user/password`       | ```{ "password": "string" }```                           | `200 OK` + "Password updated successfully" | ✅ |
| PATCH   | `/api/v1/user/name`           | ```{ "name": "string" }```                               | `200 OK` + "Name updated successfully"    | ✅ |
| DELETE  | `/api/v1/user/`               | —                                                        | `200 OK` + "User deleted successfully"    | ✅ |

>  Заголовки `User-Id` и `User-Name` автоматически извлекаются из JWT и добавляются в запрос к сервису. Передавать их вручную **не нужно**.

---

### Transaction Service (`/api/v1/transactions/**`)

| Метод   | Эндпоинт                                   | Тело запроса (JSON)                                              | Ответ (успех)                              | Требует JWT |
|---------|--------------------------------------------|------------------------------------------------------------------|--------------------------------------------|-------------|
| POST    | `/api/v1/transactions/`                    | ```{ "type": "DEPOSIT" \| "WITHDRAWAL", "sum": number }```           | `201 Created` + "Transaction successfully created" | ✅ |
| GET     | `/api/v1/transactions/`                    | —                                                                | `200 OK` + массив транзакций               | ✅ |
| PATCH   | `/api/v1/transactions/`                    | ```{ "type": "DEPOSIT" \| "WITHDRAWAL", "sum": number, "transactionId": number }``` | `200 OK` + "Sum of transaction was successfully updated" | ✅ |
| DELETE  | `/api/v1/transactions/?id={transactionId}` | —                                                                | `200 OK` + "Transaction was successfully deleted" | ✅ |

> Поле `User-Id` из JWT автоматически передаётся в сервис. В DELETE-запросе требуется только параметр `id`.

---

### Примеры запросов

#### 1. Регистрация пользователя
```http
POST /api/v1/user/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "login": "john_doe",
  "password": "mySecretPass123",
  "name": "John Doe"
}
```

#### 2. Получение JWT-токена
```http
POST /api/v1/user/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "login": "john_doe",
  "password": "mySecretPass123"
}
```

> Ответ: строка с JWT.

#### 3. Создание транзакции (с токеном)
```http
POST /api/v1/transactions/ HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "type": "EXPENSE",
  "sum": 1500
}
```

#### 4. Удаление транзакции
```http
DELETE /api/v1/transactions/?id=42 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### Ошибки

При отсутствии или недействительности JWT возвращается:

```json
{
  "error": "Missing or invalid Authorization header",
  "timestamp": "2025-11-04T12:00:00Z"
}
```

HTTP-статус: `401 Unauthorized`

---
