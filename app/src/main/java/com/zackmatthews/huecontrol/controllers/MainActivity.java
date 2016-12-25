package com.zackmatthews.huecontrol.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zackmatthews.huecontrol.managers.HueManager;
import com.zackmatthews.huecontrol.R;
import com.zackmatthews.huecontrol.models.hue.HueLight;
import com.zackmatthews.huecontrol.sound.SoundDetectionService;
import com.zackmatthews.huecontrol.sound.SoundMeter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, SoundMeter.SoundPolledListener, HueManager.HueApiRequestListener{

    TextView currentDb;
    List<CheckBox> lightCheckBoxes = new ArrayList<CheckBox>();
    EditText editDecibalThreshold, editTimeout;
    Button applySettings;
    Intent soundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editDecibalThreshold = (EditText)findViewById(R.id.editDbThreshold);
        editTimeout = (EditText)findViewById(R.id.editTimeout);
        currentDb = (TextView)findViewById(R.id.dbLevel);
        if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){}
        else{
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        HueManager.instance().startLightDiscovery(this, this);

        SoundMeter.instance().setListener(this);
        SoundMeter.instance().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundMeter.instance().stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                soundServiceIntent = new Intent(this, SoundDetectionService.class);
                startService(soundServiceIntent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confirmSettings) {
            if(editDecibalThreshold.getText().length() > 0) {
                HueManager.instance().dbThreshold = Double.parseDouble(editDecibalThreshold.getText().toString());
            }
            if(editTimeout.getText().length() > 0) {
                HueManager.instance().soundTimeout = Long.parseLong(editTimeout.getText().toString()) * 1000;
            }
            Toast.makeText(this, "Settings applied", Toast.LENGTH_SHORT).show();

            stopService(soundServiceIntent);
            startService(soundServiceIntent);
        }
        else if(v.getId() == R.id.manualToggle){
            String lightsOff = getString(R.string.all_lights_off);
            String lightsOn = getString(R.string.all_lights_on);

            boolean isSetToOn = ((Button)v).getText().equals(lightsOn);

            ((Button)v).setText((isSetToOn) ? lightsOff : lightsOn);

            for(int i = 1; i <= HueManager.LIGHT_COUNT; i++) {
                HueManager.instance().toggleLight(this, i, isSetToOn);
            }
            stopService(soundServiceIntent);
            Toast.makeText(this, "Stopping sound service, apply settings to restart", Toast.LENGTH_SHORT).show();
        }
    }

    protected void setupCheckBoxes(List<HueLight> lights){
        int margin = 120;
        int height = 30;
        for(HueLight light : lights){
            CheckBox checkBox = new CheckBox(this);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                            height,
                                                                            Gravity.LEFT | Gravity.CENTER_VERTICAL);
            params.bottomMargin = margin;
            margin -= height;
            String lightName = light.getName() + " - " + light.getType();
            checkBox.setText(lightName);
            checkBox.setLayoutParams(params);
            lightCheckBoxes.add(checkBox);
            ViewGroup root = (ViewGroup)findViewById(R.id.main_root_view);
            root.addView(checkBox);
        }
    }

    @Override
    public void onLightsDiscovered(List<HueLight> lights) {
        setupCheckBoxes(lights);
    }

    @Override
    public void onSoundPolled(final double soundDb) {
        currentDb.post(new Runnable() {
            @Override
            public void run() {
                currentDb.setText("db level :" + String.valueOf(soundDb) + "db");
            }
        });

    }
}
