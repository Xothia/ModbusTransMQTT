package com.xothia.bean.modbusSlave;

import com.xothia.bean.modbusMaster.MbMasterManager;
import com.xothia.bean.mqttClient.MqttClientManager;
import com.xothia.util.Util;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Max;
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

    @PositiveOrZero
    private Integer slaveId; //slave 设备标识号，仅对通过Tcp网关接入的串行设备有意义

    @NotNull @PositiveOrZero @Max(4)
    private Integer function; //功能码

    @NotNull
    private MbSlaveUpstreamPatten[] upstreamPatten; //上报数据行为的描述

    @NotNull
    private MbSlaveDownstreamPatten downstreamPatten; //下行指令行为的描述

    @NotNull
    private MqttClientManager mqttClientManager; //对应MqttClient的代理类 未完工

    @NotNull
    private MbMasterManager mbMasterManager; //对应Modbus master的代理类 未完工

    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
    }

    public MbTcpSlave() {
    }

    public MbTcpSlave(Integer slaveId, Integer function, MbSlaveUpstreamPatten[] upstreamPatten, MbSlaveDownstreamPatten downstreamPatten, MqttClientManager mqttClientManager, MbMasterManager mbMasterManager) {
        this.slaveId = slaveId;
        this.function = function;
        this.upstreamPatten = upstreamPatten;
        this.downstreamPatten = downstreamPatten;
        this.mqttClientManager = mqttClientManager;
        this.mbMasterManager = mbMasterManager;
    }

    @Override
    public MqttClientManager getMqttClientManager() {
        return mqttClientManager;
    }

    @Override
    public MbSlaveUpstreamPatten[] getUpstreamPatten() {
        return upstreamPatten;
    }


    public Integer getFunction() {
        return function;
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

    public MbMasterManager getMbMasterManager() {
        return mbMasterManager;
    }
}
