package com.zackmatthews.huecontrol.sound;

/**
 * Created by zachmathews on 11/23/16.
 */
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class SoundMeter {

    public interface SoundPolledListener{
        public void onSoundPolled(double soundDb);
    }
    private static SoundMeter instance;
    private SoundPolledListener listener;
    private AudioRecord ar = null;
    private int minSize;
    private Thread dbPollThread;

    public static SoundMeter instance(){
        if(instance == null){
            instance = new SoundMeter();
        }
        return instance;
    }
    public void setListener(SoundPolledListener listener){
        this.listener = listener;
    }
    public void start() {
        minSize= AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
        ar.startRecording();
        dbPollThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!dbPollThread.isInterrupted()) {
                    try {
                        Thread.sleep(100);
                        getAmplitude();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        dbPollThread.start();
    }

    public void stop() {
        if (ar != null) {
            dbPollThread.interrupt();
            ar.stop();
            ar.release();
            ar = null;
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        if(listener != null){
            listener.onSoundPolled(max);
        }
        return max;
    }

}