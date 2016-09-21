package com.edwin.edwinbutler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class ServiceDemo extends Service {
    private final String TAG = "Edwin";
    //Service被创建时调用
    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate方法被调用!");
        super.onCreate();
    }

    //必须要实现的方法
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind方法被调用!");
        return null;
    }
    //Service被启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand方法被调用!");
        return super.onStartCommand(intent, flags, startId);
    }
    //Service被关闭之前回调
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestory方法被调用!");
        super.onDestroy();
    }
}
