import com.xothia.MqttProxy;
import com.xothia.bean.Conf;
import com.xothia.springConfig.SpringConfig;
import de.gandev.modjn.example.Example;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.Future;
import org.jetlinks.mqtt.client.*;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.charset.StandardCharsets;

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
        MqttClientConfig config = new MqttClientConfig();
        config.setClientId("gwdxm71ywNi.N5Ly9CzuLWIyANQmQ5Jl|securemode=2,signmethod=hmacsha256,timestamp=1649002653400|");
        config.setUsername("N5Ly9CzuLWIyANQmQ5Jl&gwdxm71ywNi");
        config.setPassword("d6e3cc2528127203ccf832f7dfe2c19ff57f83a0d61519805d8d362f6949d731");
        config.setProtocolVersion(MqttVersion.MQTT_3_1_1);

        MqttClient mqttClient = MqttClient.create(config, new MqttHandler() {
            @Override
            public void onMessage(String s, ByteBuf byteBuf) {

            }
        });

        Future<MqttConnectResult> connectResult = mqttClient.connect("iot-06z00d3qre8b6ub.mqtt.iothub.aliyuncs.com", 1883);


        try {
            MqttConnectResult result = connectResult.await().get();
            System.out.println(result.getReturnCode());
        }catch (Exception e){
            e.printStackTrace();
        }

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
    @Test
    public void test7()throws Exception{

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        Conf config = context.getBean("conf", Conf.class);

        EventLoopGroup loop = new NioEventLoopGroup();
        MqttClient mqttClient = MqttClient.create(new MqttClientConfig(),((topic, payload) -> {
            System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
        }));
        mqttClient.setEventLoop(loop);
        mqttClient.getClientConfig().setClientId(config.clientId);
        mqttClient.getClientConfig().setUsername(config.usesrname);
        mqttClient.getClientConfig().setPassword(config.passwd);
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
        MqttConnectResult result = mqttClient.connect(config.mqttHostUrl, config.port).await().get();
        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            System.out.println("error:" + result.getReturnCode());
            mqttClient.disconnect();
        } else {
            System.out.println("success");
//    mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"event\"}", StandardCharsets.UTF_8));
        }
    }
    @Test
    public void test8(){
        System.out.println(toString());;
    }

}
