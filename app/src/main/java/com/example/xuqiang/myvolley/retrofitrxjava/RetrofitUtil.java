package com.example.xuqiang.myvolley.retrofitrxjava;


import android.support.annotation.NonNull;

import com.example.xuqiang.myvolley.BuildConfig;
import com.example.xuqiang.myvolley.MyApplication;
import com.example.xuqiang.myvolley.constant.Constant;
import com.example.xuqiang.myvolley.retrofitrxjava.api.TestApi;
import com.example.xuqiang.myvolley.retrofitrxjava.entity.HttpResult;
import com.example.xuqiang.myvolley.retrofitrxjava.interceptor.CacheInterceptor;
import com.example.xuqiang.myvolley.retrofitrxjava.interceptor.HeaderInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ================================================
 * 作    者：marsxq
 * 创建日期：2018/4/3
 * 描    述：
 * ================================================
 */
public class RetrofitUtil {

    private Retrofit retrofit;

    //创建单例
    private static class SingletonHolder {
        private static final RetrofitUtil INSTANCE = new RetrofitUtil();
    }

    //获取单例
    public static RetrofitUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitUtil() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASEURL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getOkHttpClient() {
        int DEFAULT_TIMEOUT = 20 * 1000;

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        //DEBUG模式下 添加日志拦截器
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(interceptor);
        }

        return httpClientBuilder
                //超时设置
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                //请求头设置
                .addInterceptor(new HeaderInterceptor())
                //缓存设置
                .addNetworkInterceptor(new CacheInterceptor())//缓存机制
                .cache(getCache())//缓存位置、大小
                //连接重试
                .retryOnConnectionFailure(true)
                .build();
    }

    //网络请求带缓存
    //注意的是：okhttp只会对get请求进行缓存，post请求是不会进行缓存的。
    @NonNull
    private static Cache getCache() {
        File mFile = new File(MyApplication.getInstance().getCacheDir() + "mycache");//储存目录
        long maxSize = 10 * 1024 * 1024; // 10 MB 最大缓存数
        return new Cache(mFile, maxSize); //缓存目录和大小
    }


    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    //--------------------------------------------------------------------------------------------------------------

    public void getTest(Subscriber subscriber, String id, String pages, String maxperpage) {
        Observable observable = retrofit.create(TestApi.class).getTest(id, pages, maxperpage)
                .map(httpResult -> httpResult.getData().getPage_list());

        toSubscribe(observable, subscriber);
    }
}
