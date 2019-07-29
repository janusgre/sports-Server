package com.jit.sports.mqtt;


import com.jit.sports.Utils.PropertiesUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Random;

public class MQTTClient {

	static private String TOPIC_Will = "Will";

	static private String HOST = PropertiesUtil.getProperty("mqtt.host");
	static private String userName = "server";
	static private String passWord = "123";
	static private int qos = 1;
	public static MqttClient client;


	public MQTTClient() {
		Random random = new Random();
		String clientid = String.valueOf(random.nextInt(10000));


		try {
			client = new MqttClient(HOST, clientid, new MemoryPersistence());
			// MQTT的连接设置
			MqttConnectOptions options = new MqttConnectOptions();
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
			options.setCleanSession(true);
			// 设置连接的用户名
			options.setUserName(userName);
			// 设置连接的密码
			options.setPassword(passWord.toCharArray());
			// 设置超时时间 单位为秒
			options.setConnectionTimeout(10);
			// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
			options.setKeepAliveInterval(20);

			options.setAutomaticReconnect(true);

			// 设置回调函数
			client.setCallback(new MyCallback());
			client.connect(options);
			// 订阅消息
			client.subscribe("sports/sportInfo/#", qos);
			client.subscribe(TOPIC_Will, qos);

			System.out.println("mqtt start");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void myPublish(String topic, byte[] msg) {
		try {
			System.out.println("pubish:"+topic);
			client.publish(topic, new MqttMessage(msg));
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}