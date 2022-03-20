package com.xothia;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
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
 * @createTime : 2022/3/14 15:36
 * @Email : huaxia889900@126.com
 * @Description :
 */
//@Component
//@PropertySource(value={"classpath:Mtm.properties"})
public class CoreHandler extends ChannelInboundHandlerAdapter {
    protected final Log logger = LogFactory.getLog(getClass()); //日志
    private final static DefaultEventLoopGroup group = new DefaultEventLoopGroup();

    //目标远程broker url
    //@Value("${gateway.remote.url}")
    private String remoteHost="iot-06z00d3qre8b6ub.mqtt.iothub.aliyuncs.com";

    //目标远程broker port
   // @Value("${gateway.remote.port}")
    private int remotePort=1883;

    //网关与broker之间的通道（从代理服务器出去所以是outbound过境）
    private volatile Channel outboundChannel;

    /**
     * 当mqtt客户端与网关建立tcp连接时执行
     * @param ctx 依赖注入
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();

        // 创建一个tcp client转发报文
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new MqttDecoder(),
                                MqttEncoder.INSTANCE,
                                new ProxyBackendHandler(inboundChannel)
                        );
                    }
                });


        /**
         * 连接目标服务器
         * ChannelFuture
         * Netty中的IO操作是异步的，
         * 包括bind、write、connect等操作会简单的返回一个ChannelFuture，调用者并不能立刻获得结果。
         * 当future对象刚刚创建时，处于非完成状态。可以通过isDone()方法来判断当前操作是否完成。通过isSuccess()判断已完成的当前操作是否成功，getCause()来获取已完成的当前操作失败的原因，isCancelled()来判断已完成的当前操作是否被取消。
         * 调用者可以通过返回的ChannelFuture来获取操作执行的状态，注册监听函数来执行完成后的操作。
         */
        ChannelFuture f = b.connect(remoteHost, remotePort);
        /**
         * 获得代理服务器和目标服务器之间的连接通道
         */
        outboundChannel = f.channel();

        /**
         * ChannelFutureListener
         * 监听ChannelFuture的状态
         * 注册监听函数来执行完成后的操作
         */
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            }
        });

        //继续通知后续handler
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MqttMessage){ //接受到mqtt消息时
            logger.info("proxy: mqtt msg inbound.");
            group.submit(new Runnable() {
                @Override
                public void run() {
                    outboundChannel.writeAndFlush((MqttMessage)msg).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                // was able to flush out data, start to read the next chunk
                                logger.info("client msg->proxy->broker is complete.");
                                ctx.channel().read();
                            } else {
                                logger.info("client msg->proxy->broker is bad.");
                                future.channel().close();
                            }
                        }
                    });
                }
            });
//            while (!outboundChannel.isActive()){
//
//            }
            //logger.info("proxy: mqtt msg inbound and outbound channel is ready.");
//            outboundChannel.writeAndFlush((MqttMessage)msg).addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    if (future.isSuccess()) {
//                        // was able to flush out data, start to read the next chunk
//                        logger.info("client msg->proxy->broker is complete.");
//                        ctx.channel().read();
//                    } else {
//                        logger.info("client msg->proxy->broker is bad.");
//                        //future.channel().close();
//                    }
//                }
//            });

//            System.out.println("mqtt msg inbound.");
//            MqttConnAckMessage ackMessage = MqttMessageBuilders.connAck().returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED).build();
//            ctx.writeAndFlush(ackMessage);
//            System.out.println("write complete.");
//            group.submit(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("this is a task.");
//                }
//            });

        }
        else {
            super.channelRead(ctx, msg); //向后传递msg
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            if (outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("backend handler exception caught!");
        cause.printStackTrace();
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
}
