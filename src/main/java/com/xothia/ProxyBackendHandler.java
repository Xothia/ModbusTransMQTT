package com.xothia;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/3/21 0:18
 * @Email : huaxia889900@126.com
 * @Description : 用于处理网关与mqtt broker的通信。
 */
public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {
    protected final Log logger = LogFactory.getLog(getClass()); //日志
    private final static DefaultEventLoopGroup group = new DefaultEventLoopGroup();

    private final Channel inboundChannel;

    public ProxyBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MqttMessage){

            logger.info("mqtt msg from broker inbound.");
            group.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(msg.getClass());

                    inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                //inboundChannel.close();
                                logger.info("broker msg write to local client success.");
                                inboundChannel.read();
                            } else {
                                //future.channel().close();
                                logger.warn("broker msg write to local client failed.");
                                inboundChannel.close();
                            }
                        }
                    });
                }
            });

//            inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    if (future.isSuccess()) {
//                        //inboundChannel.close();
//                        logger.info("broker msg write to local client success.");
//                        inboundChannel.read();
//                    } else {
//                        //future.channel().close();
//                        logger.warn("broker msg write to local client failed.");
//                        inboundChannel.close();
//                    }
//                }
//            });

        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("backend channel to broker is active.");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("backend channel to broker is inactive.");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("backend handler exception caught!");
        cause.printStackTrace();
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        super.exceptionCaught(ctx, cause);
    }
}
