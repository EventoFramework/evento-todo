package com.eventoframework.demo.todo.config;

import com.eventoframework.demo.todo.utils.RealtimeUpdatesService;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealtimeConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "mqtt")
    public MqttConnectionOptions mqttConnectOptions() {
        return new MqttConnectionOptions();
    }

    @Bean
    public RealtimeUpdatesService realtimeService(@Value("${mqtt.broker}") String mqttBroker,
                                                  @Value("${mqtt.client}") String mqttClient,
                                                  @Value("${mqtt.env}") String mqttEnvironment,
                                                  MqttConnectionOptions mqttConnectionOptions) throws MqttException {
        return RealtimeUpdatesService.create(mqttBroker, mqttClient, mqttConnectionOptions, mqttEnvironment);
    }
}

