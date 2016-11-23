package com.zackmatthews.huecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SoundDetectionService extends Service {
    private SoundMeter soundMeter;
    public SoundDetectionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        soundMeter = new SoundMeter();
        soundMeter.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    double db = soundMeter.getAmplitude();
                    Log.d("Debug", String.valueOf(db) + "db");
                    if(db > 1000){
                        HueManager.instance().turnOnLight(SoundDetectionService.this, 1);
                    }
                    else{
                        HueManager.instance().turnOffLight(SoundDetectionService.this, 1);
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
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundMeter.stop();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
