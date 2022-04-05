package com.xothia.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : PACKAGE_NAME
 * @ClassName : .java
 * @createTime : 2022/4/5 15:37
 * @Email : huaxia889900@126.com
 * @Description :
 */
@Component(value = "conf")
@PropertySource(value={"classpath:Mtm.properties"})
public class Conf {
    @Value("${gateway.mqttDevice.clientId}")
    public String clientId;

    @Value("${gateway.mqttDevice.userName}")
    public String usesrname;

    @Value("${gateway.mqttDevice.password}")
    public String passwd;

    @Value("${gateway.remote.url}")
    public String mqttHostUrl;

    @Value("${gateway.remote.port}")
    public Integer port;

}
