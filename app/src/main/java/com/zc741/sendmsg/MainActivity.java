package com.zc741.sendmsg;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc741.sendmsg.bean.PhoneNumber;
import com.zc741.sendmsg.utils.HttpUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGE = 1;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";// 发送的广播
    private Button mPhoneNumber;
    private int frequency = 1000;// 毫秒数
    private int sendCount = 0;
    private Timer timer;
    private RadioGroup mRadioGroup;
    private RadioButton mOneFifth;
    private RadioButton mOne;
    private RadioButton mFive;
    private Button mSendBtn;
    private Button mStop;
    private List<PhoneNumber> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

//        getServerInfo();
        getAssetsInfo();

        // 检查权限
        isPermission();

        // 注册发送广播
        registerReceiver(sendMessageBroadcast, new IntentFilter(SENT_SMS_ACTION));
    }

    private void initView() {
        mSendBtn = findViewById(R.id.send);
        mStop = findViewById(R.id.stop);
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
        // 设置监听
        mPhoneNumber.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }

    // assets
    private void getAssetsInfo() {
        String stringData = ParseAssets.getJson("data.json", this);
        try {
            JSONObject jsonObject = new JSONObject(stringData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Gson gson = new Gson();
            mList = gson.fromJson(jsonArray.toString(), new TypeToken<List<PhoneNumber>>() {
            }.getType());

            // 将 messageId 存储起来
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < mList.size(); i++) {
                map.put(String.valueOf(i), mList.get(i).messageId);
            }
            System.out.println(map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // server
    private void getServerInfo() {
        String token = "1dnstfg68rgqh13ol1femh4g8cl";
        String maxMessage = "10";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(HttpUrls.makeUrl(HttpUrls.UNSENT + "?token=" + token + "&maxMessages=" + maxMessage, HttpUrls.YIMI)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(call.toString() + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //System.out.println(response.body().string());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Gson gson = new Gson();
                    mList = gson.fromJson(jsonArray.toString(), new TypeToken<List<PhoneNumber>>() {
                    }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_number:
                Toast.makeText(this, "模拟5条手机号成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.send:
                // 设置 RadioButton 不可点击
                mOneFifth.setEnabled(false);
                mOne.setEnabled(false);
                mFive.setEnabled(false);
                mSendBtn.setEnabled(false);
                timer = new Timer();
                setTimerTask();
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

    private void sendMsg() {
        SmsManager smsManager = SmsManager.getDefault();
        Intent intent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (mList.get(sendCount).getContent().length() <= 70) {
//            smsManager.sendTextMessage(String.valueOf(mList.get(sendCount).getPhoneNo()), null, currentTime() + mList.get(sendCount).getContent(), sentIntent, null);
            //sendLimit();
        } else {
            List<String> smsDivs = smsManager.divideMessage(currentTime() + mList.get(sendCount).getContent());
            for (String sms : smsDivs) {
//                smsManager.sendTextMessage(String.valueOf(mList.get(sendCount).getPhoneNo()), null, sms, sentIntent, null);
                //sendLimit();
            }
        }

        sendCount++;
        // 当相等的时候 表明已经是最后一条短信了
        if (sendCount == mList.size()) {
            sendCount = 0;
            timer.cancel();
            mOneFifth.setEnabled(true);
            mOne.setEnabled(true);
            mFive.setEnabled(true);
            mSendBtn.setEnabled(true);
            Toast.makeText(this, "短信发送完毕", Toast.LENGTH_SHORT).show();
        }
    }

//    public void sendLimit() {
//        sendCount++;
//        if (frequency < 5000) {
//            if (sendCount > 9) {
//                timer.cancel();
//                Toast.makeText(this, "发送短信次数过快,暂停下", Toast.LENGTH_SHORT).show();
//                mOneFifth.setEnabled(true);
//                mOne.setEnabled(true);
//                mFive.setEnabled(true);
//                mSendBtn.setEnabled(true);
//                sendCount = 0;
//            }
//        }
//    }

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
    @SuppressLint("HandlerLeak")
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
