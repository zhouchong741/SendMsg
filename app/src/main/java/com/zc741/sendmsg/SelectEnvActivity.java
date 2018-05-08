package com.zc741.sendmsg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zc741.sendmsg.http.HttpUrls;

public class SelectEnvActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mTestBtn;
    private Button mServerBtn;
    private String TAG_HOST = HttpUrls.IMMI_TEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_env);

        mTestBtn = findViewById(R.id.test_btn);
        mServerBtn = findViewById(R.id.server_btn);

        mTestBtn.setOnClickListener(this);
        mServerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.test_btn:
                System.out.println("=====测试环境======");
                TAG_HOST = HttpUrls.IMMI_TEST;
                startActivity(new Intent(this, MainActivity.class).putExtra("tag", TAG_HOST));
                break;
            case R.id.server_btn:
                System.out.println("=====生成环境======");
                TAG_HOST = HttpUrls.IMMI_SERVER;
                startActivity(new Intent(this, MainActivity.class).putExtra("tag", TAG_HOST));
                break;
        }
    }
}
