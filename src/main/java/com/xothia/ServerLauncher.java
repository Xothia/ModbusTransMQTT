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
    public static void main(String[] args) throws Exception{
        //ApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        //Modbus设备代理服务
        final ModbusProxy modbusProxy = context.getBean(ModbusProxy.class);
        //modbusProxy.run();

        //Mqtt设备代理服务
        final MqttProxy server = context.getBean("mqttProxy", MqttProxy.class);
        server.run();
    }
}
