package com.meng.mynetty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZuoHao
 * @date 2020/12/5
 */
@Data
public class InvokerProtocol implements Serializable {
    /**
     * 类名
     */
    private String className;
    /**
     * 函数名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] params;
    /**
     * 参数列表
     */
    private Object[] values;
}
