package com.alok.iot.mqtt.client.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.charset.StandardCharsets;

@EnableScheduling
@Configuration
public class JobScheduler {

    @Autowired
    IMqttClient mqttClient;

    @Value("${iot.publish.topic}")
    private String topic;

    @Value("${iot.publish.qos}")
    private Integer qos;

    @Scheduled(cron = "0 * * * * ?")
    public void publishMessage() {

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        // AWS IoT Core doesn't support retained=true
        mqttMessage.setRetained(false);
        mqttMessage.setPayload("Some message".getBytes(StandardCharsets.UTF_8));

        try {
            System.out.println("Publishing message");
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
