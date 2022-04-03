package com.xothia.springConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.springConfig
 * @ClassName : .java
 * @createTime : 2022/3/15 20:44
 * @Email : huaxia889900@126.com
 * @Description : spring config类
 */
@Configuration
@ComponentScan(basePackages = {"com.xothia"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig {
    /**
     * mqtt client bean
     * @return 一个新的MqttClientImpl实例
     */
    //@Bean
    //@Scope("prototype")
    //public MqttClientImpl mqttClient(){
    //    return new MqttClientImpl();
    //}

}
