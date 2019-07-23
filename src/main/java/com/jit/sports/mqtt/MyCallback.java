package com.jit.sports.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sports.InfluxDB.InfluxDealData;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyCallback implements MqttCallback
{
	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("MQTT : connectionLost\n" + cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) {
		dealMsg(topic, message);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token)	{ return; }

	private void dealMsg(String topic, MqttMessage message) {
		if (message.isRetained()) {
			return;
		}
		System.out.println("in dealDevMsg : topic:	" + topic + "\t");

		if(topic.startsWith("sports/sportInfo")) {
			dealSportInfo(topic, message);
		}
		switch (topic) {
			case "Will":
				dealWill(message);
				break;

			default:
				break;
		}
	}

	//处理运动信息
	private void dealSportInfo(String topic, MqttMessage message) {

		String userName = topic.substring(topic.lastIndexOf('/') + 1);

		try {
			System.out.println("payload:"+new String(message.getPayload()));
			JSONObject obj = JSON.parseObject(new String(message.getPayload()));
			System.out.println(obj);
			InfluxDealData.writeSportInfoIntoDB(obj.getString("sportTag"), obj.getDoubleValue("longitude"),
					obj.getDoubleValue("latitude"), obj.getDoubleValue("altitude"),
					obj.getDoubleValue("speed"), obj.getDoubleValue("azimuth"),
					obj.getDoubleValue("pitch"), obj.getDoubleValue("roll"),
					obj.getDoubleValue("accelerated_x"), obj.getDoubleValue("accelerated_y"),
					obj.getDoubleValue("accelerated_z"), obj.getInteger("steps"));
		}catch (Exception e){
			System.out.println("error：mqtt接收到不正确的格式");
		}


		//返回处理后的信息
		MySubscribe.myPublish("sports/processedInfo/"+userName, "recvived");
	}


	//记录异常掉线
	public void dealWill(MqttMessage message) 	{
	}


}