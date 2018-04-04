package com.example.xuqiang.myvolley.retrofitrxjava.interceptor;

import com.example.xuqiang.myvolley.constant.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：marsxq
 * 创建日期：2018/4/3
 * 描    述：配置请求头
 * ================================================
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request()
                .newBuilder()
                .addHeader("version", Constant.VERSION_CODE)
                .addHeader("versionnumber", Constant.VERSION_NUMBER)
                .addHeader("os", Constant.OS)
                .addHeader("method", Constant.PAGE);
        return chain.proceed(requestBuilder.build());
    }
}
