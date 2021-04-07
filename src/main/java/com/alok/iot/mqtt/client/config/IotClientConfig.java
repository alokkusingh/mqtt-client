package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.service.MqttClientService;
import com.alok.iot.mqtt.client.utils.CertUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class IotClientConfig {

    @Value("${iot.mqtt.host}")
    private String iotHost;

    @Value("${iot.mqtt.port}")
    private String iotPort;

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    @Bean
    public IMqttClient mqttClient1() throws MqttException {

        String publisherId = "client1";
        String iotClientUrl = String.format("ssl://%s:%s", iotHost, iotPort);

        return new MqttClient(iotClientUrl, publisherId);
    }

    @Bean
    public IMqttClient mqttClient2() throws MqttException {

        String publisherId = "client2";
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
    public MqttConnectOptions mqttConnectOptions(
            @Value("${iot.mqtt.clean-state}") Boolean cleanState,
            @Value("${iot.mqtt.auto-reconnect}") Boolean autoReconnect,
            @Value("${iot.mqtt.conn-timeout}") Integer connTimeout,
            @Value("${iot.mqtt.keep-alive}") Integer keepAliveTime,
            @Value("${iot.keystore.type}") String keystoreType,
            @Value("${iot.keystore.file}") String keystoreFile,
            @Value("${iot.truststore.file}") String truststoreFile,
            @Value("${iot.keystore.password}") String keystorePassword,
            @Value("${iot.truststore.password}") String truststorePassword
    ) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(cleanState);
        mqttConnectOptions.setAutomaticReconnect(autoReconnect);
        mqttConnectOptions.setConnectionTimeout(connTimeout);
        mqttConnectOptions.setKeepAliveInterval(keepAliveTime);
        mqttConnectOptions.setWill("home/last-will", "My Last Will".getBytes(), 1, false);

        Properties sslClientProperties = new Properties();
        sslClientProperties.setProperty("com.ibm.ssl.keyStoreType", keystoreType);
        sslClientProperties.setProperty("com.ibm.ssl.keyStore", CertUtils.getClientKeyStore(keystoreFile));
        sslClientProperties.setProperty("com.ibm.ssl.keyStorePassword", keystorePassword);
        sslClientProperties.setProperty("com.ibm.ssl.trustStore", CertUtils.getClientTrustStore(truststoreFile));
        sslClientProperties.setProperty("com.ibm.ssl.trustStorePassword", truststorePassword);

        mqttConnectOptions.setSSLProperties(sslClientProperties);

        return mqttConnectOptions;
    }

    @Bean(initMethod = "connect", destroyMethod = "disConnect")
    public MqttClientService mqttClientService() {
        return new MqttClientService();
    }
}
