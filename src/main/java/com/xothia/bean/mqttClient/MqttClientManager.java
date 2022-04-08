package com.xothia.bean.mqttClient;

import com.xothia.bean.modbusSlave.MbSlaveGroup;
import com.xothia.util.Util;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.jetlinks.mqtt.client.MqttClient;
import org.jetlinks.mqtt.client.MqttClientCallback;
import org.jetlinks.mqtt.client.MqttClientConfig;
import org.jetlinks.mqtt.client.MqttConnectResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.nio.charset.StandardCharsets;

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
@Valid
@Component("mqttCliManager")
@Scope("prototype")
@PropertySource(value={"classpath:Mtm.properties"})
public class MqttClientManager implements InitializingBean {
    @NotNull
    private MqttClient mqttClient;

    @NotNull
    private static final EventLoopGroup loopGroup; //所有MqttClient共用的线程池

    @Positive
    private static int PUBLIC_THEAD_NUM; //线程池线程数

    @NotNull
    private static final MqttVersion MQTT_VER = MqttVersion.MQTT_3_1_1;

    static{
        //初始化线程池
        loopGroup = new NioEventLoopGroup(PUBLIC_THEAD_NUM);
    }

    public MqttClientManager() {
    }

    public MqttClientManager(MbSlaveGroup.MqttConfig conf) {
        //创建mqttclient 并建立同MQTT Broker的连接
        mqttClient = MqttClient.create(conf2Config(conf), ((topic, payload) -> {
            Util.LOGGER.info("Default Handler: " + topic + "=>" + payload.toString(StandardCharsets.UTF_8));
        }));
        mqttClient.setEventLoop(loopGroup);
        mqttClient.setCallback(new MqttClientCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Util.LOGGER.warn(mqttClient.getClientConfig().getClientId()+" Lost Connection to Broker. "+cause.getMessage());
            }

            @Override
            public void onSuccessfulReconnect() {
                Util.LOGGER.info(mqttClient.getClientConfig().getClientId()+"Successful Reconnect to Broker.");
            }
        });
        MqttConnectResult result;
        try{
            result = mqttClient.connect(conf.BrokerAddress, conf.BrokerPort).await().get();
        }catch (Exception e){
            Util.LOGGER.error("mqtt client failed establishing tcp connection to broker."+e.getMessage());
            mqttClient.disconnect();
            return;
        }

        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            Util.LOGGER.error("broker refused mqtt client." + result.getReturnCode());
            mqttClient.disconnect();
        } else {
            Util.LOGGER.info("mqtt client is connected to broker.");
        }
        //MQTT连接完成，初始化结束。
    }

    private MqttClientConfig conf2Config(MbSlaveGroup.MqttConfig myConf){
        final MqttClientConfig config = new MqttClientConfig();
        config.setClientId(myConf.ClientId);
        config.setUsername(myConf.Username);
        config.setPassword(myConf.Password);
        config.setProtocolVersion(MQTT_VER);
        config.setReconnect(true);
        return config;
    }


    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
    }

    @Value("${gateway.modbusDevice.mqttClient-threads}")
    public static void setPublicTheadNum(int publicTheadNum) {
        PUBLIC_THEAD_NUM = publicTheadNum;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }
}
