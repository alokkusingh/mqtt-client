package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.service.MqttClientService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class IotClient1Config {

    @Value("${iot.mqtt.host}")
    private String iotHost;

    @Value("${iot.mqtt.port}")
    private String iotPort;

    @Bean
    public IMqttClient mqttClient1() throws MqttException {

        String publisherId = "client1";
        String iotClientUrl = String.format("ssl://%s:%s", iotHost, iotPort);

        MqttClient mqttClient = new MqttClient(iotClientUrl, publisherId);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.format("[%s] Message arrived - %s - %s\n", publisherId, topic, mqttMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        return mqttClient;
    }

    @Bean
    public MqttConnectOptions mqttClient1ConnectOptions(
            @Value("${iot.mqtt.clean-state}") Boolean cleanState,
            @Value("${iot.mqtt.auto-reconnect}") Boolean autoReconnect,
            @Value("${iot.mqtt.conn-timeout}") Integer connTimeout,
            @Value("${iot.mqtt.keep-alive}") Integer keepAliveTime,
            Properties sslClientProperties
    ) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(cleanState);
        mqttConnectOptions.setAutomaticReconnect(autoReconnect);
        mqttConnectOptions.setConnectionTimeout(connTimeout);
        mqttConnectOptions.setKeepAliveInterval(keepAliveTime);
        mqttConnectOptions.setWill("home/device/client1/status", "OFFLINE".getBytes(), 1, false);

        mqttConnectOptions.setSSLProperties(sslClientProperties);

        return mqttConnectOptions;
    }

    @Bean(initMethod = "connect", destroyMethod = "disConnect")
    public MqttClientService mqttClient1Service(
            IMqttClient mqttClient1,
            MqttConnectOptions mqttClient1ConnectOptions,
            MqttMessage offlineMessage,
            MqttMessage onlineMessage,
            @Value("${iot.mqtt-connection-retry}") Integer connectRetry,
            @Value("${iot.subscribe.topic.client1}") String subscriptionTopic,
            @Value("${iot.subscribe.qos}") Integer qos
    ) {

        MqttClientService mqttClientService = new MqttClientService();

        mqttClientService.setClientId("client1");
        mqttClientService.setMqttClient(mqttClient1);
        mqttClientService.setMqttConnectOptions(mqttClient1ConnectOptions);
        mqttClientService.setOfflineMessage(offlineMessage);
        mqttClientService.setOnlineMessage(onlineMessage);
        mqttClientService.setConnectRetry(connectRetry);
        mqttClientService.setSubscriptionTopic(subscriptionTopic);
        mqttClientService.setQos(qos);
        mqttClientService.setMyStatusTopic("home/device1/status");
        mqttClientService.setOthersStatusTopic("home/device2/status");

        return mqttClientService;
    }
}
