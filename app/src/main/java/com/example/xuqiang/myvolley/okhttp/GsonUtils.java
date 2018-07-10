package com.example.xuqiang.myvolley.okhttp;


import com.google.gson.Gson;

public class GsonUtils {
    private static volatile Gson gson;
    public static Gson getInstance() {
        if(gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
