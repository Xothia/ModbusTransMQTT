package com.xothia.bean.modbusSlave;

import com.xothia.bean.modbusMaster.MbMaster;
import com.xothia.bean.modbusMaster.MbMasterManager;
import com.xothia.bean.mqttClient.MqttClient;
import com.xothia.bean.mqttClient.MqttClientManager;
import com.xothia.util.Attribute;
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
                case "Function":
                case "SlaveId":
                    map.put(ele.getName(), Integer.parseInt(ele.getTextTrim()));
                    break;
            }
        }
        //构建Mqtt Manager
        final MqttClient mqttClientManager = mqttClientManagerBuilder(conf, ctx);
        map.put("MqttClientManager", mqttClientManager);

        //构建Modbus master manager
        final MbMaster mbMasterManager = buildMbMasterManager(map, ctx);
        mqttClientManager.setMbMaster(mbMasterManager);
        map.put("MbMasterManager", mbMasterManager);

        return ctx.getBean(MbTcpSlave.class, map.getOrDefault("SlaveId", 0), map.getOrDefault("Function", 3),map.get("UpstreamPattens"), map.get("DownstreamTopics"), map.get("MqttClientManager"), map.get("MbMasterManager"));
    }

    private MbMasterManager buildMbMasterManager(HashMap<String, Object> map, ApplicationContext ctx){
        return ctx.getBean(MbMasterManager.class, map.getOrDefault("HostAddress", "127.0.0.1"), map.getOrDefault("HostPort", 502), map.get("MqttClientManager"));
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
        return ctx.getBean(MqttClientManager.class, (Object)conf);
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
                case "Qos":
                    map.put("Qos", Integer.parseInt(ele.getTextTrim()));
                    break;

                case "CronExpr":
                    map.put("CronExpr", ele.getTextTrim());
                    break;

                case "IntervalInMilliseconds":
                    map.put("IntervalInMilliseconds", Integer.parseInt(ele.getTextTrim()));
                    break;

                case "Attributes":
                    map.put("Attributes", getAttributes(ele, ctx));
                    break;

            }
        }
        return ctx.getBean(MbSlaveUpstreamPatten.class, map.getOrDefault("Qos", 0),map.get("Topics"),map.get("CronExpr"),map.get("IntervalInMilliseconds"), map.get("Attributes"));
    }

    //解析attributes标签
    private Attribute[] getAttributes(Element attributes, ApplicationContext ctx){
        final List<Element> attrList = attributes.elements("Attribute");
        final Attribute[] res = new Attribute[attrList.size()];

        int index=0;
        for (Element attr:attrList) {
            res[index++] = getAttribute(attr, ctx);
        }
        return res;

    }

    private Attribute getAttribute(Element attr, ApplicationContext ctx){
        final HashMap<String, Object> map = new HashMap<>();
        for (Iterator<Element> i = attr.elementIterator(); i.hasNext();) {
            Element ele = i.next();
            switch (ele.getName()){
                case "AttrName":
                    map.put("AttrName", ele.getTextTrim());
                    break;

                case "Address":
                    map.put("Address", Integer.parseInt(ele.getTextTrim()));
                    break;

                case "Quantity":
                    map.put("Quantity", Integer.parseInt(ele.getTextTrim()));
                    break;
            }
        }
        return ctx.getBean(Attribute.class, map.get("AttrName"), map.getOrDefault("Address", 0), map.getOrDefault("Quantity", 1));
    }


    public MbSlave[] getSlaveGroup() {
        return slaveGroup;
    }

    public static class MqttConfig {
        //无需校验参数，空缺将从properties配置中获取默认值。
        public String BrokerAddress;
        public Integer BrokerPort;
        public String ClientId;
        public String Username;
        public String Password;
    }

}
