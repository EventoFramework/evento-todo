package com.eventoframework.demo.todo.config;

import com.evento.application.performance.TracingAgent;
import com.evento.common.modeling.messaging.message.application.Message;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.spring.boot.EventoBundleCustomizer;
import com.eventoframework.demo.todo.api.TodoUpcasters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Evento 3.0: the bundle is built, started and stopped by {@code evento-spring-boot-starter}
 * from the {@code evento.bundle.*} properties (server, auth token, TLS, payload allowlist,
 * consumer state in Postgres). This customizer only adds what properties cannot say.
 */
@Configuration
public class EventoConfig {

    @Bean
    EventoBundleCustomizer todoBundle(
            @Value("${evento.bundle.id}") String bundleId,
            @Value("${evento.bundle.version}") long bundleVersion,
            @Value("${evento.bundle.repository-url:https://github.com/EventoFramework/evento-todo/blob/main/src/main/java}") String repositoryUrl) {
        return builder -> builder
                // Lets the Evento GUI deep-link every handler to its source on GitHub
                .setRepositoryUrl(repositoryUrl)
                // The history of the payloads whose shape changed (payload versioning)
                .addUpcasters(TodoUpcasters.ALL)
                // Propagate the "user" metadata (a pseudonymous id) across the whole message flow
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
                });
    }
}
