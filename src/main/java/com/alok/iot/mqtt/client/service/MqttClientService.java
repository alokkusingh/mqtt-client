package com.alok.iot.mqtt.client.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClientService {

    private String clientId;
    private IMqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;
    private MqttMessage offlineMessage;
    private MqttMessage onlineMessage;
    private Integer connectRetry;
    private String subscriptionTopic;
    private String myStatusTopic;
    private String othersStatusTopic;
    private Integer qos;

    public void connect() {
        boolean connected = false;
        int retryCount = 0;
        while (connected == false && retryCount < connectRetry) {
            try {
                System.out.format("[%s] Connecting to MQTT broker - attempt: %d\n", clientId, retryCount + 1);
                System.out.println(mqttConnectOptions);
                mqttClient.connect(mqttConnectOptions);
                System.out.format("[%s] Connected: %b\n", clientId, mqttClient.isConnected());
                mqttClient.subscribe(new String[] {subscriptionTopic, othersStatusTopic}, new int[] {qos, qos});
                mqttClient.publish(myStatusTopic, onlineMessage);

                connected = true;
            } catch (MqttException e) {
                e.printStackTrace();
                ++retryCount;
                if (retryCount < connectRetry) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    public void disConnect() {
        try {
            System.out.format("[%s] Disconnecting, connected: %b\n", clientId, mqttClient.isConnected());
            mqttClient.publish(myStatusTopic, offlineMessage);
            mqttClient.disconnect();
            System.out.format("[%s] Disconnected\n", clientId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public String getClientId() {
        return clientId;
    }

    public IMqttClient getMqttClient() {
        return mqttClient;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setMqttClient(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }

    public void setOfflineMessage(MqttMessage offlineMessage) {
        this.offlineMessage = offlineMessage;
    }

    public void setOnlineMessage(MqttMessage onlineMessage) {
        this.onlineMessage = onlineMessage;
    }

    public void setConnectRetry(Integer connectRetry) {
        this.connectRetry = connectRetry;
    }

    public void setSubscriptionTopic(String subscriptionTopic) {
        this.subscriptionTopic = subscriptionTopic;
    }

    public void setMyStatusTopic(String myStatusTopic) {
        this.myStatusTopic = myStatusTopic;
    }

    public void setOthersStatusTopic(String othersStatusTopic) {
        this.othersStatusTopic = othersStatusTopic;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }
}
