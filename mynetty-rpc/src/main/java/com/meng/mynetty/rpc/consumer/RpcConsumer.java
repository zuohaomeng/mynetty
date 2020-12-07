package com.meng.mynetty.rpc.consumer;

import com.meng.mynetty.rpc.api.IRpcHiService;
import com.meng.mynetty.rpc.api.IRpcService;
import com.meng.mynetty.rpc.consumer.proxy.RpcProxy;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHiService rpcHiService = RpcProxy.create(IRpcHiService.class);
        System.out.println(rpcHiService.hello("梦醉"));

        IRpcService service = RpcProxy.create(IRpcService.class);

        System.out.println("8 + 2 = " + service.add(8, 2));
        System.out.println("8 - 2 = " + service.add(8, 2));
        System.out.println("8 * 2 = " + service.add(8, 2));
        System.out.println("8 / 2 = " + service.add(8, 2));

    }
}
