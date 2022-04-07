package com.xothia.bean.modbusSlave;

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
 *
 */
class MbSlaveUpstreamPatten {
    private String[] topics; //被发布数据的topics
    private String cronExpr; //Cron表达式
    private int intervalInMilliseconds; //触发间隔（毫秒）

    MbSlaveUpstreamPatten(String[] topics, String cronExpr, int intervalInMilliseconds) {
        this.topics = topics;
        this.cronExpr = cronExpr;
        this.intervalInMilliseconds = intervalInMilliseconds;
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

    @Override
    public String toString() {
        return "MbSlaveUpstreamPatten{" +
                "topics=" + Arrays.toString(topics) +
                ", cronExpr='" + cronExpr + '\'' +
                ", intervalInMilliseconds=" + intervalInMilliseconds +
                '}';
    }
}
