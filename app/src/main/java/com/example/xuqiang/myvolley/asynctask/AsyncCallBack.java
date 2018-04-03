package com.example.xuqiang.myvolley.asynctask;

public abstract class AsyncCallBack {

    public void onPreExecute() {
    }

    public void onProgressUpdate(Integer... values) {

    }

    public abstract void onPostExecute(String response);
}

