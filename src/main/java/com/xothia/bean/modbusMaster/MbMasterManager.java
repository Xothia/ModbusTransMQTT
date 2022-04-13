package com.xothia.bean.modbusMaster;

import com.xothia.bean.mqttClient.MqttClient;
import com.xothia.util.Util;
import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.exception.ConnectionException;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.handler.ModbusResponseHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusMaster
 * @ClassName : .java
 * @createTime : 2022/4/11 17:25
 * @Email : huaxia889900@126.com
 * @Description : Modbus master的代理类
 */
@Valid
@Component("mbMasterManager")
@Scope("prototype")
public class MbMasterManager implements MbMaster, InitializingBean {

    @NotNull
    private String hostAddress;  //tcp slave ip 地址

    @NotNull @Min(0) @Max(65535)
    private Integer hostPort;  //tcp slave 端口号

    @NotNull
    private MqttClient mqttClient;

    @NotNull
    private ModbusClient modbusClient;

    //transaction id to attribute name.
    @NotNull
    private final HashMap<Integer, String> attrMap = new HashMap<>();

    public MbMasterManager() {
    }

    public MbMasterManager(String hostAddress, Integer hostPort, MqttClient mqttClient) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.mqttClient = mqttClient;
    }

    @Override
    public void afterPropertiesSet() {
        this.modbusClient = new ModbusClient(hostAddress, hostPort);
        Util.valid(this);
    }

    public void doConnect() throws ConnectionException {
        modbusClient.setup(new ModbusResponseHandler() {
            @Override
            public void newResponse(ModbusFrame modbusFrame) {
                //这里应当调用mqtt client发送数据到topics。待施工...
                //Q: 如何在这里得到对应的AttrName?
                //A: 指定TransactionId, 将TID-attrName键值对放到这个类的map里。需要修改callModbusFunction方法。
                System.out.println(modbusFrame.toString());
            }
        });
    }

    @Override
    public Object requestSync(Integer functionCode, Integer address, Integer quantity) throws ConnectionException, ErrorResponseException, NoResponseException {
        switch (functionCode){
            case 1:
                return modbusClient.readCoils(address, quantity);

            case 2:
                return modbusClient.readDiscreteInputs(address, quantity);

            case 3:
                return modbusClient.readHoldingRegisters(address, quantity);

            case 4:
                return modbusClient.readInputRegisters(address, quantity);
        }
        return null;
    }

    public int requestAsync(Integer functionCode, Integer address, Integer quantity) throws ConnectionException {
        switch (functionCode){
            case 1:
                return modbusClient.readCoilsAsync(address, quantity);

            case 2:
                return modbusClient.readDiscreteInputsAsync(address, quantity);

            case 3:
                return modbusClient.readHoldingRegistersAsync(address, quantity);

            case 4:
                return modbusClient.readInputRegistersAsync(address, quantity);

        }
        throw new RuntimeException("function code do not exist "+functionCode);
        //return -1;
    }

    public void putAttrMap(int transactionId, String attrName){
        this.attrMap.put(transactionId, attrName);
    }

}
