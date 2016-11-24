package com.zackmatthews.huecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SoundDetectionService extends Service {

    public SoundDetectionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoundMeter.instance().start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    double db = SoundMeter.instance().getAmplitude();
                    Log.d("Debug", String.valueOf(db) + "db");
                    if(db > HueManager.instance().dbThreshold){
                        HueManager.instance().turnOnLight(SoundDetectionService.this, 1);
                        HueManager.instance().turnOnLight(SoundDetectionService.this, 2);
                        HueManager.instance().turnOnLight(SoundDetectionService.this, 3);
                        try {
                            Thread.sleep(HueManager.instance().soundTimeout);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        HueManager.instance().turnOffLight(SoundDetectionService.this, 1);
                        HueManager.instance().turnOffLight(SoundDetectionService.this, 2);
                        HueManager.instance().turnOffLight(SoundDetectionService.this, 3);

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
        SoundMeter.instance().stop();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
