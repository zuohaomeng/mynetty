package com.meng.mynetty.rpc.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author ZuoHao
 * @date 2020/12/8
 */
public class SecondHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("second handler");
        ctx.close();
    }
}
