package com.xothia.bean.modbusSlave;

import com.xothia.util.Util;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusSlave
 * @ClassName : .java
 * @createTime : 2022/4/8 20:34
 * @Email : huaxia889900@126.com
 * @Description : 一个单例类，用于存储接收控制报文的Topics。
 */
@Valid
@Component("mbDownsPatten")
@Scope("prototype")
public class MbSlaveDownstreamPatten implements InitializingBean {
    @NotNull
    private String[] topics; //接受控制报文的topics

    public MbSlaveDownstreamPatten() {
    }

    public MbSlaveDownstreamPatten(String[] topics) {
        this.topics = topics;
    }

    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
    }

    public String[] getTopics() {
        return topics;
    }

}
