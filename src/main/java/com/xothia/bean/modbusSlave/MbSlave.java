package com.xothia.bean.modbusSlave;

import com.xothia.bean.modbusMaster.MbMasterManager;
import com.xothia.bean.mqttClient.MqttClientManager;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusSlave
 * @ClassName : .java
 * @createTime : 2022/4/7 15:45
 * @Email : huaxia889900@126.com
 * @Description : Modbus Slave Interface
 */
public interface MbSlave {
    MqttClientManager getMqttClientManager();

    MbMasterManager getMbMasterManager();

    MbSlaveUpstreamPatten[] getUpstreamPatten();
}
