import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.xothia.MqttServer;
import com.xothia.bean.MqttClientImpl;
import com.xothia.bean.MqttClientIn;
import com.xothia.springConfig.SpringConfig;
import de.gandev.modjn.example.Example;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
        MqttServer server = context.getBean("mqttServer", MqttServer.class);
    }

    @Test
    public void test2(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
        MqttClientIn obj1 = ac.getBean("mqttClient", MqttClientImpl.class);
        MqttClientIn obj2 = ac.getBean("mqttClient", MqttClientImpl.class);
        System.out.println(obj1);
        System.out.println(obj2);
    }

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


}
