package com.xothia;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/3/14 15:36
 * @Email : huaxia889900@126.com
 * @Description :
 */
public class CoreHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MqttMessage){
            System.out.println("mqtt msg inbound.");
        }
        else {
            super.channelRead(ctx, msg); //向后传递msg
        }

    }
}
