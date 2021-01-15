package com.meng.mynetty.chat.client;

import com.meng.mynetty.chat.protocol.IMMessage;
import com.meng.mynetty.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZuoHao
 * @date 2020/12/10
 */
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<IMMessage> {
    ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1);
    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), this.nickName);
        sendMsg(message);
        log.info("成功连接服务器,已执行登录动作");
        session();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage m) throws Exception {
        System.out.println((null == m.getSender() ? "" : (m.getSender() + ":")) + removeHtmlTag(m.getContent()));
    }

    public static String removeHtmlTag(String htmlStr) {
        //定义script的正则表达式
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        //过滤script标签
        htmlStr = m_script.replaceAll("");

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        //过滤style标签
        htmlStr = m_style.replaceAll("");

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        //过滤html标签
        htmlStr = m_html.replaceAll("");
        //返回文本字符串
        return htmlStr.trim();
    }

    /**
     * 启动客户端控制台
     */
    private void session() {
        threadPool.execute(this::run);
    }

    public void run() {
        System.out.println(nickName + ",你好，请在控制台输入对话内容");
        IMMessage message = null;
        Scanner scanner = new Scanner(System.in);
        do {
            if (scanner.hasNext()) {
                String input = scanner.nextLine();
                if ("exit".equals(input)) {
                    message = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), nickName);
                } else {
                    message = new IMMessage(IMP.CHAT.getName(), System.currentTimeMillis(), nickName, input);
                }
            }
        } while (sendMsg(message));
        scanner.close();
    }


    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    private boolean sendMsg(IMMessage msg) {
        ctx.channel().writeAndFlush(msg);
        System.out.println("继续输入开始对话...");
        return !IMP.LOGOUT.getName().equalsIgnoreCase(msg.getCmd());
    }
}
