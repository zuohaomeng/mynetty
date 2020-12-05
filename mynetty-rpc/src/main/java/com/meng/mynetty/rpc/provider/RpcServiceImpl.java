package com.meng.mynetty.rpc.provider;

import com.meng.mynetty.rpc.api.IRpcHiService;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
public class RpcServiceImpl implements IRpcHiService {
    @Override
    public String hello(String name) {
        return "hello" + name + "!";
    }
}
