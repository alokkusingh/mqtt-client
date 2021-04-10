package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.service.MqttClientService;
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
    MqttClientService mqttClient1Service;

    @Value("${iot.publish.topic.client1}")
    private String topicClient1;

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
            System.out.format("[%s] Publishing message\n", mqttClient1Service.getClientId());
            if (!mqttClient1Service.getMqttClient().isConnected()) {
                System.out.format("[%s] Not connected\n", mqttClient1Service.getMqttClient().getClientId());
                return;
            }

            mqttClient1Service.getMqttClient().publish(topicClient1, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
