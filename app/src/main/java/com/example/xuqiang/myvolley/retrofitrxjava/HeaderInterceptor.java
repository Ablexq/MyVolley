package com.example.xuqiang.myvolley.retrofitrxjava;

import com.example.xuqiang.myvolley.constant.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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
