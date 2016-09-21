package com.edwin.edwinbutler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private final String ACTION_START = "com.edwin.edwinbutler.service.AlarmLongRunningService_Start";
    private final String ACTION_STOP = "com.edwin.edwinbutler.service.AlarmLongRunningService_Stop";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmLongRunningService.class);
        //如果Action为启动，则调用启动服务
        if (intent.getAction().equals(ACTION_START)) {
            Log.i("Edwin", "AlarmLongRunningService开始启动服务");
            context.startService(service);
        }
        //如果Action为停止，则调用停止服务
        else if(intent.getAction().equals(ACTION_STOP)) {
            Log.i("Edwin", "AlarmLongRunningService开始停止服务");
            context.stopService(service);
        }
        //否则提示错误
        else{
            Log.i("Edwin", "AlarmLongRunningService调用时错误");
        }
//        context.startActivity(intent);
    }
}