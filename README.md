# evento-todo

The official sample application of the [Evento Framework](https://www.eventoframework.com/) —
a collaborative todo-list app built as a complete RECQ system, and the backend of the
[live demo](https://www.eventoframework.com/demo/) at demo.eventoframework.com.

Every RECQ component type appears in plain Java classes:

| Component | Class | Role in this app |
|---|---|---|
| `@Invoker` | [`TodoListInvoker`](src/main/java/com/eventoframework/demo/todo/service/invoker/TodoListInvoker.java) | Turns UI actions into Commands/Queries |
| `@Aggregate` | [`TodoListAggregate`](src/main/java/com/eventoframework/demo/todo/command/todo/TodoListAggregate.java), [`UserAggregate`](src/main/java/com/eventoframework/demo/todo/command/user/UserAggregate.java) | Event-sourced lists and users: validation & events |
| `@Service` | [`ErpService`](src/main/java/com/eventoframework/demo/todo/command/erp/ErpService.java), [`UserPrivacyService`](src/main/java/com/eventoframework/demo/todo/command/user/UserPrivacyService.java) | Simulated external ERP registration; the right to erasure |
| `@Projector` | [`TodoListProjector`](src/main/java/com/eventoframework/demo/todo/query/TodoListProjector.java), [`UserDirectoryProjector`](src/main/java/com/eventoframework/demo/todo/query/UserDirectoryProjector.java) | Materialize the JPA read model + SSE pushes |
| `@Projection` | [`TodoListProjection`](src/main/java/com/eventoframework/demo/todo/query/TodoListProjection.java) | Serves single, paged and streamed queries |
| `@Saga` | [`ErpTodoListActivityRegistrationSaga`](src/main/java/com/eventoframework/demo/todo/service/saga/ErpTodoListActivityRegistrationSaga.java) | Registers contributions when a list completes |
| `@Observer` | [`TodoListNotificationObserver`](src/main/java/com/eventoframework/demo/todo/service/observer/TodoListNotificationObserver.java) | Sends a notification on every check |

## What it shows of Evento 3.0

| Feature | Where |
|---|---|
| **Spring Boot starter**: the bundle is built, started and stopped from `evento.bundle.*` properties, consumer state in Postgres | [`application.properties`](src/main/resources/application.properties), [`EventoConfig`](src/main/java/com/eventoframework/demo/todo/config/EventoConfig.java) |
| **Read your writes**: the page after an action is answered once the projectors have it, no retry loop | [`TodoListInvoker`](src/main/java/com/eventoframework/demo/todo/service/invoker/TodoListInvoker.java), the observer |
| **Personal data and the right to erasure**: names are `@PersonalData`, encrypted with a key per user; *Forget me* deletes the key | [`UserRegisteredEvent`](src/main/java/com/eventoframework/demo/todo/api/user/event/UserRegisteredEvent.java), [`UserPrivacyService`](src/main/java/com/eventoframework/demo/todo/command/user/UserPrivacyService.java) |
| **Several events from one command**: *Check all* returns `DomainEvents`, one event per todo, stored all or none | [`TodoListAggregate`](src/main/java/com/eventoframework/demo/todo/command/todo/TodoListAggregate.java) |
| **Paged and streamed queries**: the lists are a `Page`, the CSV export a stream from a database cursor | [`TodoListProjection`](src/main/java/com/eventoframework/demo/todo/query/TodoListProjection.java), [`PageController`](src/main/java/com/eventoframework/demo/todo/web/PageController.java) |
| **Payload versioning**: `TodoListCreatedEvent` is at `@Revision(1)`, an upcaster reads the older events | [`TodoUpcasters`](src/main/java/com/eventoframework/demo/todo/api/TodoUpcasters.java) |
| **Test fixtures**: aggregates, saga and services tested given/when/then, no server | [`src/test`](src/test/java/com/eventoframework/demo/todo) |
| **Hardened server**: the live demo runs the `prod` security profile (bus token, TLS, payload allowlist, read-only console) | [evento-www `docker-compose.prod.yml`](https://github.com/EventoFramework/evento-www/blob/main/docker-compose.prod.yml) |

## Stack

- Java 25, Spring Boot 4.1, Thymeleaf server-rendered UI (no JS build)
- [`evento-spring-boot-starter`](https://central.sonatype.com/artifact/com.eventoframework/evento-spring-boot-starter) 3.0.0
  connected to an [Evento Server](https://hub.docker.com/r/eventoframework/evento-server) 3.0.0
- Postgres for both the read model and the Evento consumer state
  (`evento_v2_*` tables, created automatically at startup)
- Live updates via Server-Sent Events, fed in-JVM by the Projector

## Run it locally

Start the infrastructure (Postgres + Evento Server, with the `dev` security profile):

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

Against a hardened server set `EVENTO_BUNDLE_AUTH_TOKEN`, `EVENTO_BUNDLE_TLS_ENABLED=true` and
`EVENTO_BUNDLE_TLS_TRUST_CERTS` (the PEM that signs the server certificate).

## Tests

```bash
./gradlew test
```

Aggregates, the saga and the services are tested with the `evento-test` fixtures:
given these events, when this command, expect these events. No infrastructure needed.
That is the point of RECQ components: domain logic with no framework in the way.

## License

Part of the Evento Framework project — see
[eventoframework.com/licensing](https://www.eventoframework.com/licensing).
