package com.zc741.sendmsg;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/11 10:45.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .build();
        Realm.setDefaultConfiguration(config);

        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
    }
}
