package com.meng.mynettytomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;


/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public class HiResponse {
    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public HiResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }


    public void write(String out) {
        try {
            if (StringUtils.isEmpty(out)) {
                return;
            }
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8")));

            response.headers().set("Content-Type", "text/html;");

            ctx.writeAndFlush(response);
        } catch (UnsupportedEncodingException e) {
            ctx.flush();
            ctx.flush();
        }
    }

}
