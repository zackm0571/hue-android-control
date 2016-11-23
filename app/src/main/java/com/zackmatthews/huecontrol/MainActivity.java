package com.zackmatthews.huecontrol;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;


import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity implements View.OnClickListener{

    TextView currentDb;
    CheckBox light1, light2, light3;
    EditText editDecibalThreshold, editTimeout;
    Button applySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editDecibalThreshold = (EditText)findViewById(R.id.editDbThreshold);
        editTimeout = (EditText)findViewById(R.id.editTimeout);
        if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){

        }
        else{
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, SoundDetectionService.class);
                startService(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        HueManager.instance().dbThreshold = Double.parseDouble(editDecibalThreshold.getText().toString());
        HueManager.instance().soundTimeout = Long.parseLong(editTimeout.getText().toString()) * 1000;
        Toast.makeText(this, "Settings applied", Toast.LENGTH_SHORT).show();
    }
}
