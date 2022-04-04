import com.xothia.MqttProxy;
import de.gandev.modjn.example.Example;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.jetlinks.mqtt.client.MqttClient;
import org.jetlinks.mqtt.client.MqttClientConfig;
import org.jetlinks.mqtt.client.MqttConnectResult;
import org.jetlinks.mqtt.client.MqttHandler;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        MqttProxy server = context.getBean("mqttProxy", MqttProxy.class);
    }

//    @Test
//    public void test2(){
//        ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
//        MqttClientIn obj1 = ac.getBean("mqttClient", MqttClientImpl.class);
//        MqttClientIn obj2 = ac.getBean("mqttClient", MqttClientImpl.class);
//        System.out.println(obj1);
//        System.out.println(obj2);
//    }

    @Test
    public void test3(){
        System.out.println(Double.toString(Math.random()).substring(0,12));
        System.out.println(Double.toString(Math.random()).substring(0,12));
        System.out.println(Double.toString(Math.random()).substring(0,12));
    }

    @Test
    public void test4(){
//        IpParameters ip = new IpParameters();
//        ip.setHost("192.168.31.32");
//        ModbusMaster tcpMaster = new ModbusFactory().createTcpMaster(ip, true);
//        try{
//            tcpMaster.init();
//            //ReadHoldingRegistersResponse modbusResponse = (ReadHoldingRegistersResponse)tcpMaster.send(new ReadHoldingRegistersRequest(1, 0, 2));
//            WriteRegisterResponse modbusResponse = (WriteRegisterResponse)tcpMaster.send(new WriteRegisterRequest(2, 1, 53));
//            if(modbusResponse.isException()){
//                System.out.println(modbusResponse.getExceptionMessage());
//            }
//            else{
//                //System.out.println(Arrays.toString(modbusResponse.getShortData()));
//                System.out.println(modbusResponse.getWriteValue());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
    @Test
    public void test5(){
        Example.main(null);
    }

    @Test
    public void test6() throws Exception{
        EventLoopGroup loop = new NioEventLoopGroup();
        //MqttClient mqttClient = new MqttClientImpl(); mqttClient.creat
        MqttClient mqttClient = MqttClient.create(new MqttClientConfig(), new MqttHandler() {
            @Override
            public void onMessage(String s, ByteBuf byteBuf) {

            }
        });

        mqttClient.setEventLoop(loop);
        mqttClient.getClientConfig().setClientId("gwdxm71ywNi.N5Ly9CzuLWIyANQmQ5Jl|securemode=2,signmethod=hmacsha256,timestamp=1649002653400|");
        mqttClient.getClientConfig().setUsername("N5Ly9CzuLWIyANQmQ5Jl&gwdxm71ywNi");
        mqttClient.getClientConfig().setPassword("d6e3cc2528127203ccf832f7dfe2c19ff57f83a0d61519805d8d362f6949d731");

        mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);

        MqttConnectResult result = mqttClient.connect("iot-06z00d3qre8b6ub.mqtt.iothub.aliyuncs.com", 1883).await().get();
        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            System.out.println("error:" + result.getReturnCode());
        } else {
            System.out.println("success");
//    mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"event\"}", StandardCharsets.UTF_8));
        }

    }


}
