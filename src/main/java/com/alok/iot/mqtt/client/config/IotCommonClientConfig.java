package com.alok.iot.mqtt.client.config;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class IotCommonClientConfig {

    @Bean
    public MqttMessage offlineMessage() {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        // AWS IoT Core doesn't support retained=true
        mqttMessage.setRetained(false);
        mqttMessage.setPayload("OFFLINE".getBytes(StandardCharsets.UTF_8));

        return mqttMessage;
    }

    @Bean
    public MqttMessage onlineMessage() {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        // AWS IoT Core doesn't support retained=true
        mqttMessage.setRetained(false);
        mqttMessage.setPayload("ONLINE".getBytes(StandardCharsets.UTF_8));

        return mqttMessage;
    }
}
