package com.edwin.edwinbutler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class AlarmLongRunningReceiver extends BroadcastReceiver {
    private EdwinUtil eU=new EdwinUtil();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle= intent.getExtras();
        try {
            eU.Log(context,null,
                    "AlarmLongRunningService信息已收到，准备进行处理",true,false,true);

            if (!bundle.isEmpty() && eU.getExtraFromIntent(intent,
                    AlarmLongRunningService.SERVICE_CODE).equals(
                    AlarmLongRunningService.SERVICE_CODE_SMBFILEMANAGERSERVICE)){
                eU.Log(context,null,
                        "AlarmLongRunningService信息已收到，开始调用SmbFileManagerService",true,false,true);
                //创建启动Service的Intent,以及Intent属性
                Intent serviceIntent = new Intent(context, SmbFileManagerService.class);
                //startService(new Intent(getApplicationContext(), ServiceDemo.class));
                context.startService(serviceIntent);
            }
            //取消广播接收，注：此处不能写此句，否则报错，因为receiver自己不能注销自己，并且显示调用的receiver调用后会自动注销
            //因此不需要手动注销，除非这个receiver是通过registerReceiver注册的
//            context.unregisterReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
            // 取消广播接收，注：此处不能写此句，否则报错，因为receiver自己不能注销自己，并且显示调用的receiver调用后会自动注销
            //因此不需要手动注销，除非这个receiver是通过registerReceiver注册的
//            context.unregisterReceiver(this);
        }
    }
}
