package com.xothia;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;

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
    private final static DefaultEventLoopGroup group = new DefaultEventLoopGroup();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MqttMessage){ //接受到mqtt消息时
            System.out.println("mqtt msg inbound.");
            MqttConnAckMessage ackMessage = MqttMessageBuilders.connAck().returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED).build();
            ctx.writeAndFlush(ackMessage);
            System.out.println("write complete.");
            group.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("this is a task.");
                }
            });

        }
        else {
            super.channelRead(ctx, msg); //向后传递msg
        }

    }
}
