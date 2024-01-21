package com.eventoframework.demo.todo.config;

import com.eventoframework.demo.todo.TodoApplication;
import org.evento.application.EventoBundle;
import org.evento.application.bus.ClusterNodeAddress;
import org.evento.application.bus.EventoServerMessageBusConfiguration;
import org.evento.application.performance.TracingAgent;
import org.evento.common.modeling.messaging.message.application.Message;
import org.evento.common.modeling.messaging.message.application.Metadata;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventoConfig {

    @Bean
    public EventoBundle eventoBundle(BeanFactory factory) throws Exception {
        String bundleId = "ToDoList-Bundle";
        int bundleVersion = 1;
        var evento =  EventoBundle.Builder.builder()
                // Starting Package to detect RECQ components
                .setBasePackage(TodoApplication.class.getPackage())
                // Name of the bundle
                .setBundleId(bundleId)
                // Bundle's version
                .setBundleVersion(bundleVersion)
                // Set up the Evento message bus
                .setEventoServerMessageBusConfiguration(new EventoServerMessageBusConfiguration(
                        // Evento Server Addresses
                        new ClusterNodeAddress("localhost",3030)
                ))
                .setTracingAgent(new TracingAgent(bundleId, bundleVersion){
                    @Override
                    public Metadata correlate(Metadata metadata, Message<?> handledMessage) {
                        if(handledMessage!=null && handledMessage.getMetadata() != null && handledMessage.getMetadata().get("user") != null){
                            if(metadata == null) return handledMessage.getMetadata();
                            metadata.put("user", handledMessage.getMetadata().get("user"));
                            return metadata;
                        }
                        return super.correlate(metadata, handledMessage);
                    }})
                .setInjector(factory::getBean)
                .start();
        evento.getPerformanceService().setPerformanceRate(1);
        return evento;
    }
}
