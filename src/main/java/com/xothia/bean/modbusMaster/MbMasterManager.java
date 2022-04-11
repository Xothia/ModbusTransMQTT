package com.xothia.bean.modbusMaster;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

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


    @Override
    public void afterPropertiesSet() {

    }
}
