package com.xothia.util;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

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
    public static String genClientId(){
        return "123";
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
