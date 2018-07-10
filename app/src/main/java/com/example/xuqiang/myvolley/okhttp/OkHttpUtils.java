package com.example.xuqiang.myvolley.okhttp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("deprecation")
public class OkHttpUtils {

    private static volatile OkHttpUtils instance;
    private static final String TAG = "OkHttpUtils";
    public static Handler handler = new Handler();
    private final OkHttpClient mOkHttpClient;

    private OkHttpUtils(Context context) {
        File sdcache = context.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .hostnameVerifier((hostname, session) -> true)//支持HTTPS
                .sslSocketFactory(getUnSafeSSLSocketFactory())//支持HTTPS
                .cache(new Cache(sdcache, cacheSize))//缓存
                .build();
    }

    /**
     * 双重检测锁-单例模式
     */
    public static OkHttpUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 网络请求--get请求
     */
    public void get(String url, Map<String, String> map, final OkCallBack callback, final Class cls) {
        //对url和参数做一下拼接处理
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (url.contains("?")) {
            //如果？不在最后一位
            if (sb.indexOf("?") != sb.length() - 1) {
                sb.append("&");
            }
        } else {
            sb.append("?");
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if (sb.indexOf("&") != -1) {
            sb.deleteCharAt(sb.lastIndexOf("&"));
        }

        Log.i(TAG, "get url: " + sb);
        //new 一个OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(sb.toString())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                System.out.println(TAG + "====get e====" + e);
                handler.post(() -> callback.onReqFailed("失败"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                System.out.println(TAG + "====get result====" + result);
                handler.post(() -> callback.onReqSuccess(result));
            }
        });
    }

    /**
     * 网络请求--post请求
     */
    public void post(String url, Map<String, String> map, final OkCallBack okCallBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                System.out.println(TAG + "====post e====" + e);
                handler.post(() -> okCallBack.onReqFailed("访问失败"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    System.out.println(TAG + "====post result" + result);
                    handler.post(() -> okCallBack.onReqSuccess(result));
                }
            }
        });
    }

    //不安全
    private static SSLSocketFactory getUnSafeSSLSocketFactory() {
        SSLSocketFactory socketFactory = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);
            socketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socketFactory;
    }

    //安全
    //InputStream... certificates = context.getAssets().open("xxx.cer")
    private static SSLSocketFactory getSafeSSLSocketFactory(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
