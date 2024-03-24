package com.eventoframework.demo.todo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import com.evento.common.utils.ProjectorStatus;

import java.time.Instant;
import java.util.Map;

public class RealtimeUpdatesService {

    private final Logger logger = LogManager.getLogger(RealtimeUpdatesService.class);

    private final MqttClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String environment;

    public RealtimeUpdatesService(MqttClient client, String environment) {
        this.client = client;
        this.environment = environment;
    }

    public static RealtimeUpdatesService create(String broker,
                                                String clientId,
                                                MqttConnectionOptions options,
                                                String environment) throws MqttException {
        var client = new MqttClient(broker, clientId);
        client.connect(options);
        return new RealtimeUpdatesService(client, environment);

    }


    private void publishChange(RealtimeResource resource, UpdateType type, ProjectorStatus projectorStatus) {
        if (projectorStatus.isHeadReached()) {
            try {
                client.publish(
                        environment + "/EventoTodoList/" + resource.getTopic() + "/" + type,
                        new MqttMessage(
                                objectMapper.writeValueAsBytes(Map.of(
                                        "ts", Instant.now().toEpochMilli())),
                                1,
                                false,
                                new MqttProperties()
                        )
                );
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void notifyDelete(RealtimeResource realtimeResource, ProjectorStatus projectorStatus) {
        publishChange(
                realtimeResource,
                UpdateType.DELETE,
                projectorStatus
        );
    }

    public void notifyUpdate(RealtimeResource realtimeResource, ProjectorStatus projectorStatus) {
        publishChange(
                realtimeResource,
                UpdateType.UPDATE,
                projectorStatus
        );
    }

    public void notifyCreate(RealtimeResource realtimeResource, ProjectorStatus projectorStatus) {
        publishChange(
                realtimeResource,
                UpdateType.CREATE,
                projectorStatus
        );
    }
}
