package com.zackmatthews.huecontrol;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zachmathews on 11/23/16.
 */
public class HueManager {
    //Todo: Create settings page, make requests more dynamic <Proof of concept>
    private String BASE_API = "http://192.168.1.151/api";
    private String USER = "8LKcg4w717ugIGKYQKyWOkAX9bS0L254d-DPE1Ik";
    private RequestQueue queue;
    private static HueManager instance;
    public double dbThreshold = 1000;
    public long soundTimeout = 10000;
    public static HueManager instance(){
        if(instance == null) {
            instance = new HueManager();
        }

        return instance;
    }
    private RequestQueue getQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
            queue.start();
        }
        return queue;
    }

    public void turnOffLight(final Context context, int lightNum){
        String LIGHT_NUM= String.valueOf(lightNum);
        JSONObject object = null;
        try {
            object = new JSONObject("{\"on\":false}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(JsonObjectRequest.Method.PUT,
                BASE_API + "/" + USER + "/lights/" + LIGHT_NUM + "/state", object, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        getQueue(context).add(request);
    }

    public void turnOnLight(final Context context, int lightNum){
        String LIGHT_NUM= String.valueOf(lightNum);
        JSONObject object = null;
        try {
            object = new JSONObject("{\"on\":true}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(JsonObjectRequest.Method.PUT,
                BASE_API + "/" + USER + "/lights/" + LIGHT_NUM + "/state", object, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        getQueue(context).add(request);
    }
}
