package com.meng.mynetty.rpc.api;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
public interface IRpcService {
    /**
     * 加
     * @return
     */
    int add(int a,int b);

    /**
     * 减
     * @return
     */
    int sub(int a,int b);

    /**
     * 乘
     * @return
     */
    int mult(int a,int b);

    /**
     * 除
     * @return
     */
    int div(int a,int b);


}
