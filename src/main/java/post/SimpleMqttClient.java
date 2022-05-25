package post;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
public class SimpleMqttClient implements MqttCallback {
	MqttClient myClient;
	MqttConnectOptions connOpt;
	MqttMessage message;
	static final String M2MIO_THING = UUID.randomUUID().toString();
	static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
	static final Boolean subscriber = true;
	static final Boolean publisher = true;
	
	private static String postmsg = "";
	private static final Logger log = LoggerFactory.getLogger(SimpleMqttClient.class);
	public static final String TOPIC = "grupatras/lab/engine/potato";

	@RequestMapping(value = "/", produces = { "application/json;charset=utf-8" }, consumes = {"application/json;charset=utf-8" }, method = RequestMethod.POST)
	public String getRespones(@RequestBody Message msg){
		log.info(String.format("Request: %s", msg));
		log.info(String.format("Request: %s", msg.getMessage()));
		String rqs = msg.getMessage();
		postmsg = rqs.toString();
		System.out.println(postmsg);
		System.out.println(rqs);
		return rqs;
	}
	
	/**
	 *
	 * connectionLost This callback is invoked upon losing the MQTT connection.
	 *
	 */
	public void connectionLost(Throwable t) {
		log.info("Connection lost!");
// code to reconnect to the broker would go here if desired
	}

	/**
	 *
	 * deliveryComplete This callback is invoked when a message published by this
	 * client is successfully received by the broker.
	 *
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	/**
	 *
	 * messageArrived This callback is invoked when a message is received on a
	 * subscribed topic.
	 *
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		log.info("\n");
		log.info("-------------------------------------------------");
		log.info("| Topic:" + topic);
		log.info("| Message: " + new String(message.getPayload()));
		log.info("-------------------------------------------------");
		log.info("\n");
	}

	/**
	 *
	 * runClient The main functionality of this simple example. Create a MQTT
	 * client, connect to broker, pub/sub, disconnect.
	 *
	 */
	public void runClient() {
		// setup MQTT Client
		String clientID = M2MIO_THING;
		connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		// connOpt.setUserName(M2MIO_USERNAME);
		// connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());
		// Connect to Broker
		try {
			myClient = new MqttClient(BROKER_URL, clientID);
			myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		log.info("Connected to " + BROKER_URL);
		String myTopic = TOPIC;
		MqttTopic topic = myClient.getTopic(myTopic);
		// subscribe to topic if subscriber
		if (subscriber) {
			try {
				int subQoS = 0;
				myClient.subscribe(myTopic, subQoS);
				if (!publisher) {
					while (true) {
						Thread.sleep(10000);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// publish messages if publisher
		if (publisher) 
		{
			while (true) 
			{
				//double postmsg = 80 + rnd.nextDouble() * 20.0;
				//postmsg = this.getTemp();
				System.out.print("");
				if (postmsg != "") 
					{
					System.out.println("yes");
					String val = String.format("T:%s", postmsg);
					//empty postmsg
					postmsg = "";
					System.out.println(postmsg);
					String pubMsg = "{\"value\":" + val + "}";
					int pubQoS = 0;
					MqttMessage message = new MqttMessage(pubMsg.getBytes());
					message.setQos(pubQoS);
					message.setRetained(false);
					// Publish the message
					log.info("Publishing to topic \"" + topic + "\" qos " + pubQoS + "\" value " + val);
					MqttDeliveryToken token = null;
					try 
						{
						// publish message to broker
						token = topic.publish(message);
						// Wait until the message has been delivered to the broker
						token.waitForCompletion();
						Thread.sleep(5000);
						} 
						catch (Exception e) {
							e.printStackTrace();
					}
				}
			}
		}
		// disconnect
		try {
			// wait to ensure subscribed messages are delivered
			if (subscriber) {
				Thread.sleep(5000);
			}
			myClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}