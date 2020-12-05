package com.meng.mynettytomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public class HiRequest {
    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public HiRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }
    public String getUrl(){
        return req.uri();
    }
    public String getMethod() {
        return req.method().name();
    }

    /**
     * 获取当前的请求
     */
    public Map<String, List<String>> getParameters() {
        //http解码器
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        return decoder.parameters();
    }

    public String getParameter(String name) {
        Map<String, List<String>> params = getParameters();
        List<String> param = params.get(name);
        if (param == null) {
            return null;
        } else {
            return param.get(0);
        }
    }

}
