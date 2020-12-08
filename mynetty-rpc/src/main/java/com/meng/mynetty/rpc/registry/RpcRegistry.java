package com.meng.mynetty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
public class RpcRegistry {

    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            //自定义协议解码器
                            /** 入参有5个，分别解释如下
                             maxFrameLength：框架的最大长度。如果帧的长度大于此值，则将抛出TooLongFrameException。
                             lengthFieldOffset：长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
                             lengthFieldLength：长度字段的长度。如：长度字段是int型表示，那么这个值就是4（long型就是8）
                             lengthAdjustment：要添加到长度字段值的补偿值
                             initialBytesToStrip：从解码帧中去除的第一个字节数
                             */
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //自定义协议编码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new RegistryHandler());
                            pipeline.addLast(new SecondHandler());
                        }
                    })
                    //最大128个客户端连接
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //TCP会主动探测空闲连接的有效性，默认心跳间隔7200是，即2h.
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("netty is listen at" + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("netty is end");
        }

    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }

}
