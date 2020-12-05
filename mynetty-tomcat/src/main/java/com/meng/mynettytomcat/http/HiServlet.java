package com.meng.mynettytomcat.http;

/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public abstract class HiServlet {

    public void service(HiRequest request, HiResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public abstract void doGet(HiRequest request, HiResponse response) throws Exception;

    public abstract void doPost(HiRequest request, HiResponse response) throws Exception;
}
