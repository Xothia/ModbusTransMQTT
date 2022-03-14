package com.xothia;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/3/14 16:34
 * @Email : huaxia889900@126.com
 * @Description :
 */
public class ServerLauncher {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        MqttServer server = context.getBean("mqttServer", MqttServer.class);
        server.run();
    }
}
