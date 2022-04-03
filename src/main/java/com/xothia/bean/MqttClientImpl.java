package com.xothia.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean
 * @ClassName : .java
 * @createTime : 2022/3/15 21:00
 * @Email : huaxia889900@126.com
 * @Description :
 */
public class MqttClientImpl {
    private String tcpUrl;
    private String clientId;

    protected final Log logger = LogFactory.getLog(getClass()); //日志

    void init() throws Exception{

//
//        MqttClient client = new MqttClient(tcpUrl, clientId);
//        MqttConnectOptions mqcConf = new MqttConnectOptions();
//        mqcConf.setConnectionTimeout(300);
//        mqcConf.setKeepAliveInterval(1000);
//        client.connect(mqcConf);
//        client.setCallback(new MqttCallback(){
//            @Override
//            public void connectionLost(Throwable throwable) {
//                logger.warn("Connection Lost.");
//            }
//
//            @Override
//            public void messageArrived(String s, MqttMessage mqttMessage)  {
//                logger.info("Message Arrived.");
//
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//                logger.info("Delivery Complete.");
//            }
//        });

        //client.subscribe(topicName);
    }

}
