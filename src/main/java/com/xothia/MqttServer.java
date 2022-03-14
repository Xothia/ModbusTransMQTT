package com.xothia;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/3/14 14:51
 * @Email : huaxia889900@126.com
 * @Description : mqtt网关，用于接收和发送mqtt报文。
 */
@Component("mqttServer")
public class MqttServer {
    @Value("7890")
    private Integer port; //服务端口号

    private NioEventLoopGroup bossGroup, workerGroup;
    private ServerBootstrap bootstrap;

    private void init(){ //创建对象 并初始化管道中handler
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() { //初始化channel
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new MqttDecoder());
                        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        pipeline.addLast(null); //业务handler
                    }
                });
    }

    public void run() throws Exception{ //server启动
        try {
            init();
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync(); //一直监听关闭事件防止程序结束
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
