package com.xothia.bean.modbusSlave;


import com.xothia.util.Attribute;
import com.xothia.util.Util;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.bean.modbusSlave
 * @ClassName : .java
 * @createTime : 2022/4/7 15:49
 * @Email : huaxia889900@126.com
 * @Description : 用于描述一个Modbus Slave/Server 上传其数据的模式（如每隔2秒向某个/几个topic 上传数据）
 * 可以选择cron表达式或者以毫秒为间隔的触发形式（二选一）
 * cron表达式优先级大于毫秒间隔
 */
@Valid
@Component("mbUpsPatten")
@Scope("prototype")
public class MbSlaveUpstreamPatten implements InitializingBean {
    @NotNull @PositiveOrZero @Max(2)
    private Integer qos = 0;

    @NotNull
    private String[] topics; //被发布数据的topics
    private String cronExpr; //Cron表达式
    @PositiveOrZero
    private int intervalInMilliseconds; //触发间隔（毫秒）

    @NotNull
    private Attribute[] attributes; //上报数据具体参数

    //用于测试响应时间
    public long timestamp;

    public MbSlaveUpstreamPatten() {
    }

    public MbSlaveUpstreamPatten(Integer qos, String[] topics, String cronExpr, int intervalInMilliseconds, Attribute[] attributes) {
        this.qos = qos;
        this.topics = topics;
        this.cronExpr = cronExpr;
        this.intervalInMilliseconds = intervalInMilliseconds;
        this.attributes = attributes;
    }

    public MbSlaveUpstreamPatten(String[] topics, String cronExpr, int intervalInMilliseconds, Attribute[] attributes) {
        this.topics = topics;
        this.cronExpr = cronExpr;
        this.intervalInMilliseconds = intervalInMilliseconds;
        this.attributes = attributes;
    }

    public MbSlaveUpstreamPatten(String[] topics, String cronExpr, int intervalInMilliseconds) {
        this.topics = topics;
        this.cronExpr = cronExpr;
        this.intervalInMilliseconds = intervalInMilliseconds;
    }

    public MbSlaveUpstreamPatten(String[] topics, String cronExpr) {
        this.topics = topics;
        this.cronExpr = cronExpr;
        this.intervalInMilliseconds = 0;
    }

    public MbSlaveUpstreamPatten(String[] topics, int intervalInMilliseconds) {
        this.topics = topics;
        this.cronExpr = null;
        this.intervalInMilliseconds = intervalInMilliseconds;
    }

    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
        if(Util.isNullOrBlank(cronExpr)&&intervalInMilliseconds==0){
            throw new RuntimeException("Bean参数不合法，检查xml配置是否正确。");
        }
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }

    public int getIntervalInMilliseconds() {
        return intervalInMilliseconds;
    }

    public void setIntervalInMilliseconds(int intervalInMilliseconds) {
        this.intervalInMilliseconds = intervalInMilliseconds;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public Integer getQos() {
        return qos;
    }

    @Override
    public String toString() {
        return "MbSlaveUpstreamPatten{" +
                "topics=" + Arrays.toString(topics) +
                ", cronExpr='" + cronExpr + '\'' +
                ", intervalInMilliseconds=" + intervalInMilliseconds +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }
}
