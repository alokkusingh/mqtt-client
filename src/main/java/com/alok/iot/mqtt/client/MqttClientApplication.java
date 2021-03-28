package com.alok.iot.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttClientApplication {

	@Autowired
	IMqttClient iotClient;

	public static void main(String[] args) {
		SpringApplication.run(MqttClientApplication.class, args);
		System.out.println();
	}

}
