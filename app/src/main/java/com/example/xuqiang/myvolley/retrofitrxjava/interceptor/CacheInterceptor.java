package com.example.xuqiang.myvolley.retrofitrxjava.interceptor;

import com.example.xuqiang.myvolley.MyApplication;
import com.example.xuqiang.myvolley.util.NetWorkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：marsxq
 * 创建日期：2018/4/3
 * 描    述：get缓存方式拦截器
 * ================================================
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
        if (!NetWorkUtils.isNetworkAvailable(MyApplication.getInstance())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        Response responseLatest;
        if (NetWorkUtils.isNetworkAvailable(MyApplication.getInstance())) {
//            int maxAge = 0;// 有网络时 设置缓存超时时间0个小时
            int maxAge = 60; //有网失效一分钟
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
//            int maxStale = 60 * 60 * 24 * 28;// 无网络时，设置超时为4周
            int maxStale = 60 * 60 * 6; // 没网失效6小时
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return responseLatest;
    }

}
