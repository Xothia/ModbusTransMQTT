package stressTest;

import de.gandev.modjn.entity.func.WriteSingleCoil;
import de.gandev.modjn.entity.func.WriteSingleRegister;
import de.gandev.modjn.entity.func.request.*;
import de.gandev.modjn.entity.func.response.*;
import de.gandev.modjn.handler.ModbusRequestHandler;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : stressTest
 * @ClassName : .java
 * @createTime : 2022/4/16 15:46
 * @Email : huaxia889900@126.com
 * @Description : 自定义modbus server response
 */
public class MyResponseHandler extends ModbusRequestHandler {
    @Override
    protected WriteSingleCoil writeSingleCoil(WriteSingleCoil writeSingleCoil) {
        return null;
    }

    @Override
    protected WriteSingleRegister writeSingleRegister(WriteSingleRegister writeSingleRegister) {
        return null;
    }

    @Override
    protected ReadCoilsResponse readCoilsRequest(ReadCoilsRequest readCoilsRequest) {
        return null;
    }

    @Override
    protected ReadDiscreteInputsResponse readDiscreteInputsRequest(ReadDiscreteInputsRequest readDiscreteInputsRequest) {
        return null;
    }

    @Override
    protected ReadInputRegistersResponse readInputRegistersRequest(ReadInputRegistersRequest readInputRegistersRequest) {
        int[] registers = new int[readInputRegistersRequest.getQuantityOfInputRegisters()];
        registers[0] = 12;
        return new ReadInputRegistersResponse(registers);
    }

    @Override
    protected ReadHoldingRegistersResponse readHoldingRegistersRequest(ReadHoldingRegistersRequest readHoldingRegistersRequest) {
        int[] registers = new int[readHoldingRegistersRequest.getQuantityOfInputRegisters()];
        registers[0] = 12;
        return new ReadHoldingRegistersResponse(registers);
    }

    @Override
    protected WriteMultipleRegistersResponse writeMultipleRegistersRequest(WriteMultipleRegistersRequest writeMultipleRegistersRequest) {
        return null;
    }

    @Override
    protected WriteMultipleCoilsResponse writeMultipleCoilsRequest(WriteMultipleCoilsRequest writeMultipleCoilsRequest) {
        return null;
    }
}
