package com.edwin.edwinbutler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class BootAutoStartService extends Service {
    @Override
    public void onCreate()
    {
        Log.v("TAG", "开机自动服务已完成启动.....");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
