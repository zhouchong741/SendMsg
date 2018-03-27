package com.zc741.sendmsg;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGE = 1;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";// 发送的广播
    private EditText mPhoneNumber;
    private int frequency = 1000;// 毫秒数
    private Timer timer;
    private RadioGroup mRadioGroup;
    private RadioButton mOneFifth;
    private RadioButton mOne;
    private RadioButton mFive;
    private Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendBtn = findViewById(R.id.send);
        Button stop = findViewById(R.id.stop);
        mPhoneNumber = findViewById(R.id.phone_number);
        mRadioGroup = findViewById(R.id.set_frequency);
        mOneFifth = findViewById(R.id.one_fifth);
        mOne = findViewById(R.id.one);
        mFive = findViewById(R.id.five);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == mOneFifth.getId()) {
                    frequency = 200;
                } else if (checkedId == mOne.getId()) {
                    frequency = 1000;
                } else if (checkedId == mFive.getId()) {
                    frequency = 5000;
                } else {
                    frequency = 1000;
                }
            }
        });

        // 检查权限
        isPermission();

        // 设置监听
        mSendBtn.setOnClickListener(this);
        stop.setOnClickListener(this);

        // 注册发送广播
        registerReceiver(sendMessageBroadcast, new IntentFilter(SENT_SMS_ACTION));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                check();
                break;
            case R.id.stop:
                if (timer != null) {
                    timer.cancel();
                    mOneFifth.setEnabled(true);
                    mOne.setEnabled(true);
                    mFive.setEnabled(true);
                    mSendBtn.setEnabled(true);
                }
                break;
        }
    }

    private void check() {
        if (mPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "目标手机号错误", Toast.LENGTH_SHORT).show();
        } else {
            // 设置 RadioButton 不可点击
            mOneFifth.setEnabled(false);
            mOne.setEnabled(false);
            mFive.setEnabled(false);
            mSendBtn.setEnabled(false);
            timer = new Timer();
            setTimerTask();
        }
    }

    private void sendMsg() {
        String sendMsg = currentTime() + " 测试 测试 测试";
        System.out.println("send msg" + sendMsg);

        SmsManager smsManager = SmsManager.getDefault();
        Intent intent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (sendMsg.length() <= 70) {
            smsManager.sendTextMessage(mPhoneNumber.getText().toString(), null, sendMsg, sentIntent, null);
        } else {
            List<String> smsDivs = smsManager.divideMessage(sendMsg);
            for (String sms : smsDivs) {
                smsManager.sendTextMessage(mPhoneNumber.getText().toString(), null, sms, sentIntent, null);
            }
        }
    }

    // 短信发送成功广播
    private BroadcastReceiver sendMessageBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    System.out.println("=============短信发送成功============");
                    break;
                default:
                    System.out.println("=============短信发送失败============");
                    break;
            }
        }
    };

    // TimerTask
    public void setTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 100, frequency);
    }

    // Handler
    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    sendMsg();
                    break;
                default:
                    break;
            }
        }
    };

    // 毫秒值
    public Long currentTime() {
        return System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        unregisterReceiver(sendMessageBroadcast);
    }

    // 权限检查
    private void isPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_MESSAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_MESSAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("已授权");
            } else {
                Toast.makeText(this, "发送短信权限获取失败，请重新获取", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
