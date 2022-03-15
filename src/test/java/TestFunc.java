import com.xothia.MqttServer;
import com.xothia.bean.MqttClientImpl;
import com.xothia.bean.MqttClientIn;
import com.xothia.springConfig.SpringConfig;
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
}
