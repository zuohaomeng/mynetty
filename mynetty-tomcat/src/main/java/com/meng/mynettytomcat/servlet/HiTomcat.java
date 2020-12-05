package com.meng.mynettytomcat.servlet;

import com.meng.mynettytomcat.http.HiServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public class HiTomcat {
    private int port = 8080;


    private Properties webxml = new Properties();

    private void init() {
        //加载web.xml,同时初始化 ServletMapping对象
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);
            for (Object o : webxml.keySet()) {
                String key = o.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    //可改为类似springboot容器进行获取
                    HiServlet obj = (HiServlet) Class.forName(className).newInstance();
                    TomcatContainer.servletMapping.put(url, obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
        init();
        //boss线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //1.创建对象
            ServerBootstrap server = new ServerBootstrap();

            //2.配置参数
            server.group(bossGroup, workerGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 无锁化串行编程
                            //Netty对HTTP协议的封装，顺序有要求
                            // HttpResponseEncoder 编码器
                            // 责任链模式，双向链表Inbound OutBound
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            socketChannel.pipeline().addLast(new HiTomcatHandler());
                        }
                    })
                    //针对主线程的配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            //启动服务
            ChannelFuture f = server.bind(port).sync();
            System.out.println("Hi Tomcat 已启动，监听的端口是：" + port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("-------关闭了");
            // 关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        new HiTomcat().start();
    }
}
