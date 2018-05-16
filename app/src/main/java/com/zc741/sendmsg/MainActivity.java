package com.zc741.sendmsg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;
import com.zc741.sendmsg.bean.PhoneNumber;
import com.zc741.sendmsg.bean.SentMessage;
import com.zc741.sendmsg.db.RealmDao;
import com.zc741.sendmsg.http.HttpUrls;
import com.zc741.sendmsg.http.HttpUtil;
import com.zc741.sendmsg.utils.ParseAssets;
import com.zc741.sendmsg.utils.RequestParam;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zc741.sendmsg.http.HttpUtil.forSentParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String SENT_SMS_ACTION = "SENT_SMS_ACTION";// 发送的广播
    private ArrayList<Integer> mMessageIdList;
    private Timer mSentTimer;
    private TextView mTipsTv;
    private String mTag;
    boolean first = true;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();
        // 获取tag
        mTag = getIntent().getStringExtra("tag");
        initView();
        // 注册发送广播
        registerReceiver(sendMessageBroadcast, new IntentFilter(SENT_SMS_ACTION));
        // 设置获取未发送短信接口频率
        mSentTimer = new Timer();
        setSentTimerTask();
        // bugly
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(getApplicationContext(), "14521b003c", false, strategy);

        mMessageIdList = new ArrayList();
    }

    private void initView() {
        Button sentMessage = findViewById(R.id.sent_message);
        mTipsTv = findViewById(R.id.tips);

        // 设置监听
        sentMessage.setOnClickListener(this);
    }

    // server
    private void getServerInfo() {
        System.out.println("=========" + currentTime() + "==========");
        String maxMessage = "1";// 每次获取一条并更新
        RequestParam param = HttpUtil.getParams();
        param.put("maxMessages", maxMessage);
        OkHttpUtils
                .get()
                .url(HttpUrls.makeUrl(HttpUrls.UNSENT, mTag))
                .params(param.get())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println("error === " + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (!jsonArray.isNull(0)) {

                                Gson gson = new Gson();
                                PhoneNumber phoneNumber = gson.fromJson(jsonArray.get(0).toString(), PhoneNumber.class);
                                // 发送短信 如果短信已经存在 则不发送
                                if (phoneNumber != null) {
                                    if (!mMessageIdList.contains(phoneNumber.getMessageId())) {
                                        sendMsg(phoneNumber);
                                        // 将 messageId 存储起来
                                        mMessageIdList.add(phoneNumber.messageId);
                                        // 保存到数据库
                                        saveToSql(phoneNumber);
                                    } else {
                                        System.out.println("短信已经存在");
                                    }
                                } else {
                                    mTipsTv.setText("暂无未发送短信");
                                }
                            } else {
                                mTipsTv.setText("暂无未发送短信");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mTipsTv.setText("当前环境不可用");
                            mSentTimer.cancel();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sent_message:
                startActivity(new Intent(this, SentMessageActivity.class));
                break;
        }
    }

    private void sendMsg(PhoneNumber phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        Intent intent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (phoneNumber.getContent().length() <= 70) {
            // 判断是否含有+
            if (phoneNumber.getIddCode().contains("+")) {
                smsManager.sendTextMessage(phoneNumber.getIddCode() + phoneNumber.getPhoneNo(), null, phoneNumber.getContent(), sentIntent, null);
            } else {
                smsManager.sendTextMessage("+" + phoneNumber.getIddCode() + phoneNumber.getPhoneNo(), null, phoneNumber.getContent(), sentIntent, null);
            }
        } else {
            List<String> smsDivs = smsManager.divideMessage(phoneNumber.getContent());
            for (String sms : smsDivs) {
                if (phoneNumber.getIddCode().contains("+")) {
                    smsManager.sendTextMessage(phoneNumber.getIddCode() + phoneNumber.getPhoneNo(), null, sms, sentIntent, null);
                } else {
                    smsManager.sendTextMessage("+" + phoneNumber.getIddCode() + phoneNumber.getPhoneNo(), null, sms, sentIntent, null);
                }
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
                    // messageId 更新状态
                    if (!mMessageIdList.isEmpty()) {
                        updateMessageIds();
                    }
                    break;
                default:
                    System.out.println("=============短信发送失败============");
                    break;
            }
        }
    };

    // 更新messageId
    private void updateMessageIds() {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, mMessageIdList.toString());
        final Request request = new Request
                .Builder()
                .post(requestBody)
                .url(HttpUrls.makeUrl(HttpUrls.SENT + "?" + forSentParams(), mTag))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error ==== " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int statusCode = jsonObject.getInt("statusCode");
                    if (statusCode == 200) {
                        first = false;
                        System.out.println("=====" + jsonObject.get("message") + "=====");
                    } else {
                        System.out.println("=====" + jsonObject.get("message") + "=====");
                    }
                    System.out.println("=========" + currentTime() + "==========");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 保存到数据库
    private void saveToSql(PhoneNumber list) {
        RealmDao realmDao = new RealmDao();
        boolean isExist = realmDao.isMessageIdExist(list.getMessageId());
        if (!isExist) {
            mRealm.beginTransaction();
            SentMessage sentMessage = mRealm.createObject(SentMessage.class);
            sentMessage.setMessageId(list.getMessageId());
            sentMessage.setIddCode(list.getIddCode());
            sentMessage.setPhoneNo(list.getPhoneNo());
            sentMessage.setContent(list.getContent());
            mRealm.commitTransaction();
            System.out.println("==========保存了==========");
            //mRealm.copyFromRealm(sentMessage);
        } else {
            System.out.println("========已经存在数据库了 不再保存========");
        }
    }

    // 设置获取未发送短信接口频率 2/1(秒/次)
    public void setSentTimerTask() {
        mSentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                sentHandler.sendMessage(message);
            }
        }, 100, 1000 * 2);
    }

    @SuppressLint("HandlerLeak")
    private Handler sentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    System.out.println("getServerInfo");
                    mTipsTv.setText("");
                    getServerInfo();
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
        if (mSentTimer != null) {
            mSentTimer.cancel();
        }
        unregisterReceiver(sendMessageBroadcast);
        mRealm.close();
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
