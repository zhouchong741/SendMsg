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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button send = (Button) findViewById(R.id.send);
        Button stop = (Button) findViewById(R.id.stop);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);

        mRadioGroup = (RadioGroup) findViewById(R.id.set_frequency);
        mOneFifth = (RadioButton) findViewById(R.id.one_fifth);
        mOne = (RadioButton) findViewById(R.id.one);
        mFive = (RadioButton) findViewById(R.id.five);
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

        isPermission();

        send.setOnClickListener(this);
        stop.setOnClickListener(this);

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

            timer = new Timer();
            setTimerTask();
        }
    }


    private void sendMsg() {
        String sendMsg = message() + " 测试 测试 测试";
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

    public Long message() {
        Long time = System.currentTimeMillis();
        return time;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer == null) {
            System.out.println("do nothing");
        } else {
            timer.cancel();
        }

        unregisterReceiver(sendMessageBroadcast);
    }

    private void isPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_MESSAGE);
        } else {
            System.out.println("do nothing");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_MESSAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "发送短信权限获取失败，请重新获取", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
