# evento-todo

The official sample application of the [Evento Framework](https://www.eventoframework.com/) —
a collaborative todo-list app built as a complete RECQ system, and the backend of the
[live demo](https://www.eventoframework.com/demo/) at demo.eventoframework.com.

Every RECQ component type appears exactly once, each in one plain Java class:

| Component | Class | Role in this app |
|---|---|---|
| `@Invoker` | [`TodoListInvoker`](src/main/java/com/eventoframework/demo/todo/service/invoker/TodoListInvoker.java) | Turns UI actions into Commands/Queries |
| `@Aggregate` | [`TodoListAggregate`](src/main/java/com/eventoframework/demo/todo/command/todo/TodoListAggregate.java) | Event-sourced list: validation & events |
| `@Service` | [`ErpService`](src/main/java/com/eventoframework/demo/todo/command/erp/ErpService.java) | Simulated external ERP registration |
| `@Projector` | [`TodoListProjector`](src/main/java/com/eventoframework/demo/todo/query/TodoListProjector.java) | Materializes the JPA read model + SSE pushes |
| `@Projection` | [`TodoListProjection`](src/main/java/com/eventoframework/demo/todo/query/TodoListProjection.java) | Serves queries from the read model |
| `@Saga` | [`ErpTodoListActivityRegistrationSaga`](src/main/java/com/eventoframework/demo/todo/service/saga/ErpTodoListActivityRegistrationSaga.java) | Registers contributions when a list completes |
| `@Observer` | [`TodoListNotificationObserver`](src/main/java/com/eventoframework/demo/todo/service/observer/TodoListNotificationObserver.java) | Sends a notification on every check |

## Stack

- Java 25, Spring Boot 3.5 (LTS), Thymeleaf server-rendered UI (no JS build)
- [`evento-bundle`](https://central.sonatype.com/artifact/com.eventoframework/evento-bundle) 2.2.2
  connected to an [Evento Server](https://hub.docker.com/r/eventoframework/evento-server)
- Postgres for both the read model and the Evento consumer state
  (`evento_v2_*` tables, migrated automatically at startup)
- Live updates via Server-Sent Events, fed in-JVM by the Projector

## Run it locally

Start the infrastructure (Postgres + Evento Server):

```bash
docker compose up -d   # see docker-compose.yaml
```

Then run the app:

```bash
./gradlew bootRun
```

Open http://localhost:8080 — and the Evento GUI at http://localhost:3000
(user `evento`, password `secret`) to watch the system from the inside:
component catalog, application graph, flows and live telemetry.

## Tests

```bash
./gradlew test
```

Aggregate and saga behavior are plain-Java unit tested — no infrastructure needed.
That is the point of RECQ components: domain logic with no framework in the way.

## License

Part of the Evento Framework project — see
[eventoframework.com/licensing](https://www.eventoframework.com/licensing).
