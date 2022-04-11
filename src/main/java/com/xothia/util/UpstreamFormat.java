package com.xothia.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia.util
 * @ClassName : .java
 * @createTime : 2022/4/11 14:07
 * @Email : huaxia889900@126.com
 * @Description : 属性上报的格式。
 */
public class UpstreamFormat {

    private final HashMap<String, Object> data = new HashMap<>();

    public void put(String attributeName, Object value){
        data.put(attributeName, value);
    }

    public String getJsonStr(){
        return JSON.toJSONString(data);
    }

    public byte[] getJsonBytes(){
        return JSON.toJSONBytes(data);
    }

}
