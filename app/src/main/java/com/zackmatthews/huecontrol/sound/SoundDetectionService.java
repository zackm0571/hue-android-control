package com.zackmatthews.huecontrol.sound;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zackmatthews.huecontrol.managers.HueManager;
import com.zackmatthews.huecontrol.models.hue.HueLight;

public class SoundDetectionService extends Service {

    public SoundDetectionService() {
    }

    private boolean shouldContinue = true;
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(shouldContinue){
                    double db = SoundMeter.instance().getAmplitude();
                    Log.d("Debug", String.valueOf(db) + "db");
                    boolean isLit = db > HueManager.instance().dbThreshold;
                    long sleep = (isLit) ? HueManager.instance().soundTimeout : 100;
                    for(HueLight light : HueManager.instance().getLights()){
                        if(light.isEnabled()) {
                            HueManager.instance().toggleLight(SoundDetectionService.this, light.getId(), isLit);
                        }
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        shouldContinue = false;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouldContinue = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
