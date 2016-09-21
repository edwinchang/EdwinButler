package com.edwin.edwinbutler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context,AlarmLongRunningService.class);
        context.startService(service);
        Log.v("Edwin", "开机自动服务开始启动.....");
        //开机自动启动APP应用，参数为需要自动启动的应用的包名（暂不使用）
        //Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//        context.startActivity(intent);
    }
}