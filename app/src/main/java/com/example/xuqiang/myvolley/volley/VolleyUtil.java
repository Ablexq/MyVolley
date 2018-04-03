package com.example.xuqiang.myvolley.volley;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.xuqiang.myvolley.MyApplication;

import java.util.Map;

/**
 * ================================================
 * 作    者：marsxq
 * 创建日期：2018/4/3
 * 描    述：
 * ================================================
 */
public class VolleyUtil {

    //连接超时时间
    private static final int REQUEST_TIMEOUT_TIME = 60 * 1000;
    //连接重连次数
    private static final int REQUEST_MAX_RETRIES = 1;
    private static VolleyUtil instance;
    public static RequestQueue requestQueue;

    private VolleyUtil() {
        requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
    }

    //单例获取工具类
    public static VolleyUtil getInstance() {
        if (instance == null) {
            synchronized (VolleyUtil.class) {
                if (instance == null) {
                    instance = new VolleyUtil();
                }
            }
        }
        return instance;
    }

    /**
     * get请求
     */
    public void get(String url, Map<String, String> map, final VolleyCallBack volleyCallBack) {
        //请求及回调
        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            if (volleyCallBack != null && !TextUtils.isEmpty(s)) {
                volleyCallBack.onSuccess(s);
            }
        }, volleyError -> {
            if (volleyCallBack != null && volleyError != null) {
                String msg = volleyError.getMessage();
                volleyCallBack.onFail(msg);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return map;
            }
        };

        //请求超时等设置
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_TIME,
                REQUEST_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        //设置tag用于取消请求
        request.setTag(url);

        requestQueue.add(request);
    }
}