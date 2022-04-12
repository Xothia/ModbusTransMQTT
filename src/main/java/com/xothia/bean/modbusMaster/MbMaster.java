package com.xothia.bean.modbusMaster;

import de.gandev.modjn.entity.exception.ConnectionException;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusMaster
 * @ClassName : .java
 * @createTime : 2022/4/11 17:23
 * @Email : huaxia889900@126.com
 * @Description : Modbus Master接口类。
 */
public interface MbMaster {
    void doConnect() throws ConnectionException;
    void requestAsync(Integer functionCode, Integer address, Integer quantity) throws ConnectionException;
    Object requestSync(Integer functionCode, Integer address, Integer quantity) throws ConnectionException, ErrorResponseException, NoResponseException;

}
