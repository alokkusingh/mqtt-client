package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.service.MqttClientService;
import com.alok.iot.mqtt.client.utils.CertUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.UUID;

@Configuration
public class IotClientConfig {

    @Value("${iot.mqtt.host}")
    private String iotHost;

    @Value("${iot.mqtt.port}")
    private String iotPort;

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    @Bean
    public IMqttClient mqttClient() throws MqttException {

        String publisherId = UUID.randomUUID().toString();
        String iotClientUrl = String.format("ssl://%s:%s", iotHost, iotPort);

        return new MqttClient(iotClientUrl, publisherId);
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
