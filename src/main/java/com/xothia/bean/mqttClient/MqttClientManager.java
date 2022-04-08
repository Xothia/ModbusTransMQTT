package com.xothia.bean.mqttClient;

import org.jetlinks.mqtt.client.MqttClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.mqttClient
 * @ClassName : .java
 * @createTime : 2022/4/7 17:04
 * @Email : huaxia889900@126.com
 * @Description : 这是一个MqttClient的代理类。
 * 单例
 * 参数不足会从properties配置文件中获取默认值。
 */
@Component
public class MqttClientManager implements InitializingBean {
    @NotNull
    private MqttClient mqttClient;


    public MqttClientManager() {
    }

    @Override
    public void afterPropertiesSet(){

    }
}
