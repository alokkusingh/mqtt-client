package com.alok.iot.mqtt.client.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MqttClientService {

    @Autowired
    private IMqttClient mqttClient;

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    @Value("${iot.mqtt-connection-retry}")
    private Integer connectRetry;

    @Value("${iot.subscribe.topic}")
    private String topic;

    public void connect() {
        boolean connected = false;
        int retryCount = 0;
        while (connected == false && retryCount < connectRetry) {
            try {
                System.out.println("connecting to MQTT broker - count: " + (retryCount + 1));
                mqttClient.connect(mqttConnectOptions);
                connected = true;
                System.out.println("connecting to MQTT broker - connected");
            } catch (MqttException e) {
                e.printStackTrace();
                ++retryCount;
            }
        }
    }

    public void disConnect() {
        try {
            System.out.println("disconnecting to MQTT broker");
            mqttClient.disconnect();
            System.out.println("disconnecting to MQTT broker - disconnected");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
