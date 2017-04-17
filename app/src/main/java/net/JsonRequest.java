package net;


import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JsonRequest<T> extends Request<T> {
    private final Response.Listener<T> mListener;
    private Class<T> mClass;

    public JsonRequest(int method, String url, Class<T> mClass, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mClass = mClass;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        if (networkResponse.statusCode != 200) {
            return Response.error(new VolleyError("操作失败"));
        } else {
            try {
                String jsonString = new String(networkResponse.data,
                        HttpHeaderParser.parseCharset(networkResponse.headers));
                return Response.success(JSON.parseObject(jsonString, mClass),
                        HttpHeaderParser.parseCacheHeaders(networkResponse));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            }
        }

    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public RetryPolicy getRetryPolicy() {//设置超时
        return new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {//添加header
        Map<String,String> map=new HashMap<>();
        map.put("Charset","UTF-8");
        return map;
    }
}
