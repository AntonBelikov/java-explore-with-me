# Explore With Me

`Explore With Me` — многомодульное backend-приложение на **Java 21 + Spring Boot 3** для публикации, модерации и поиска событий.

Проект разделён на два независимых сервиса:

- **ewm-service** — основной REST API для пользователей, событий, подборок, категорий, заявок на участие и локаций;
- **stats-server** — отдельный сервис статистики просмотров, который принимает данные о посещениях и предоставляет агрегированную статистику.

Дополнительно реализован поиск опубликованных событий внутри заданной географической зоны.

## Возможности

- регистрация и управление пользователями;
- создание и редактирование событий;
- отправка событий на модерацию;
- публикация и отклонение событий администратором;
- публичный поиск и фильтрация событий;
- сортировка событий по дате и просмотрам;
- пагинация;
- подача заявок на участие;
- подтверждение и отклонение заявок;
- формирование тематических подборок;
- работа с категориями;
- работа с локациями;
- поиск событий в радиусе выбранной локации;
- сбор статистики просмотров;
- расчёт `views` и `confirmedRequests`;
- централизованная обработка ошибок;
- интеграционное тестирование;
- Docker-сборка и запуск сервисов.

## Архитектура

Проект реализован как **multi-module Maven application**:

```text
explore-with-me
├── stats
│   ├── stats-dto
│   ├── stats-client
│   └── stats-server
│
└── ewm-service
    └── ewm-server
```

### `ewm-server`

Основной Spring Boot сервис, реализующий бизнес-логику приложения:

- пользователи;
- категории;
- события;
- подборки событий;
- заявки на участие;
- локации;
- публичный поиск;
- интеграция со stats-сервисом.

### `stats-server`

Отдельный Spring Boot сервис для работы со статистикой:

- `POST /hit` — сохранение информации о просмотре;
- `GET /stats` — получение агрегированной статистики;
- хранение статистики в PostgreSQL.

### Взаимодействие сервисов

`ewm-server` и `stats-server` запускаются независимо и взаимодействуют по HTTP.

```text
Client
   │
   ▼
ewm-server
   │
   ├──────────────► stats-server
   │                    │
   │                    ▼
   │                PostgreSQL
   │
   ▼
PostgreSQL
```

## Географический поиск

В проекте реализован поиск опубликованных событий внутри заданной географической зоны.

Локация содержит:

- `name` — название;
- `lat` — широта;
- `lon` — долгота;
- `radiusM` — радиус поиска в метрах.

Для расчёта расстояния используется SQL-функция `distance_m(...)`, которая создаётся при запуске приложения.

## Статистика просмотров

При обращении к публичным эндпоинтам событий основной сервис:

1. отправляет информацию о просмотре в `stats-server`;
2. `stats-server` сохраняет данные;
3. основной сервис запрашивает агрегированную статистику;
4. количество просмотров добавляется в DTO события.

Также рассчитывается количество подтверждённых заявок `confirmedRequests`.

## Технологии

- **Java:** Java 21
- **Spring:** Spring Boot 3.3.2, Spring Web, Spring Data JPA, Spring Validation
- **Persistence:** PostgreSQL 16, H2, Hibernate
- **Testing:** JUnit, Spring Boot Test
- **Build:** Maven
- **Tools:** Git, Docker, Docker Compose, Lombok
- **Code Quality:** Checkstyle, SpotBugs, JaCoCo
- **Architecture:** REST API, multi-module Maven, HTTP service-to-service communication

## Тестирование

В проекте реализованы интеграционные тесты для основного и статистического сервисов.

Покрыты основные сценарии:

- users;
- categories;
- compilations;
- events;
- public API;
- statistics.

### Запуск тестов

```bash
mvn test
```

### Проверка качества кода

```bash
mvn -Pcheck verify
```

### Проверка покрытия

```bash
mvn -Pcoverage verify
```

## Запуск проекта

### Требования

- Java 21;
- Maven 3.9+;
- PostgreSQL;
- Docker и Docker Compose.

### Запуск через Docker Compose

Клонировать репозиторий:

```bash
git clone https://github.com/AntonBelikov/java-explore-with-me.git
cd java-explore-with-me
```

Запустить приложение:

```bash
docker compose up --build
```

После запуска:

- `ewm-server` — `http://localhost:8080`
- `stats-server` — `http://localhost:9090`

Остановить контейнеры:

```bash
docker compose down
```

### Локальный запуск через Maven

Собрать проект:

```bash
mvn clean package
```

Запустить `stats-server`:

```bash
mvn -pl stats/stats-server spring-boot:run
```

Запустить `ewm-server`:

```bash
mvn -pl ewm-service/ewm-server spring-boot:run
```

Для локального запуска необходимо предварительно настроить PostgreSQL и параметры подключения к базам данных.

## API Documentation

API-спецификации находятся в репозитории:

- [`ewm-main-service-spec.json`](./ewm-main-service-spec.json)
- [`ewm-stats-service-spec.json`](./ewm-stats-service-spec.json)

## Структура основного сервиса

Код `ewm-server` организован по feature-based структуре:

```text
ewm-server
├── category
├── compilation
├── event
├── location
├── request
├── stats
├── user
├── error
└── util
```

Внутри функциональных областей используются следующие слои:

```text
controller
service
repository
dto
mapper
model
```

Такое разделение позволяет изолировать бизнес-логику отдельных функциональных областей и упрощает навигацию по проекту.

## Docker

Для сервисов подготовлены отдельные Dockerfile:

- `stats/stats-server/Dockerfile`
- `ewm-service/ewm-server/Dockerfile`

Используется multi-stage build:

1. сборка JAR через Maven;
2. запуск приложения на `eclipse-temurin:21-jre`.

## CI

В проекте настроен GitHub Actions workflow:

```text
.github/workflows/api-tests.yml
```

Workflow используется для автоматической проверки API.