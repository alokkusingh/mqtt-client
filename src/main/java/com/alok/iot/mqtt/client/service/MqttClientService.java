package com.alok.iot.mqtt.client.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MqttClientService {

    @Autowired
    private IMqttClient mqttClient1;

    @Autowired
    private IMqttClient mqttClient2;

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    @Value("${iot.mqtt-connection-retry}")
    private Integer connectRetry;

    @Value("${iot.subscribe.topic.client2}")
    private String subsTopicClient2;

    @Value("${iot.subscribe.qos}")
    private Integer qos;

    public void connect() {
        boolean connected = false;
        int retryCount = 0;
        System.out.println(mqttConnectOptions);
        while (connected == false && retryCount < connectRetry) {
            try {
                System.out.println("connecting to MQTT broker - count: " + (retryCount + 1));
                mqttClient1.connect(mqttConnectOptions);

                mqttClient2.connect(mqttConnectOptions);
                mqttClient2.subscribe(new String[] {subsTopicClient2, "home/last-will"}, new int[] {qos, qos});
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
            mqttClient1.disconnect();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mqttClient2.disconnect();
            System.out.println("disconnecting to MQTT broker - disconnected");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
