package com.zc741.sendmsg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zc741.sendmsg.http.HttpUrls;

public class SelectEnvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGE = 1;
    private static final int REQUEST_CODE = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_env);

        Button testBtn = findViewById(R.id.test_btn);
        Button serverBtn = findViewById(R.id.server_btn);

        testBtn.setOnClickListener(this);
        serverBtn.setOnClickListener(this);

//        isPermission();
        requestMultiplePermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_btn:
                System.out.println("=====测试环境======");
                String TAG_HOST = HttpUrls.IMMI_TEST;
                startActivity(new Intent(this, MainActivity.class).putExtra("tag", TAG_HOST));
                break;
            case R.id.server_btn:
                System.out.println("=====生产环境======");
                TAG_HOST = HttpUrls.IMMI_SERVER;
                startActivity(new Intent(this, MainActivity.class).putExtra("tag", TAG_HOST));
                break;
        }
    }

    // 权限检查
    private void isPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_MESSAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE
        };
        requestPermissions(permissions,REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_MESSAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                System.out.println("已授权");
//            } else {
//                Toast.makeText(this, "发送短信权限获取失败，请重新获取", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                System.out.println("已授权");
            } else {
                Toast.makeText(this, "权限获取失败，请重新获取", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
