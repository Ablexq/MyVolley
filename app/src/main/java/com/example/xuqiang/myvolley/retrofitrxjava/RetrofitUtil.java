package com.example.xuqiang.myvolley.retrofitrxjava;

import android.provider.SyncStateContract;

import com.example.xuqiang.myvolley.BuildConfig;
import com.example.xuqiang.myvolley.constant.Constant;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ================================================
 * 作    者：marsxq
 * 创建日期：2018/4/3
 * 描    述：
 * ================================================
 */
public class RetrofitUtil {

    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constant.BASEURL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient getOkHttpClient() {
        int DEFAULT_TIMEOUT = 20 * 1000;

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //DEBUG模式下 添加日志拦截器
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(interceptor);
        }

        return httpClientBuilder
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new HeaderInterceptor())
                .build();
    }


}
