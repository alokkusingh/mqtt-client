package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.service.MqttClientService;
import com.alok.iot.mqtt.client.utils.CertUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class IotClient2Config {

    @Value("${iot.mqtt.host}")
    private String iotHost;

    @Value("${iot.mqtt.port}")
    private String iotPort;

    @Value("${iot.client.id.2}")
    private String clientId;

    @Bean
    public IMqttClient mqttClient2() throws MqttException {

        String publisherId = clientId;
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
    public MqttConnectOptions mqttClient2ConnectOptions(
            @Value("${iot.mqtt.clean-state}") Boolean cleanState,
            @Value("${iot.mqtt.auto-reconnect}") Boolean autoReconnect,
            @Value("${iot.mqtt.conn-timeout}") Integer connTimeout,
            @Value("${iot.mqtt.keep-alive}") Integer keepAliveTime,
            Properties sslClient2Properties
    ) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(cleanState);
        mqttConnectOptions.setAutomaticReconnect(autoReconnect);
        mqttConnectOptions.setConnectionTimeout(connTimeout);
        mqttConnectOptions.setKeepAliveInterval(keepAliveTime);
        mqttConnectOptions.setWill("home/device/client2/status", "OFFLINE".getBytes(), 1, false);

        mqttConnectOptions.setSSLProperties(sslClient2Properties);

        return mqttConnectOptions;
    }

    @Bean
    public Properties sslClient2Properties(
            @Value("${iot.keystore.type}") String keystoreType,
            @Value("${iot.keystore.file.2}") String keystoreFile,
            @Value("${iot.truststore.file}") String truststoreFile,
            @Value("${iot.keystore.password}") String keystorePassword,
            @Value("${iot.truststore.password}") String truststorePassword
    ) {

        //valid properties are {"com.ibm.ssl.protocol", "com.ibm.ssl.contextProvider", "com.ibm.ssl.keyStore", "com.ibm.ssl.keyStorePassword", "com.ibm.ssl.keyStoreType", "com.ibm.ssl.keyStoreProvider", "com.ibm.ssl.keyManager", "com.ibm.ssl.trustStore", "com.ibm.ssl.trustStorePassword", "com.ibm.ssl.trustStoreType", "com.ibm.ssl.trustStoreProvider", "com.ibm.ssl.trustManager", "com.ibm.ssl.enabledCipherSuites", "com.ibm.ssl.clientAuthentication"};
        Properties properties = new Properties();
        properties.setProperty("com.ibm.ssl.keyStoreType", keystoreType);
        properties.setProperty("com.ibm.ssl.keyStore", CertUtils.getClientKeyStore(keystoreFile));
        properties.setProperty("com.ibm.ssl.keyStorePassword", keystorePassword);
        properties.setProperty("com.ibm.ssl.trustStore", CertUtils.getClientTrustStore(truststoreFile));
        properties.setProperty("com.ibm.ssl.trustStorePassword", truststorePassword);
        return properties;
    }

    @Bean(initMethod = "connect", destroyMethod = "disConnect")
    public MqttClientService mqttClient2Service(
            IMqttClient mqttClient2,
            MqttConnectOptions mqttClient2ConnectOptions,
            MqttMessage offlineMessage,
            MqttMessage onlineMessage,
            @Value("${iot.mqtt-connection-retry}") Integer connectRetry,
            @Value("${iot.subscribe.topic.client2}") String subscriptionTopic,
            @Value("${iot.subscribe.qos}") Integer qos
    ) {

        MqttClientService mqttClientService = new MqttClientService();

        mqttClientService.setClientId(clientId);
        mqttClientService.setMqttClient(mqttClient2);
        mqttClientService.setMqttConnectOptions(mqttClient2ConnectOptions);
        mqttClientService.setOfflineMessage(offlineMessage);
        mqttClientService.setOnlineMessage(onlineMessage);
        mqttClientService.setConnectRetry(connectRetry);
        mqttClientService.setSubscriptionTopic(subscriptionTopic);
        mqttClientService.setQos(qos);
        mqttClientService.setMyStatusTopic("home/device2/status");
        mqttClientService.setOthersStatusTopic("home/device1/status");

        return mqttClientService;
    }
}
