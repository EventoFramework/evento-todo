package com.eventoframework.demo.todo.config;

import com.evento.application.EventoBundle;
import com.evento.application.bus.ClusterNodeAddress;
import com.evento.application.bus.EventoServerMessageBusConfiguration;
import com.evento.application.consumer.ConsumerEngineConfig;
import com.evento.application.performance.TracingAgent;
import com.evento.common.messaging.bus.EventoServer;
import com.evento.common.messaging.consumer.ConsumerProcessor;
import com.evento.common.modeling.messaging.message.application.Message;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.performance.PerformanceService;
import com.evento.common.serialization.ObjectMapperUtils;
import com.evento.consumer.state.store.jdbc.FlywayMigrator;
import com.evento.consumer.state.store.jdbc.JdbcConsumerLock;
import com.evento.consumer.state.store.jdbc.JdbcConsumerStateStore;
import com.evento.consumer.state.store.jdbc.JdbcDeadEventQueue;
import com.evento.consumer.state.store.jdbc.JdbcDedupeStore;
import com.evento.consumer.state.store.jdbc.JdbcSagaStateStore;
import com.evento.consumer.state.store.jdbc.SqlDialect;
import com.eventoframework.demo.todo.TodoApplication;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.Executors;

/**
 * Wires this Spring Boot application into an Evento bundle: all annotated RECQ
 * components in the base package are discovered and registered with the Evento
 * Server, and the consumer engines (projector/saga/observer) persist their
 * state in the same Postgres the read models use.
 */
@Configuration
public class EventoConfig {

    @Bean
    public EventoBundle eventoBundle(
            @Value("${evento.server.host}") String host,
            @Value("${evento.server.port}") int port,
            @Value("${evento.bundle.id}") String bundleId,
            @Value("${evento.bundle.version}") long bundleVersion,
            @Value("${evento.bundle.repository-url:https://github.com/EventoFramework/evento-todo/blob/main}") String repositoryUrl,
            DataSource dataSource,
            BeanFactory factory) throws Exception {
        // Create the evento_v2_* consumer tables on first start
        FlywayMigrator.migrate(dataSource, SqlDialect.POSTGRES);
        return EventoBundle.Builder.builder()
                .setBasePackage(TodoApplication.class.getPackage())
                .setBundleId(bundleId)
                .setBundleVersion(bundleVersion)
                // Lets the Evento GUI deep-link every handler to its source on GitHub
                .setRepositoryUrl(repositoryUrl)
                .setEventoServerMessageBusConfiguration(new EventoServerMessageBusConfiguration(
                        new ClusterNodeAddress(host, port)))
                .setInjector(factory::getBean)
                .setConsumerEngineConfigBuilder((eventoServer, performanceService) ->
                        jdbcEngineConfig(dataSource, eventoServer, performanceService))
                // Propagate the "user" metadata across the whole message flow
                .setTracingAgent(new TracingAgent(bundleId, bundleVersion) {
                    @Override
                    public Metadata correlate(Metadata metadata, Message<?> handledMessage) {
                        if (handledMessage != null && handledMessage.getMetadata() != null
                                && handledMessage.getMetadata().get("user") != null) {
                            if (metadata == null) return handledMessage.getMetadata();
                            metadata.put("user", handledMessage.getMetadata().get("user"));
                            return metadata;
                        }
                        return super.correlate(metadata, handledMessage);
                    }
                })
                .start();
    }

    private ConsumerEngineConfig jdbcEngineConfig(DataSource dataSource,
                                                  EventoServer eventoServer,
                                                  PerformanceService performanceService) {
        var dialect = SqlDialect.POSTGRES;
        var objectMapper = ObjectMapperUtils.getPayloadObjectMapper();
        var stateStore = new JdbcConsumerStateStore(dataSource, dialect);
        var deadEventQueue = new JdbcDeadEventQueue(dataSource, dialect, objectMapper);
        var processor = ConsumerProcessor.builder()
                .eventoServer(eventoServer)
                .lock(new JdbcConsumerLock(dataSource, dialect))
                .stateStore(stateStore)
                .sagaStateStore(new JdbcSagaStateStore(dataSource, dialect, objectMapper))
                .deadEventQueue(deadEventQueue)
                .dedupeStore(new JdbcDedupeStore(dataSource, dialect))
                .performanceService(performanceService)
                .observerExecutor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
        return new ConsumerEngineConfig(processor, stateStore, deadEventQueue);
    }
}
