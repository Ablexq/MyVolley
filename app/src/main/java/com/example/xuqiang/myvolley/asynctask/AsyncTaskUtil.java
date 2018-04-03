package com.example.xuqiang.myvolley.asynctask;

import android.text.TextUtils;

import java.util.Map;

public class AsyncTaskUtil {

    public static MyAsyncTask doAsync(String url, Map<String, String> paramsMap, AsyncCallBack asyncCallBack) {

        if (!TextUtils.isEmpty(url) && asyncCallBack != null) {
            MyAsyncTask myTask = new MyAsyncTask(asyncCallBack, paramsMap);
            myTask.execute(url);
            return myTask;
        }
        return null;
    }
}

