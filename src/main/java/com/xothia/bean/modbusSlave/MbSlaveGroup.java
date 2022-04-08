package com.xothia.bean.modbusSlave;

import com.xothia.util.Util;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusSlave
 * @ClassName : .java
 * @createTime : 2022/4/8 0:38
 * @Email : huaxia889900@126.com
 * @Description : 这是Modbus Slave集合类，单例模式。
 * ModbusSlave及其持有的MbSlaveUpstreamPatten在此被从XML文件中读取并实例化。
 */

public class MbSlaveGroup implements InitializingBean {
    @NotNull
    private final MbSlave[] slaveGroup;

    @Autowired
    public MbSlaveGroup(ApplicationContext ctx) {
        final Element root = Util.load("MtmModbusSlaveConfigs.xml").getRootElement();
        final List<Element> slaveList = root.element("ModbusDevices").elements("ModbusSlave");
        for (Element e: slaveList) {
            System.out.println(e.getName());
        }
        System.out.println("fukfuk");





        //
        slaveGroup=null;
        MbSlaveUpstreamPatten upsPatten = ctx.getBean("mbUpsPatten", MbSlaveUpstreamPatten.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //Util.valid(this);
    }
}
