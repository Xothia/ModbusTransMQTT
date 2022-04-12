package com.xothia.bean.mqttClient;

import com.xothia.bean.modbusMaster.MbMaster;
import com.xothia.bean.modbusSlave.MbSlaveGroup;
import com.xothia.util.UpstreamFormat;
import com.xothia.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.Future;
import org.jetlinks.mqtt.client.*;
import org.jetlinks.mqtt.client.MqttClient;
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
public class MqttClientManager implements InitializingBean, com.xothia.bean.mqttClient.MqttClient {
    @NotNull
    private MqttClient mqttClient;

    @NotNull
    private static final EventLoopGroup loopGroup; //所有MqttClient共用的线程池

    @Positive
    private static int PUBLIC_THEAD_NUM; //线程池线程数

    @NotNull
    private static final MqttVersion MQTT_VER = MqttVersion.MQTT_3_1_1;

    @Value("${gateway.modbusDevice.defaultMqttClient.clientId}")
    private String defaultClientId;

    @Value("${gateway.modbusDevice.defaultMqttClient.userName}")
    private String defaultUsername;

    @Value("${gateway.modbusDevice.defaultMqttClient.password}")
    private String defaultPassword;

    @Value("${gateway.modbusDevice.defaultMqttBroker.hostAddress}")
    private String defaultHostAddress;

    @Value("${gateway.modbusDevice.defaultMqttBroker.hostPort}")
    private Integer defaultHostPort;

    @NotNull
    private MbSlaveGroup.MqttConfig conf;

    //持有相关Modbus Master的引用
    private MbMaster mbMaster;


    static{
        //初始化线程池
        loopGroup = new NioEventLoopGroup(PUBLIC_THEAD_NUM);
    }

    public MqttClientManager() {
    }

    public MqttClientManager(MbSlaveGroup.MqttConfig conf) {
        this.conf=conf;
    }

    //从conf获取参数，若无则读取默认参数。

    private MqttClientConfig conf2Config(MbSlaveGroup.MqttConfig myConf){
        final MqttClientConfig config = new MqttClientConfig();

        config.setClientId(Util.isNullOrBlank(myConf.ClientId)?defaultClientId:myConf.ClientId);
        config.setUsername(Util.isNullOrBlank(myConf.Username)?defaultUsername:myConf.Username);
        config.setPassword(Util.isNullOrBlank(myConf.Password)?defaultPassword:myConf.Password);

        config.setProtocolVersion(MQTT_VER);
        config.setReconnect(true);
        return config;
    }


    @Override
    public void afterPropertiesSet(){
        //创建mqttclient
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
        //参数设置完毕
        Util.valid(this);
    }

    @Override
    public void doConnect(){
        MqttConnectResult result;
        try{
            result = mqttClient.connect(Util.isNullOrBlank(conf.BrokerAddress)?defaultHostAddress:conf.BrokerAddress,
                    (conf.BrokerPort==null)?defaultHostPort:conf.BrokerPort)
                    .await().get();
        }catch (Exception e){
            Util.LOGGER.error("mqtt client failed establishing tcp connection to broker."+e.getMessage());
            mqttClient.disconnect();
            return;
        }

        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            Util.LOGGER.error("broker refused mqtt client." + result.getReturnCode());
            mqttClient.disconnect();
        } else {
            Util.LOGGER.info(mqttClient.getClientConfig().getClientId() + ": mqtt client is connected to broker.");
        }
        //MQTT连接完成。
    }

    @Override
    public Future<Void> subscribe(String topic, MqttHandler handler){
        //等待添加指令报文解析。 handler应被extend并完善解析下达指令功能。

        return mqttClient.on(topic, handler);
    }

    public void publish(String[] topics, UpstreamFormat data, Integer qos){
        //未测试
        ByteBuf dataBuf = data.getByteBuf();
        MqttQoS q = MqttQoS.AT_MOST_ONCE;
        switch (qos){
            case 1:
                q=MqttQoS.AT_LEAST_ONCE;
                break;
            case 2:
                q=MqttQoS.EXACTLY_ONCE;
                break;
        }

        for(String topic:topics){
            mqttClient.publish(topic, dataBuf, q);
        }
    }

    @Override
    public void disconnect() {
        mqttClient.disconnect();
    }

    @Value("${gateway.modbusDevice.mqttClient-threads}")
    public void setPublicTheadNum(int publicTheadNum) {
        PUBLIC_THEAD_NUM = publicTheadNum;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMbMaster(MbMaster mbMaster) {
        this.mbMaster = mbMaster;
    }
}
