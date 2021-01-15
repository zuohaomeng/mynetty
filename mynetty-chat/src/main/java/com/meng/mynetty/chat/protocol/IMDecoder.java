package com.meng.mynetty.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.StringUtils;
import org.msgpack.MessagePack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义IM协议编码器
 *
 * @author ZuoHao
 * @date 2020/12/10
 */
public class IMDecoder extends ByteToMessageDecoder {
    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array,in.readerIndex(),length);

            //如果为空或者命令不匹配，则不处理
            if(StringUtils.isBlank(content) || !IMP.isIMP(content)){
                ctx.channel().pipeline().remove(this);
                return;
            }

            in.getBytes(in.readerIndex(),array,0,length);
            out.add(new MessagePack().read(array,IMMessage.class));
            in.clear();
        }catch (Exception e){
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 字符串解析成自定义即时通信协议
     */
    public IMMessage decode(String msg){
        if(StringUtils.isBlank(msg)){
            return null;
        }
        try {
            Matcher m = pattern.matcher(msg);

            String header = "";
            String content = "";
            if(m.matches()){
                header = m.group(1);
                content = m.group(3);
            }
            String[] heards = header.split("\\]\\[");
            long time = 0;
            try {
                time = Long.parseLong(heards[1]);
            }catch (Exception e){
                e.printStackTrace();
            }
            String nickName = heards[2];
            //昵称最多十个字
            nickName = nickName.length() < 10 ? nickName : nickName.substring(0,9);

            if(msg.startsWith("[" + IMP.LOGIN.getName() + "]")){
                return new IMMessage(heards[0],heards[3],time,nickName);
            }else if(msg.startsWith("[" + IMP.CHAT.getName() + "]")){
                return new IMMessage(heards[0],time,nickName,content);
            }else if(msg.startsWith("[" + IMP.FLOWER.getName() + "]")){
                return new IMMessage(heards[0],heards[3],time,nickName);
            }else{
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
