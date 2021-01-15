package com.meng.mynetty.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.meng.mynetty.chat.protocol.IMDecoder;
import com.meng.mynetty.chat.protocol.IMEncoder;
import com.meng.mynetty.chat.protocol.IMMessage;
import com.meng.mynetty.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author ZuoHao
 * @date 2020/12/10
 * 主要用于自定义协议内容的逻辑处理
 */
public class MsgProcessor {
    //记录在线用户
    private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //定义一些扩展属性
    public static final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    public static final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    public static final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");
    public static final AttributeKey<String> FROM = AttributeKey.valueOf("from");

    //自定义解码器
    private IMDecoder decoder = new IMDecoder();
    //自定义编码器
    private IMEncoder encoder = new IMEncoder();

    /**
     * 获取用户昵称
     *
     * @param channel
     * @return
     */
    public String getNickName(Channel channel) {
        return channel.attr(NICK_NAME).get();
    }

    /**
     * 获取用户远程ip地址
     *
     * @param channel
     * @return
     */
    public String getAddress(Channel channel) {
        return channel.remoteAddress().toString().replaceFirst("/", "");
    }

    /**
     * 获取扩展属性
     *
     * @param channel
     * @return
     */
    public JSONObject getAttrs(Channel channel) {
        try {
            return channel.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 添加扩展属性
     */
    private void setAttrs(Channel channel, String key, Object value) {
        try {
            JSONObject json = channel.attr(ATTRS).get();
            json.put(key, value);
            channel.attr(ATTRS).set(json);
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            channel.attr(ATTRS).set(json);
        }
    }

    /**
     * 登出通知
     *
     * @param channel
     */
    public void logout(Channel channel) {
        //如果nickName为null，没有遵从聊天协议的连接，表示未非法登录
        if (getNickName(channel) == null) {
            return;
        }
        for (Channel ch : onlineUsers) {
            IMMessage request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(channel) + "离开");
            String content = encoder.encode(request);
            channel.writeAndFlush(new TextWebSocketFrame(content));
        }
        onlineUsers.remove(channel);
    }
    public void sendMsg(Channel channel,IMMessage msg){
        sendMsg(channel,encoder.encode(msg));
    }
    /**
     * 发送消息
     * @param channel
     * @param msg
     */
    public void sendMsg(Channel channel, String msg) {
        IMMessage request = decoder.decode(msg);
        if (request == null) {
            return;
        }
        String addr = getAddress(channel);
        if (request.getCmd().equals(IMP.LOGIN.getName())) {
            channel.attr(NICK_NAME).getAndSet(request.getSender());
            channel.attr(IP_ADDR).getAndSet(addr);
            channel.attr(FROM).getAndSet(request.getTerminal());

            onlineUsers.add(channel);
            for (Channel ch : onlineUsers) {
                boolean isSelf = ch == channel;
                if (!isSelf) {
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(channel) + "加入");
                } else {
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), "已与服务器建立连接！");
                }
                if ("Console".equals(channel.attr(FROM).get())) {
                    channel.writeAndFlush(request);
                    continue;
                }
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        } else if (request.getCmd().equals(IMP.FLOWER.getName())) {
            JSONObject attrs = getAttrs(channel);
            long currTime = sysTime();
            if (null != attrs) {
                long lastTime = attrs.getLongValue("lastFlowerTime");
                //60秒之内不允许重复刷鲜花
                int secends = 10;
                long sub = currTime - lastTime;
                if (sub < 1000 * secends) {
                    request.setSender("you");
                    request.setCmd(IMP.SYSTEM.getName());
                    request.setContent("您送鲜花太频繁，" + (secends - Math.round(sub / 1000)) + "秒后再试");

                    String content = encoder.encode(request);
                    channel.writeAndFlush(new TextWebSocketFrame(content));
                    return;
                }
            }
            for (Channel ch : onlineUsers) {
                if (ch == channel) {
                    request.setSender("you");
                    request.setContent("你给大家送了一波鲜花雨");
                    setAttrs(channel, "lastFlowerTime", currTime);
                } else {
                    request.setSender(getNickName(channel));
                    request.setContent(getNickName(channel) + "送来一波鲜花雨");
                }
                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    private Long sysTime() {
        return System.currentTimeMillis();
    }
}
