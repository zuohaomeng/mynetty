package com.meng.mynetty.chat.protocol;

/**
 * @author ZuoHao
 * @date 2020/12/10
 * 自定义IM协议，Instant Messaging Protocol即时通信协议
 */
public enum IMP {
    /**
     * 系统指令
     */
    SYSTEM("SYSTEM"),
    /**
     * 登录指令
     */
    LOGIN("LOGIN"),
    /**
     * 登出指令
     */
    LOGOUT("LOGOUT"),
    /**
     * 聊天消息
     */
    CHAT("CHAT"),
    /**
     * 送鲜花
     */
    FLOWER("FLOWER"),
    ;


    private String name;

    IMP(String name) {
        this.name = name;
    }
    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGIN|CHAT)\\]");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
