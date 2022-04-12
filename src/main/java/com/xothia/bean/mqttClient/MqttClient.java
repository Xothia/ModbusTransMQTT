package com.xothia.bean.mqttClient;

import com.xothia.bean.modbusMaster.MbMaster;
import com.xothia.util.UpstreamFormat;
import io.netty.util.concurrent.Future;
import org.jetlinks.mqtt.client.MqttHandler;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.mqttClient
 * @ClassName : .java
 * @createTime : 2022/4/9 19:18
 * @Email : huaxia889900@126.com
 * @Description : MqttClient 接口，表示应当实现的功能。
 */
public interface MqttClient {
    //执行连接到Broker的耗时操作
    void doConnect();

    //关闭连接
    void disconnect();

    //订阅一个topic
    Future<Void> subscribe(String topic, MqttHandler handler);

    //向一个topic publish数据
    void publish(String[] topics, UpstreamFormat data, Integer qos);

    //设置modbus master***
    void setMbMaster(MbMaster mbMaster);

}
