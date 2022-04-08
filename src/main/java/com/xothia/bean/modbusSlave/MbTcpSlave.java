package com.xothia.bean.modbusSlave;

import com.xothia.bean.mqttClient.MqttClientManager;
import com.xothia.util.Util;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusSlave
 * @ClassName : .java
 * @createTime : 2022/4/7 16:56
 * @Email : huaxia889900@126.com
 * @Description : 一个MbTcpSlave类映射到一个真实Modbus Tcp Slave设备，该类从配置文件中生成。
 */
@Valid
@Component
@Scope("prototype")
class MbTcpSlave implements MbSlave, InitializingBean {
    @NotNull
    private String hostAddress; //tcp slave ip 地址

    @NotNull
    private Integer hostPort; //tcp slave 端口号

    @PositiveOrZero
    private Integer slaveId; //slave 设备标识号，仅对通过Tcp网关接入的串行设备有意义

    @NotNull
    private MbSlaveUpstreamPatten[] upstreamPatten; //上报数据行为的描述

    @NotNull
    private MbSlaveDownstreamPatten downstreamPatten; //上报数据行为的描述

    @NotNull
    private MqttClientManager mqttClientManager; //对应MqttClient的引用 未完工

    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
    }

    public MbTcpSlave() {
    }

    public MbTcpSlave(String hostAddress, Integer hostPort, Integer slaveId, MbSlaveUpstreamPatten[] upstreamPatten, MbSlaveDownstreamPatten downstreamPatten, MqttClientManager mqttClientManager) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.slaveId = slaveId;
        this.upstreamPatten = upstreamPatten;
        this.downstreamPatten = downstreamPatten;
        this.mqttClientManager = mqttClientManager;
    }

    @Override
    public MqttClientManager getMqttClientManager() {
        return mqttClientManager;
    }

    @Override
    public MbSlaveUpstreamPatten[] getUpstreamPatten() {
        return upstreamPatten;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public Integer getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(Integer slaveId) {
        this.slaveId = slaveId;
    }

    public void setUpstreamPatten(MbSlaveUpstreamPatten[] upstreamPatten) {
        this.upstreamPatten = upstreamPatten;
    }

    public void setMqttClientManager(MqttClientManager mqttClientManager) {
        this.mqttClientManager = mqttClientManager;
    }

    public MbSlaveDownstreamPatten getDownstreamPatten() {
        return downstreamPatten;
    }

    public void setDownstreamPatten(MbSlaveDownstreamPatten downstreamPatten) {
        this.downstreamPatten = downstreamPatten;
    }
}
