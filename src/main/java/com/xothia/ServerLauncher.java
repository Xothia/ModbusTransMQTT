package com.xothia;

import com.xothia.springConfig.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
        //ApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        MqttServer server = context.getBean("mqttServer", MqttServer.class);
        server.run();
    }
}
