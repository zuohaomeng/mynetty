package com.meng.mynetty.rpc.provider;

import com.meng.mynetty.rpc.api.IRpcService;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
public class RpcHiServiceImpl implements IRpcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
