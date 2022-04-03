import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.xothia.MqttProxy;
import de.gandev.modjn.example.Example;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import nl.jk5.mqtt.*;
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
        IpParameters ip = new IpParameters();
        ip.setHost("192.168.31.32");
        ModbusMaster tcpMaster = new ModbusFactory().createTcpMaster(ip, true);
        try{
            tcpMaster.init();
            //ReadHoldingRegistersResponse modbusResponse = (ReadHoldingRegistersResponse)tcpMaster.send(new ReadHoldingRegistersRequest(1, 0, 2));
            WriteRegisterResponse modbusResponse = (WriteRegisterResponse)tcpMaster.send(new WriteRegisterRequest(2, 1, 53));
            if(modbusResponse.isException()){
                System.out.println(modbusResponse.getExceptionMessage());
            }
            else{
                //System.out.println(Arrays.toString(modbusResponse.getShortData()));
                System.out.println(modbusResponse.getWriteValue());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void test5(){
        Example.main(null);
    }

    @Test
    public void test6()throws Exception{
        //Example.main(null);
        MqttClientConfig config = new MqttClientConfig();
        config.setClientId("gwdxm71ywNi.N5Ly9CzuLWIyANQmQ5Jl|securemode=2,signmethod=hmacsha256,timestamp=1649002653400|");
        config.setUsername("N5Ly9CzuLWIyANQmQ5Jl&gwdxm71ywNi");
        config.setPassword("d6e3cc2528127203ccf832f7dfe2c19ff57f83a0d61519805d8d362f6949d731");
        MqttClientImpl mqttClient = new MqttClientImpl(config);
        Future<MqttConnectResult> connectResult = mqttClient.connect("iot-06z00d3qre8b6ub.mqtt.iothub.aliyuncs.com",1883);
        connectResult.sync();

//        connectResult.addListener(new GenericFutureListener<Future<? super MqttConnectResult>>() {
//            @Override
//            public void operationComplete(Future<? super MqttConnectResult> future) throws Exception {
//                if(future.isSuccess()){
//                    System.out.println("connect success.");
//                }
//                else{
//                    System.out.println("connect failed.");
//                }
//            }
//        });
        //connectResult.await();
        mqttClient.on("/broadcast/gwdxm71ywNi/hahah", new MqttHandler() {
            @Override
            public void onMessage(String s, ByteBuf byteBuf) {
                System.out.println("arrived.");
                System.out.println(s);
            }
        });

        while(true){
            if(connectResult.isSuccess()){
                System.out.println("success");
            }else{
                System.out.println("failed");
            }
            Thread.sleep(1000);
        }




    }


}
