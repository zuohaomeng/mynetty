package com.meng.mynettytomcat.servlet;

import com.meng.mynettytomcat.http.HiRequest;
import com.meng.mynettytomcat.http.HiResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public class HiTomcatHandler extends ChannelInboundHandlerAdapter {


    /**
     * 每次读
     *
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            System.out.println("hello");
            HttpRequest req = (HttpRequest) msg;
            //转换为自己的实现
            HiRequest request = new HiRequest(ctx, req);
            HiResponse response = new HiResponse(ctx, req);
            String url = request.getUrl();



            if (TomcatContainer.servletMapping.containsKey(url)) {
                TomcatContainer.servletMapping.get(url).service(request, response);
            } else {
                response.write("404 - Not Found");
            }



        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
