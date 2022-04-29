package stressTest;

import com.alibaba.fastjson.JSONObject;
import com.xothia.ModbusProxy;
import com.xothia.MqttProxy;
import com.xothia.springConfig.SpringConfig;
import de.gandev.modjn.ModbusServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jetlinks.mqtt.client.*;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : stressTest
 * @ClassName : .java
 * @createTime : 2022/4/16 13:33
 * @Email : huaxia889900@126.com
 * @Description : 用于自动生成配置文件。
 */
public class GenXml {
    public int slaveNumber=50;
    public static int intervalMill = 50;

    public int pattenNumber = 2;
    public static int attrNumber = 1;

    public static int upsTopics = 1;
    public static int quantity = 1;


    public static int PUBLIC_WORKER_THEAD_NUM = 32; //线程池线程数
    public static int PUBLIC_BOSS_THEAD_NUM = 4; //线程池线程数
    public static int MQTT_LISTENER_THEAD_NUM = 32; //监听MQTT线程池线程数


    public static int nowPort=5001;
    public int beginPort;
    public int endPort;

    public LinkedList<Integer> ports = new LinkedList<>();
    public LinkedList<String> topics_meter = new LinkedList<>();

    public static Long totalQ = 0L;
    public static double totalRT = 0L;
    public static Long beginTime = 0L;

    public static CopyOnWriteArrayList<Double> QPS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Double> RT = new CopyOnWriteArrayList<>();

    public static NioEventLoopGroup bossGroup,workerGroup;

    static {
        //初始化线程池
        workerGroup = new NioEventLoopGroup(PUBLIC_WORKER_THEAD_NUM);
        bossGroup = new NioEventLoopGroup(PUBLIC_BOSS_THEAD_NUM);
    }

    protected final Log logger = LogFactory.getLog(getClass()); //日志

    public void runTest() throws SchedulerException, ExecutionException, InterruptedException {
        //生成配置文件
        logger.info("生成配置文件...");
        init();
        logger.info("创建Modbus Slaves...");
        setModbusSlaves();

        logger.info("创建Mqtt Listener...");
        setMqttClientListener();

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        //Modbus设备代理服务
        final ModbusProxy modbusProxy = context.getBean(ModbusProxy.class);
        modbusProxy.run();
        //测量开始时间
        //beginTime = System.currentTimeMillis(); //毫秒

        //Mqtt设备代理服务
        final MqttProxy server = context.getBean("mqttProxy", MqttProxy.class);

        beginTime = System.nanoTime(); //毫秒
        server.run();
    }


    public void setMqttClientListener() throws InterruptedException, ExecutionException {
        final MqttClientConfig config = new MqttClientConfig();
        config.setClientId("MqttListener");
        config.setUsername("test");
        config.setPassword("test");
        config.setProtocolVersion(MqttVersion.MQTT_3_1_1);
        config.setReconnect(true);

        MqttClient mqttClient = MqttClient.create(config, ((topic, payload) -> {
            logger.info("Default Handler: " + topic + "=>" + payload.toString(StandardCharsets.UTF_8));
        }));

        mqttClient.setEventLoop(new NioEventLoopGroup(MQTT_LISTENER_THEAD_NUM));
        mqttClient.setCallback(new MqttClientCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.warn(mqttClient.getClientConfig().getClientId()+" Lost Connection to Broker. "+cause.getMessage());
            }

            @Override
            public void onSuccessfulReconnect() {
                logger.info(mqttClient.getClientConfig().getClientId()+"Successful Reconnect to Broker.");
            }
        });

        MqttConnectResult result;
        try{
            result = mqttClient.connect("127.0.0.1", 1883)
                    .await().get();
        }catch (Exception e){
            logger.error("MqttListener failed establishing tcp connection to broker."+e.getMessage());
            mqttClient.disconnect();
            return;
        }

        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            logger.error("broker refused MqttListener." + result.getReturnCode());
            mqttClient.disconnect();
        } else {
            logger.info(mqttClient.getClientConfig().getClientId() + ": MqttListener is connected to broker.");
        }
        //MQTT连接完成。

        for (String tp:topics_meter){
            mqttClient.on(tp, new MqttHandler() {
                @Override
                public void onMessage(String s, ByteBuf byteBuf) {
                    //测量RT
//                    final long nowTime = System.currentTimeMillis(); //毫秒
                    final long nowTime = System.nanoTime(); //纳秒

                    final JSONObject jsonObject = JSONObject.parseObject(byteBuf.toString(CharsetUtil.UTF_8));
                    final Long sendTime = jsonObject.getLong("timestamp");


                    totalQ++;
                    totalRT+=(nowTime-sendTime)/1000000.0;

                    if((nowTime-beginTime)/1000000000>10){
                        //每十秒归零并计算

                        final double QPS_real_time = totalQ * 1000000000.0 / (nowTime - beginTime);
                        final double real_time_Average_RT = totalRT/totalQ;

                        beginTime = System.nanoTime();
                        totalQ=0L;
                        totalRT=0L;

                        logger.info("real_time_Average_RT = "+real_time_Average_RT+"ms\n"
                                +"QPS_real_time = "+QPS_real_time);

                        QPS.add(QPS_real_time);
                        RT.add(real_time_Average_RT);

                        if(QPS.size()>1){
                            double av_qps, av_rt;
                            av_qps = av_rt = 0;
                            for(int i=1;i<QPS.size();i++){
                                av_qps+=QPS.get(i);
                                av_rt+=RT.get(i);
                            }
                            av_rt/=(QPS.size()-1);
                            av_qps/=(QPS.size()-1);
                            logger.info("total_Average_RT = "+av_rt+"ms\n"
                                    +"total_Average_QPS = "+av_qps);
                        }


                    }
                }
            });
        }

    }

    public void setModbusSlaves(){
        for(int port:ports){
            ModbusServer server = new ModbusServer(port, bossGroup, workerGroup);
            try {
                server.setup(new MyResponseHandler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void init(){
        //测试DOM4J
        try {
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            // 2、创建根节点rss
            Element conf = document.addElement("Configuration");
            final Element devices = conf.addElement("ModbusDevices");
            for(int i=0;i<slaveNumber;i++){
                final Element modbusSlave = devices.addElement("ModbusSlave");
                final Element hostPort = modbusSlave.addElement("HostPort");
                int p = genPort(beginPort, endPort);
                hostPort.setText(Integer.toString(p));
                ports.add(p);

                final Element pattens = modbusSlave.addElement("UpstreamPattens");
                for(int j=0;j<pattenNumber;j++){
                    final Element patten = pattens.addElement("UpstreamPatten");
                    final Element interval = patten.addElement("IntervalInMilliseconds");
                    interval.setText(String.valueOf(intervalMill));
                    final Element topics = patten.addElement("Topics");
                    for(int k=0;k<upsTopics;k++){
                        final Element topic = topics.addElement("Topic");
                        topic.setText("/testUpstream/"+"slave"+i+"/patten"+j+"/topic"+k);
                    }
                    //添加监听topic
                    topics_meter.add("/testUpstream/"+"slave"+i+"/patten"+j+"/topic0");

                    final Element attributes = patten.addElement("Attributes");
                    for(int k=0;k<attrNumber;k++){
                        final Element attr = attributes.addElement("Attribute");
                        attr.addElement("AttrName").setText("Attribute"+k);
                        attr.addElement("Address").setText(String.valueOf(k*quantity));
                        attr.addElement("Quantity").setText(String.valueOf(quantity));
                    }
                }
                final Element downstreamTopics = modbusSlave.addElement("DownstreamTopics");
                downstreamTopics.addElement("Topic").setText("/testDownstream/"+i);

                final Element proxyMqttClient = modbusSlave.addElement("ProxyMqttClient");
                proxyMqttClient.addElement("ClientId").setText("TestClient"+i);
                proxyMqttClient.addElement("Username").setText("TestClient"+i);
                proxyMqttClient.addElement("Password").setText("TestClient"+i);

            }


            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(4);
            format.setNewlines(true);
            format.setNewLineAfterDeclaration(true);
            // 设置编码格式
            format.setEncoding("UTF-8");
            FileWriter fileWriter = new FileWriter(new File("src/main/resources/MtmModbusSlaveConfigs.xml"));
            XMLWriter xmlWriter = new XMLWriter(fileWriter,format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);

            xmlWriter.close();
            System.out.println("生成xml文件成功");

        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public static int genPort()throws Exception{
        int res = -1;
        if(nowPort>65535){
            throw new Exception("All ports are using.");
        }
        for(int i=nowPort;i<=65535;i++){
            if(!isLoclePortUsing(i)){
                res = i;
                break;
            }
        }
        if(res == -1){
            throw new Exception("All ports are using.");
        }
        nowPort = res+1;
        return res;
    }

    public static int genPort(int bPort, int ePort)throws Exception{
        int res = -1;
        if(nowPort==5001){
            nowPort=bPort;
            res = bPort;
        }else if(nowPort == ePort){
            throw new Exception("All ports are using.");
        }
        else{
            nowPort++;
            res = nowPort;
        }
        return res;
    }

    /**
     * 查看本机某端口是否被占用
     * @param port  端口号
     * @return  如果被占用则返回true，否则返回false
     */
    public static boolean isLoclePortUsing(int port){
        boolean flag = true;
        try{
            flag = isPortUsing("127.0.0.1", port);
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据IP和端口号，查询其是否被占用
     * @param host  IP
     * @param port  端口号
     * @return  如果被占用，返回true；否则返回false
     */
    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try{
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {
            //如果所测试端口号没有被占用，那么会抛出异常，这里利用这个机制来判断
            //所以，这里在捕获异常后，什么也不用做
        }
        return flag;
    }

    public GenXml() {
    }

    public GenXml(int beginPort, int endPort) {
        this.beginPort = beginPort;
        this.endPort = endPort;
    }
}
