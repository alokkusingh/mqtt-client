package com.alok.iot.mqtt.client.config;

import com.alok.iot.mqtt.client.utils.CertUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class IotCommonClientConfig {

    @Bean
    public Properties sslClientProperties(
            @Value("${iot.keystore.type}") String keystoreType,
            @Value("${iot.keystore.file}") String keystoreFile,
            @Value("${iot.truststore.file}") String truststoreFile,
            @Value("${iot.keystore.password}") String keystorePassword,
            @Value("${iot.truststore.password}") String truststorePassword
    ) {

        Properties properties = new Properties();
        properties.setProperty("com.ibm.ssl.keyStoreType", keystoreType);
        properties.setProperty("com.ibm.ssl.keyStore", CertUtils.getClientKeyStore(keystoreFile));
        properties.setProperty("com.ibm.ssl.keyStorePassword", keystorePassword);
        properties.setProperty("com.ibm.ssl.trustStore", CertUtils.getClientTrustStore(truststoreFile));
        properties.setProperty("com.ibm.ssl.trustStorePassword", truststorePassword);

        return properties;
    }

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
