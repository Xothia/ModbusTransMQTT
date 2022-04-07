package com.xothia.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.util
 * @ClassName : .java
 * @createTime : 2022/3/15 21:22
 * @Email : huaxia889900@126.com
 * @Description : 工具类
 */
public class Util {
    public static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    public static final Log LOGGER = LogFactory.getLog(Util.class); //日志

    public static void valid(Object obj) throws RuntimeException{
        if(!VALIDATOR.validate(obj).isEmpty()){
            ConstraintViolation<Object> firstMsg = VALIDATOR.validate(obj).iterator().next();
            String errMsg = "Bean参数不合法，检查xml配置是否正确。"+firstMsg.toString();
            LOGGER.fatal(errMsg);
            throw new RuntimeException(errMsg);
        }
    }

    //解析XML文件并返回DOM对象
    public static Document load(String filename){
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File("src/main/resources/"+filename)); // 读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

}
