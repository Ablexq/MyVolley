package com.example.xuqiang.myvolley.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;


public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    private AsyncCallBack asyncCallBack;
    private Map<String, String> paramsMap;

    public MyAsyncTask(AsyncCallBack asyncCallBack, Map<String, String> paramsMap) {
        this.asyncCallBack = asyncCallBack;
        this.paramsMap = paramsMap;
    }

    @Override
    protected void onPreExecute() {
        asyncCallBack.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return HttpURLConnectionUtil.httpURLConectionGET(params[0], paramsMap);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        asyncCallBack.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        asyncCallBack.onPostExecute(result);
    }
}