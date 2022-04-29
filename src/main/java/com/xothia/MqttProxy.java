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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@Component("mqttProxy")
@PropertySource(value={"classpath:Mtm.properties"})
public class MqttProxy implements InitializingBean {
    @Value("${gateway.mqttDevice.proxyPort}")
    private Integer port; //服务端口号

    private ProxyFrontendHandler proxyFrontendHandler; //

    private NioEventLoopGroup bossGroup, workerGroup;
    private ServerBootstrap bootstrap;

    protected final Log logger = LogFactory.getLog(getClass()); //日志

    @Autowired
    public MqttProxy(ProxyFrontendHandler proxyFrontendHandler) {
        this.proxyFrontendHandler = proxyFrontendHandler;
    }

    @Override
    public void afterPropertiesSet(){ //创建对象 并初始化管道中handler
        logger.info("网关初始化...");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.AUTO_READ, false) //???
                .childHandler(new ChannelInitializer<SocketChannel>() { //初始化channel
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new MqttDecoder());
                        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        pipeline.addLast(proxyFrontendHandler); //业务handler
                    }
                });
        logger.info("网关初始化完成.");
    }

    public void run(){ //server启动
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            logger.info("网关正常工作.");
            channelFuture.channel().closeFuture().sync(); //一直监听关闭事件防止程序结束
        }catch (Exception e){
            logger.error("网关异常.");
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("网关正常关闭.");
        }
    }

}
