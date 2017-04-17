package net;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//网络请求框架
public class AsyncHttpClient {
    private RequestQueue requestQueue;
    private static AsyncHttpClient instance;

    private AsyncHttpClient(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }
    public static AsyncHttpClient getInstance(Context context) {
        if (instance==null){
            synchronized (AsyncHttpClient.class){   //同步
                if (instance==null){
                    instance=new AsyncHttpClient(context);
                }
            }
        }
        return instance;
    }
    public  void addTask(Request task){
        requestQueue.add(task);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
