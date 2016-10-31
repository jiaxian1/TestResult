package edu.feicui.testresult.testresult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttp
 * Created by jiaXian on 2016/10/17.
 */
public class TwoActivity extends Activity{
    Button mBtnRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBtnRequest=new Button(this);
        setContentView(mBtnRequest);
        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                get();
                post();
            }
        });
    }


    public void get(){
        //1.实例化OkHttpClient对象
        OkHttpClient client=new OkHttpClient.Builder()
                            .connectTimeout(3000, TimeUnit.MILLISECONDS)
                            .build();
        //2.新建一个请求
        Request request=new Request.Builder()
                        .url("http://118.244.212.82:9092/newsClient/news_sort?ver=0000000&imei=868564020877953")
                        .get()
                        .build();
        //3.加入请求
         Call call=client.newCall(request);
        //4.执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("aaa", "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body=response.body();
                String str=body.string();
//                body.byteStream();
                Log.e("aaa", "onResponse: "+str);
            }
        });

    }

    public void post(){
        //1.实例化OkHttpClient对象
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        //2.post请求
        //请求体
        FormBody body=new FormBody.Builder()
                     .add("size","10")
                     .build();
        Request request=new Request.Builder()
                       .url("http://www.wycode.cn/api/movie/getMovies")
                       .post(body)
                       .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("aaa", "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body=response.body();
                String str=body.string();
//                body.byteStream();
                Log.e("aaa", "onResponse: "+str);
            }
        });
    }
}
