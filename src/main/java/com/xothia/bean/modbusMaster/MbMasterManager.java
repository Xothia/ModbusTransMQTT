package com.xothia.bean.modbusMaster;

import com.xothia.bean.modbusSlave.MbSlaveUpstreamPatten;
import com.xothia.bean.mqttClient.MqttClient;
import com.xothia.util.DownstreamFormat;
import com.xothia.util.UpstreamFormat;
import com.xothia.util.Util;
import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.ModbusFunction;
import de.gandev.modjn.entity.exception.ConnectionException;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.ModbusError;
import de.gandev.modjn.entity.func.response.ReadCoilsResponse;
import de.gandev.modjn.entity.func.response.ReadDiscreteInputsResponse;
import de.gandev.modjn.entity.func.response.ReadHoldingRegistersResponse;
import de.gandev.modjn.entity.func.response.ReadInputRegistersResponse;
import de.gandev.modjn.handler.ModbusResponseHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ConcurrentHashMap<Integer, String> attrMap = new ConcurrentHashMap<>();

    //transaction id to topics[].
    @NotNull
    private final ConcurrentHashMap<Integer, MbSlaveUpstreamPatten> upsMap = new ConcurrentHashMap<>();


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
                //这里应当调用mqtt client发送数据到topics。
                //Q: 如何在这里得到对应的AttrName?
                //A: 指定TransactionId, 将TID-attrName键值对放到这个类的map里。需要修改callModbusFunction方法。
                //Q: 无法写多个COILS时全为false
                final UpstreamFormat format = new UpstreamFormat();
                final int tranId = modbusFrame.getHeader().getTransactionIdentifier();
                final ModbusFunction function = modbusFrame.getFunction();

                if(isReadResponse(function)){
                    try {
                        final String attrName = getAttrName(tranId);
                        final MbSlaveUpstreamPatten patten = getUps(tranId);
                        if(function instanceof ReadHoldingRegistersResponse){
                            format.put(attrName, ((ReadHoldingRegistersResponse) function).getRegisters());
                        }
                        else if(function instanceof ReadCoilsResponse){
                            format.put(attrName, Util.bitset2bool(((ReadCoilsResponse) function).getCoilStatus()));
                        }
                        else if(function instanceof ReadInputRegistersResponse){
                            format.put(attrName, ((ReadInputRegistersResponse) function).getInputRegisters());

                        }
                        else if(function instanceof ReadDiscreteInputsResponse){
                            format.put(attrName, Util.bitset2bool(((ReadDiscreteInputsResponse) function).getInputStatus()));
                        }
                        /////////////调用Mqtt Client发数据//////////////
                        mqttClient.publish(patten.getTopics(), format, patten.getQos());

                    } catch (Exception e) {
                        Util.LOGGER.error("ModbusResponseHandler: "+e.getMessage());
                    }
                }
                else if(function instanceof ModbusError){
                    Util.LOGGER.error("ModbusResponseHandler: "+((ModbusError) function).getExceptionMessage());
                }

                //Util.LOGGER.info(format.getJsonStr());
            }
        });
    }

    private boolean isReadResponse(ModbusFunction f){
        return f instanceof ReadHoldingRegistersResponse || f instanceof ReadCoilsResponse || f instanceof ReadInputRegistersResponse || f instanceof ReadDiscreteInputsResponse;
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

    public void writeAsync(DownstreamFormat format) throws ConnectionException, ErrorResponseException, NoResponseException {

        switch (format.getType()){
            case WRITE_SINGLE_REG:
                modbusClient.writeSingleRegisterAsync(format.getAddress(), format.getReg());
                break;
            case WRITE_SINGLE_COIL:
                modbusClient.writeSingleCoil(format.getAddress(), format.getState());
                break;
            case WRITE_MULTI_COIL:
                modbusClient.writeMultipleCoilsAsync(format.getAddress(), format.getQuantity(), Util.bool2bitset(format.getStates()));
                break;
            case WRITE_MULTI_REG:
                modbusClient.writeMultipleRegistersAsync(format.getAddress(), format.getQuantity(), format.getRegs());
                break;
        }
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

    private String getAttrName(int id) throws Exception{
        if(!this.attrMap.containsKey(id)){
            Util.LOGGER.warn("MbMasterManager: transaction id do not exist in hashmap.");
            throw new Exception("MbMasterManager: transaction id do not exist in hashmap.");
        }
        return this.attrMap.getOrDefault(id, null);
    }

    public void putAttrMap(int transactionId, String attrName){
        this.attrMap.put(transactionId, attrName);
    }

    private MbSlaveUpstreamPatten getUps(int id) throws Exception{
        if(!this.upsMap.containsKey(id)){
            Util.LOGGER.warn("MbMasterManager: transaction id do not exist in hashmap [upsMap].");
            throw new Exception("MbMasterManager: transaction id do not exist in hashmap [upsMap].");
        }
        return this.upsMap.getOrDefault(id, null);
    }

    public void putUpsMap(int transactionId, MbSlaveUpstreamPatten ups){
        this.upsMap.put(transactionId, ups);
    }


}
