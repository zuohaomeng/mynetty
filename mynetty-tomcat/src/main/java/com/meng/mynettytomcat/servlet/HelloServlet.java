package com.meng.mynettytomcat.servlet;

import com.meng.mynettytomcat.http.HiRequest;
import com.meng.mynettytomcat.http.HiResponse;
import com.meng.mynettytomcat.http.HiServlet;

/**
 * @author ZuoHao
 * @date 2020/12/4
 */
public class HelloServlet extends HiServlet {
    @Override
    public void doGet(HiRequest request, HiResponse response) throws Exception {
        System.out.println("This is First Serlvet------get");
        this.doPost(request, response);
    }

    @Override
    public void doPost(HiRequest request, HiResponse response) throws Exception {
        response.write("This is First Serlvet-------post");
    }
}
