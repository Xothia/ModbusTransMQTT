import com.xothia.ModbusProxy;
import com.xothia.MqttProxy;
import com.xothia.bean.modbusSlave.MbSlaveGroup;
import com.xothia.bean.mqttClient.MqttClientManager;
import com.xothia.springConfig.SpringConfig;
import com.xothia.util.UpstreamFormat;
import com.xothia.util.Util;
import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.func.response.ReadCoilsResponse;
import de.gandev.modjn.example.Example;
import de.gandev.modjn.handler.ModbusResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetlinks.mqtt.client.*;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : java
 * @ClassName : TestFunc.java
 * @createTime : 2022/3/12 15:38
 * @Email : huaxia889900@126.com
 * @Description :测试类
 */
public class TestFunc {
    @Test
    public void test0(){
        ApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        MqttProxy server = context.getBean("mqttProxy", MqttProxy.class);
    }

    @Test
    public void test1(){
        System.out.println(Double.toString(Math.random()).substring(0,12));
        System.out.println(Double.toString(Math.random()).substring(0,12));
        System.out.println(Double.toString(Math.random()).substring(0,12));
    }

    @Test
    public void test2(){
        Example.main(null);
    }

    @Test
    public void test3() throws Exception{
        //Modbus Master测试
        ModbusClient modbusClient = new ModbusClient("127.0.0.1", 7778);
        //modbusClient.setup();
        modbusClient.setup(new ModbusResponseHandler() {
            @Override
            public void newResponse(ModbusFrame modbusFrame) {
                final BitSet bitSet = ((ReadCoilsResponse) modbusFrame.getFunction()).getCoilStatus();
                final UpstreamFormat format = new UpstreamFormat();
                format.put("tat", Util.bitset2bool(bitSet));
                System.out.println(format.getJsonStr());
            }
        });

        modbusClient.readCoilsAsync(0,3);
//        modbusClient.readHoldingRegistersAsync(0, 1);


        //ReadHoldingRegistersResponse response = modbusClient.readHoldingRegisters(0, 1);
        //System.out.println(response);


        while(true){

        }
        //modbusClient.close();

    }

    @Test
    public void test4()throws Exception{
        //MQTT Client测试

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        //Conf config = context.getBean("conf", Conf.class);

        EventLoopGroup loop = new NioEventLoopGroup(); //!!!
        MqttClient mqttClient = MqttClient.create(new MqttClientConfig(),((topic, payload) -> {
            System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
        }));
        mqttClient.setEventLoop(loop);
//        mqttClient.getClientConfig().setClientId(config.clientId);
//        mqttClient.getClientConfig().setUsername(config.usesrname);
//        mqttClient.getClientConfig().setPassword(config.passwd);
        mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);
        mqttClient.getClientConfig().setReconnect(true);
        mqttClient.setCallback(new MqttClientCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                cause.printStackTrace();
            }

            @Override
            public void onSuccessfulReconnect() {

            }
        });
        MqttConnectResult result = mqttClient.connect("127.0.0.1", 1883).await().get();
        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            System.out.println("error:" + result.getReturnCode());
            mqttClient.disconnect();
        } else {
            System.out.println("success");
//    mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"event\"}", StandardCharsets.UTF_8));
        }
        Future<Void> on = mqttClient.on("/broadcast/gwdxm71ywNi/hahaha", new MqttHandler() {
            @Override
            public void onMessage(String s, ByteBuf byteBuf) {
                System.out.println(s + "=>" + byteBuf.toString(CharsetUtil.UTF_8));
            }
        });

        final UpstreamFormat format = new UpstreamFormat();
        format.put("teste", 1);
        format.put("test2", "fasd");
        mqttClient.publish("/hahaha", Unpooled.wrappedBuffer("imatestsstring".getBytes(CharsetUtil.UTF_8)));
        mqttClient.publish("/hahaha", Unpooled.wrappedBuffer(format.getJsonBytes()));


        mqttClient.disconnect();

        while(true){

        }

    }

    @Test
    public void test4_1()throws Exception{
        //MQTT Client测试

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        final MqttClientManager manager = context.getBean("mqttCliManager", MqttClientManager.class);


        while (true){

        }

    }

    @Test
    public void test5() throws Exception{
        //测试quartz
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail jobDetail = JobBuilder
                .newJob(TestJob.class)
                .withIdentity("fuf", "group1")
                .usingJobData("count", 0)
                .build();
        Trigger build = TriggerBuilder
                .newTrigger()
                //.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(5))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(2000).repeatForever())
                .startNow()
                .withIdentity("testTrigger", "group1")
                .build();
        scheduler.scheduleJob(jobDetail, build);
        scheduler.start();
        while(true){
        }
        //scheduler.shutdown(true); //完成任务后再关闭

    }
    //必须public
    @PersistJobDataAfterExecution //复用jobdata
    public static class TestJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            int count = dataMap.getInt("count");
            System.out.println(new Date()+" count:"+count);
            dataMap.put("count", count+1);

        }
    }

    @Test
    public void test6(){
        //测试DOM4J
        Document dom = Util.load("MtmModbusSlaveConfigs.xml");
        Element root = dom.getRootElement();

        final List<Element> elementList = root.element("ModbusDevices").elements("ModbusSlave");
        final Element modbusDevices = root.element("ModbusDevices");
//        System.out.println(elementList.size());

        for (Iterator<Element> i = modbusDevices.elementIterator(); i.hasNext();) {
            Element el = i.next();
            System.out.println(el.getName());
        }
    }

    @Test
    public void test7() throws Exception{
        //测试validator
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        final MbSlaveGroup group = context.getBean("mbSlaveGroup", MbSlaveGroup.class);
        System.out.println("test");
//        String[] strings = new String[]{"ab", "cd"};
//        MbSlaveUpstreamPatten patten1 = (MbSlaveUpstreamPatten)context.getBean("mbUpsPatten", new Object[]{strings, "baha"});
//        Util.valid(patten1);

    }

    @Test
    public void test8() throws Exception{

        UpstreamFormat upstreamFormat = new UpstreamFormat();
        upstreamFormat.put("number", 1);
        upstreamFormat.put("name", "dasao");
        System.out.println(upstreamFormat.getJsonStr());


    }

    @Test
    public void test9() throws Exception{
        //模拟modbus 代理服务
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        final ModbusProxy modbusProxy = context.getBean(ModbusProxy.class);
        modbusProxy.run();
        while(true){

        }

    }
    @Test
    public void test10() throws Exception{
        //模拟modbus 代理服务
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        final ModbusProxy modbusProxy = context.getBean(ModbusProxy.class);
        modbusProxy.run();
        while(true){

        }

    }

}
