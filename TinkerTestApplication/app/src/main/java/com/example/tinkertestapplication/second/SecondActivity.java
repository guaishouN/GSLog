package com.example.tinkertestapplication.second;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tinkertestapplication.R;

public class SecondActivity extends AppCompatActivity {
    private final static String TAG = "tinkerTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.crash_tv){
            //制造闪退
            Looper.prepareMainLooper();
            //Toast.makeText(this, "has fix this bug!!", Toast.LENGTH_SHORT).show();
        }
    }

}
