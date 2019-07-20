package com.example.tinkertestapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tinkertestapplication.second.SecondActivity;
import com.example.tinkertestapplication.second.ThirdActivity;
import com.example.tinkertestapplication.utils.FixDexUtils;

public class MainActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int REQUEST_PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    public void startCrashActivy(View view) {
        // 注意不能：startActivity(new Intent(MainActivity.this, SecondActivity.class));
        // 因为这样会使在处理MainActivity时，唯一的SecondActivity.class已加载到方法区了
        // 所以热修复前不能加载SecondActivity.class，而是使用ThirdActivity.class
        // 这也是这种方法要冷启动修复的原因
        startActivity(new Intent(MainActivity.this, ThirdActivity.class));
    }


    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    private void doFix(){
        FixDexUtils.startFix(this);
        Toast.makeText(this, "try fix", Toast.LENGTH_SHORT).show();
    }

    public void fixBug(View view) {
        if (checkPermission()){
            doFix();
        }
    }
}
