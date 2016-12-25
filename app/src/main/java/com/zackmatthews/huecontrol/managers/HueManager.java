package com.zackmatthews.huecontrol.managers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zackmatthews.huecontrol.models.hue.HueLight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachmathews on 11/23/16.
 */
public class HueManager {
    //Todo: Create settings page, make requests more dynamic <Proof of concept>
    public static final int LIGHT_COUNT = 3; //todo: discover via api request

    private static HueManager instance;
    //Todo: Have user enter IP or discover via UPNP or some other protocol
    private String BASE_API = "http://192.168.0.3/api";
    //Todo: Create user dynamically
    private String USER = "OWTcRdgFq7YJIpecsxLx2tBq7rKBMvXHdYSYKiGE";
    private String BASE_LIGHT_API = BASE_API + USER + "lights";

    private RequestQueue queue;

    private List<HueLight> lights = new ArrayList<>();
    public List<HueLight> getLights() {
        return lights;
    }

    public double dbThreshold = 1000;
    public long soundTimeout = 10000;

    public interface HueApiRequestListener{
        void onLightsDiscovered(List<HueLight> lights);
    }
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

    public void startLightDiscovery(final Context context, final HueApiRequestListener listener){
        StringRequest request = new StringRequest(Request.Method.GET, BASE_LIGHT_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response == null || response.length() == 0) return;
                List<HueLight> mLights = lights;
                mLights.clear();

                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject("lights");
                    for(int i = 0; i < json.length(); i++){
                        int id = i+1;
                        String id_key = String.valueOf(id);
                        JSONObject lightJson = json.getJSONObject(id_key);
                        HueLight light = new HueLight();
                        light.setId(i+1);
                        light.setType(lightJson.optString("type", "unknown"));
                        light.setName(lightJson.optString("name", "unknown"));
                        mLights.add(light);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lights = mLights;

                if(listener != null){
                    listener.onLightsDiscovered(lights);
                }
            }
        }, null);
        getQueue(context).add(request);
    }
    public void toggleLight(final Context context, int lightNum, boolean isLit){
        String LIGHT_NUM= String.valueOf(lightNum);
        JSONObject object = null;
        try {
            object = new JSONObject("{\"on\":" +String.valueOf(isLit) +"}");
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
