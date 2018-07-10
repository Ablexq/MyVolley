package com.example.xuqiang.myvolley;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.xuqiang.myvolley.asynctask.MyAsyncTask;
import com.example.xuqiang.myvolley.constant.Constant;
import com.example.xuqiang.myvolley.asynctask.AsyncCallBack;
import com.example.xuqiang.myvolley.asynctask.AsyncTaskUtil;
import com.example.xuqiang.myvolley.okhttp.OkCallBack;
import com.example.xuqiang.myvolley.okhttp.OkHttpUtils;
import com.example.xuqiang.myvolley.retrofitrxjava.RetrofitUtil;
import com.example.xuqiang.myvolley.retrofitrxjava.entity.HttpResult;
import com.example.xuqiang.myvolley.retrofitrxjava.subscriber.ProgressSubscriber;
import com.example.xuqiang.myvolley.retrofitrxjava.subscriber.SubscriberOnNextListener;
import com.example.xuqiang.myvolley.volley.VolleyCallBack;
import com.example.xuqiang.myvolley.volley.VolleyUtil;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainActivity extends RxFragmentActivity implements View.OnClickListener {

    private MyAsyncTask myAsyncTask;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findviews();
    }

    private void findviews() {
        tv = findViewById(R.id.tv);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //请求的注销:  request.setTag(Constant.URL1);
        VolleyUtil.requestQueue.cancelAll(Constant.URL1);

        myAsyncTask.cancel(true);
    }

    //SSL延迟计算：为什么HTTPs比HTTP要慢？ https://blog.csdn.net/neng18/article/details/49684097
    //经postman请求测试发现：https（200ms左右） 请求时长比 http（100ms左右）长100ms（一倍左右）
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                asynctask();
                break;
            case R.id.btn2:
                volley();
                break;
            case R.id.btn3:
                retrofit();
                break;
            case R.id.btn4:
                //Android okHttp网络请求之Get/Post请求
                //https://www.cnblogs.com/whoislcj/p/5526431.html
                okhttp();
                break;
        }
    }

    private void okhttp() {
        String url = "https://www.daicaihang.com/api/Lend/bidtype.html";
        Map<String, String> map = new HashMap<>();
        map.put("p", "1");
        map.put("ep", "20");
        map.put("cate", "");
        map.put("appstatus", "1");
        OkHttpUtils.getInstance(this).post(url, map, new OkCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                tv.setText("Okhttp result====" + result);
            }

            @Override
            public void onReqFailed(String errorMsg) {
                tv.setText("Okhttp errorMsg====" + errorMsg);
            }
        });
    }

    private void retrofit() {
        SubscriberOnNextListener getTopMovieOnNext = (SubscriberOnNextListener<List<HttpResult.DataBean.PageListBean>>) list -> {
            String str = "" + list.get(0).getControl_data().getItems().get(0).getCustom_pic();
            tv.setText(str);
        };
        ProgressSubscriber progressSubscriber = new ProgressSubscriber(getTopMovieOnNext, MainActivity.this);
        RetrofitUtil.getInstance().getTest(progressSubscriber, "0", "1", "200");
    }

    @SuppressLint("HandlerLeak")
    private void volley() {
        //配置请求头
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("version", Constant.VERSION_CODE);
        hashMap.put("versionnumber", Constant.VERSION_NUMBER);
        hashMap.put("os", Constant.OS);
        hashMap.put("method", Constant.PAGE);
        //请求
        VolleyUtil.getInstance().get(Constant.URL1, hashMap, new VolleyCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String response) {
                tv.setText("Volley response====" + response);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFail(String msg) {
                tv.setText("Volley msg====" + msg);
            }
        });
    }

    private void asynctask() {
        //配置请求头
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("version", Constant.VERSION_CODE);
        hashMap.put("versionnumber", Constant.VERSION_NUMBER);
        hashMap.put("os", Constant.OS);
        hashMap.put("method", Constant.PAGE);
        //请求
        myAsyncTask = AsyncTaskUtil.doAsync(Constant.URL1, hashMap, new AsyncCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPostExecute(String response) {
                tv.setText("AsyncTask====" + response);
            }
        });
    }
}