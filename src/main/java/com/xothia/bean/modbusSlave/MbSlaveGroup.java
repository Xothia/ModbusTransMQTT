package com.xothia.bean.modbusSlave;

import com.xothia.bean.mqttClient.MqttClientManager;
import com.xothia.util.Util;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Iterator;
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
@Component
public class MbSlaveGroup implements InitializingBean {

    @NotNull
    private final MbSlave[] slaveGroup;

    @Autowired
    public MbSlaveGroup(ApplicationContext ctx) {
        final Element root = Util.load("MtmModbusSlaveConfigs.xml").getRootElement();
        final List<Element> slaveList = root.element("ModbusDevices").elements("ModbusSlave");
        //构造slaveGroup
        final MbSlave[] mbSlaves = new MbSlave[slaveList.size()];

        int index=0;
        for (Element e: slaveList) {
            mbSlaves[index++] = getSlave(e, ctx);
        }
        slaveGroup=mbSlaves;
    }

    @Override
    public void afterPropertiesSet(){
        Util.valid(this);
    }

    //解析XML配置文件中ModbusSlave标签
    private MbSlave getSlave(Element slave, ApplicationContext ctx){
        final HashMap<String, Object> map = new HashMap<>();
        final MqttConfig conf = new MqttConfig();

        for (Iterator<Element> i = slave.elementIterator(); i.hasNext();){
            Element ele = i.next();

            switch (ele.getName()){
                case "UpstreamPattens":
                    map.put("UpstreamPattens", getPattens(ele, ctx));
                    break;
                case "DownstreamTopics":
                    map.put("DownstreamTopics", getDownTopics(ele, ctx));
                    break;

                case "TargetMqttBroker":
                case "ProxyMqttClient":
                    buildMqttConf(conf, ele);
                    break;

                case "HostAddress":
                    map.put("HostAddress", ele.getTextTrim());
                    break;

                case "HostPort":
                case "SlaveId":
                    map.put(ele.getName(), Integer.parseInt(ele.getTextTrim()));
                    break;
            }
        }
        //构建Manager
        map.put("MqttClientManager", mqttClientManagerBuilder(conf, ctx));

        return ctx.getBean(MbTcpSlave.class, map.get("HostAddress"), map.get("HostPort"), map.get("SlaveId"), map.get("UpstreamPattens"), map.get("DownstreamTopics"), map.get("MqttClientManager"));
    }

    private MbSlaveDownstreamPatten getDownTopics(Element downTopics, ApplicationContext ctx) {
        final List<Element> topicList = downTopics.elements("Topic");
        final String[] topics = new String[topicList.size()];
        int index=0;
        for (Element e:topicList) {
            topics[index++] = e.getTextTrim();
        }

        return ctx.getBean(MbSlaveDownstreamPatten.class, (Object)topics);
    }

    //解析XML配置文件中Mqtt相关标签
    private void buildMqttConf(MqttConfig confTobeBuild, Element mqttConf) {
        switch(mqttConf.getName()){
            case "TargetMqttBroker":
                confTobeBuild.BrokerAddress = mqttConf.element("BrokerAddress").getTextTrim();
                confTobeBuild.BrokerPort = Integer.parseInt(mqttConf.element("BrokerPort").getTextTrim());
                break;
            case "ProxyMqttClient":
                confTobeBuild.ClientId = mqttConf.element("ClientId").getTextTrim();
                confTobeBuild.Username = mqttConf.element("Username").getTextTrim();
                confTobeBuild.Password = mqttConf.element("Password").getTextTrim();
                break;
        }
    }

    //根据MqttConfig构建Manager
    private MqttClientManager mqttClientManagerBuilder(MqttConfig conf, ApplicationContext ctx){
        //待施工

        return new MqttClientManager();
    }

    //解析XML配置文件中UpstreamPattens标签
    private MbSlaveUpstreamPatten[] getPattens(Element pattens, ApplicationContext ctx){
        final List<Element> pattenList = pattens.elements("UpstreamPatten");
        final MbSlaveUpstreamPatten[] res = new MbSlaveUpstreamPatten[pattenList.size()];

        int index=0;
        for (Element patten:pattenList) {
            res[index++] = getPatten(patten, ctx);
        }
        return res;
    }

    //解析单个UpstreamPatten
    private MbSlaveUpstreamPatten getPatten(Element patten, ApplicationContext ctx) {
        final HashMap<String, Object> map = new HashMap<>();

        final List<Element> topicList = patten.element("Topics").elements("Topic");
        final String[] topics = new String[topicList.size()];
        int index=0;
        for (Element e:topicList) {
            topics[index++] = e.getTextTrim();
        }
        //读取topics完毕
        map.put("Topics", topics);

        for (Iterator<Element> i = patten.elementIterator(); i.hasNext();) {
            Element ele = i.next();
            switch (ele.getName()){
                case "CronExpr":
                    map.put("CronExpr", ele.getTextTrim());
                    break;

                case "IntervalInMilliseconds":
                    map.put("IntervalInMilliseconds", Integer.parseInt(ele.getTextTrim()));
                    break;
            }
        }
        return ctx.getBean(MbSlaveUpstreamPatten.class, map.get("Topics"),map.get("CronExpr"),map.get("IntervalInMilliseconds"));
    }

    public MbSlave[] getSlaveGroup() {
        return slaveGroup;
    }

    private static class MqttConfig {
        //无需校验参数，空缺将从properties配置中获取默认值。
        public String BrokerAddress;
        public Integer BrokerPort;
        public String ClientId;
        public String Username;
        public String Password;
    }

}
