package com.edwin.edwinbutler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

/**
 * Created by edwinchang on 2016-5-25.
 * 注：如果发现时间和预计的相差一些，则查看一下手机模拟器的时间是否正确
 * 因为System.currentTimeMillis()为手机模拟器的时间而非当前电脑的时间
 */
public class AlarmLongRunningService extends Service {
    private Service mContext=this;

    public static final String SERVICE_CODE="serviceCode";
    public static final String SERVICE_CODE_SMBFILEMANAGERSERVICE="S0005";

    //时钟相关信息
    private AlarmManager alarm =null;
    private Calendar triggerTimeCalendar=Calendar.getInstance();//Calendar是可以将时间转化成绝对时间毫秒数的一个类
    //设置任务的第一次启动时间（可能需手工调整）
    //注：不用设置当天的年月日，因为在Calendar.getInstance()时系统已将Calendar日期设定为了当前时间
    //即调用了setTimeInMillis(eU.getSystemNowTimeMillis())
    private int hourOfDay=0;//定时更新的小时，范围：0-23，如下午1点就设置为13
    private int minuteOfDay=0;//定时更新的分钟，范围：0-59
    private int secondOfDay=0;//定时更新的分钟，范围：0-59
    private int milliSecondOfDay=0;//定时更新的分钟，范围：0-999

    //设置任务的间隔时间（可能需手工调整）
    private final int TIME_INTERVAL_24H=24*60*60*1000;//重复定时任务的间隔时间，如每天定时执行，则使用这里设置的24小时，单位毫秒
    private int timeIntervalOther=24*60*60*1000;//重复定时任务的间隔时间，适合于非24小时间隔的任务，单位毫秒（可能需手工调整）

    private String remoteSmbSyncChecked="";
    private String remoteSmbSyncHour="";
    private String remoteSmbSyncMin="";
    private String remoteSmbSyncSec="";

    private PendingIntent sender=null;

    private EdwinUtil eU=new EdwinUtil();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            //设置通知
            eU.sendNotification(mContext,
                    "EdwinButler","EdwinButler:后台定时服务已关闭","EdwinButler:后台定时服务已关闭");
            eU.Log(mContext,null,"EdwinButler:后台定时服务已关闭",true,false,true);
            //取消alarm（暂不使用）
            if(sender!=null){
                alarm.cancel(sender); //取消alarm
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这里开辟一条线程,用来执行具体的逻辑操作:
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Log.d("Edwin", "后台定时服务已启动:" + new Date().toString());
//            }
//        }).start();
        try{
        //---
        //“每天同步的check”如果接受到null则查看ShareReference文件中的值
        remoteSmbSyncChecked=getExtra(intent,"remoteSmbSyncChecked");
        if(remoteSmbSyncChecked==null){remoteSmbSyncChecked=getShare(mContext,"remoteSmbSyncChecked");}
        //如果“每天同步”不为"true"，则表示不开启服务
        if(!remoteSmbSyncChecked.equals("true")){
            eU.sendSimpleNotification(mContext,"EdwinButler:未勾选同步，后台定时服务启动失败");
            mContext.stopSelf();
            throw new Exception("EdwinButler:未勾选同步，后台定时服务启动失败");
//            return 0;
        }
        else{
            //只有当“每天同步的check”为"true"，注：这里为String时，后续的时分秒才有意义，才需要接收
            remoteSmbSyncHour=getExtra(intent,"remoteSmbSyncHour");
            if(remoteSmbSyncHour==null){remoteSmbSyncHour=getShare(mContext,"remoteSmbSyncHour");}
            //只有当“同步小时”为数字时，才获取“同步分”和“同步秒”
            if(!eU.isInt(remoteSmbSyncHour)) { //如果“同步小时”为非数值或null，则退出服务
                eU.sendSimpleNotification(mContext, "EdwinButler:“同步小时”为非数值或null，后台定时服务启动失败");
                mContext.stopSelf();
//                return 0;
                throw new Exception("EdwinButler:“同步小时”为非数值或null，后台定时服务启动失败");
            }
            else{ //正常获取“同步分”和“同步秒”
                //写入“同步时”
                hourOfDay=Integer.parseInt(remoteSmbSyncHour);
                //判断并写入“同步分”
                remoteSmbSyncMin=getExtra(intent,"remoteSmbSyncMin");
                if(remoteSmbSyncMin==null){remoteSmbSyncMin=getShare(mContext,"remoteSmbSyncMin");}
                if(eU.isInt(remoteSmbSyncMin)){
                    minuteOfDay=Integer.parseInt(remoteSmbSyncMin);
                }
                else{
                    minuteOfDay=0;
                }
                //判断并写入“同步秒”
                remoteSmbSyncSec=getExtra(intent,"remoteSmbSyncSec");
                if(remoteSmbSyncSec==null){remoteSmbSyncSec=getShare(mContext,"remoteSmbSyncSec");}
                if(eU.isInt(remoteSmbSyncSec)){
                    secondOfDay=Integer.parseInt(remoteSmbSyncSec);
                }
                else{
                    secondOfDay=0;
                }
                //毫秒milliSecondOfDay暂不处理
                //do nothing
            }
        }

        //设置通知
        eU.sendNotification(mContext,
                "EdwinButler","EdwinButler:后台定时服务已启动","EdwinButler:后台定时服务已启动");
        eU.Log(mContext,null,"EdwinButler:后台定时服务已启动",true,false,true);
        //操作：定时单次或重复发送广播
        Intent myIntent = new Intent(this, AlarmLongRunningReceiver.class);
        eU.putServiceCodeToIntent(myIntent,SERVICE_CODE_SMBFILEMANAGERSERVICE); //表示当前广播为调用SmbFileManagerService服务
        //设置action（暂不使用）
//        intent.setAction("repeating");
//        intent2.setAction("com.edwin.edwinbutler.service.AlarmLongRunningService_Start");
        //设置PendingIntent
        sender = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //设置triggerTimeCalendar
        setTriggerTimeCalendar();
        //====================================
        //3秒之后启动，之后每隔5秒启动一次，用于测试
//        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3 * 1000, 5 * 1000, sender);
        //===================================
        //注：如果发现时间和预计的相差一些，则查看一下手机模拟器的时间是否正确 
        //因为System.currentTimeMillis()为手机模拟器的时间而非当前电脑的时间
//        eU.Log(mContext,null,
//                "SystemClock.elapsedRealtime()="+String.valueOf(SystemClock.elapsedRealtime())
//                        + "," + eU.getTimeDescStrFromTimeMillis(SystemClock.elapsedRealtime()),
//                true,false,false);
        eU.Log(mContext,null,
                "System.currentTimeMillis()="+String.valueOf(eU.getSystemNowTimeMillis())
                        + "," + eU.getTimeDescStrFromTimeMillis(eU.getSystemNowTimeMillis()),
                true,false,false);
        eU.Log(mContext,null,
                "triggerTimeCalendar.getTimeInMillis()="+String.valueOf(triggerTimeCalendar.getTimeInMillis())
                        + "," + eU.getTimeDescStrFromTimeMillis(triggerTimeCalendar.getTimeInMillis()),
                true,false,false);
        //重复调用定时器发送广播
        //测试时候使用，每隔30秒重复一次
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeCalendar.getTimeInMillis(),
//                30000,sender);
        //使用绝对时间方法RTC，从设定的第一次时间开始触发，之后每隔24小时重复触发
        //注：测试时可以修改手机的系统日期和时间即可
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeCalendar.getTimeInMillis(),
                TIME_INTERVAL_24H,sender);
        //其它绝对时间调用用法：绝对时间：System.currentTimeMillis()或相对时间：SystemClock.elapsedRealtime()
        //===========
        //使用绝对时间方法RTC（重复跨度非24小时时使用）（暂不使用）
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeCalendar.getTimeInMillis(),
//                timeIntervalOther,sender);
        //===========
        //实例：使用相对时间方法ELAPSED_REALTIME（暂不使用）
//        //5秒一个周期，不停的发送广播，即5*1000表示5秒。这里设置的是每隔5秒打印一次时间，并且为当前时间的3秒后启动定时器
//        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3 * 1000, 5 * 1000, sender);
        //===========
        //仅发送一次（暂不使用）
        //alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, sender1);
        //===========
        //取消alarm（暂不使用）
        //alarm.cancel(sender); //取消alarm
        //如果系统在onStartCommand()返回后杀死了这个服务，系统就会重新创建这个服务并且调用onStartCommand()方法。
        //但是它不会重新传递最后的Intent对象，系统会用一个null的Intent对象来调用onStartCommand()方法。
        //在这个情况下，除非有一些被发送的Intent对象在等待启动服务。这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
        return START_STICKY;  //常驻系统后台
        }
        catch(Exception e){
            e.printStackTrace();
            eU.Log(mContext,null,"SmbFileManagerService调用出错!", true,false,true);
            return 0;
        }
    }
    //设置定时任务
    private void setTriggerTimeCalendar(){
        //初始化alarm
        alarm=(AlarmManager)super.getSystemService(Context.ALARM_SERVICE);
        //设置第一次启动任务时间对应的Calendar，分别设置时分秒和毫秒
        //注：不用设置当天的年月日，因为在Calendar.getInstance()时系统已将Calendar日期设定为了当前时间
        //即调用了setTimeInMillis(System.currentTimeMillis())
        //注：如果发现时间相差一些，则查看一下电脑和手机模拟器的时间是否一致
        //因为System.currentTimeMillis()为电脑的时间，triggerTimeCalendar.getTimeInMillis()为手机模拟器的时间
        triggerTimeCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        triggerTimeCalendar.set(Calendar.MINUTE, minuteOfDay);
        triggerTimeCalendar.set(Calendar.SECOND, secondOfDay);
        triggerTimeCalendar.set(Calendar.MILLISECOND,milliSecondOfDay);

        eU.Log(mContext,null,
                "System.currentTimeMillis()="+String.valueOf(eU.getSystemNowTimeMillis())
                        + "," + eU.getTimeDescStrFromTimeMillis(eU.getSystemNowTimeMillis()),
                true,false,false);
        eU.Log(mContext,null,
                "triggerTimeCalendar.getTimeInMillis()="+String.valueOf(triggerTimeCalendar.getTimeInMillis())
                        + "," + eU.getTimeDescStrFromTimeMillis(triggerTimeCalendar.getTimeInMillis()),
                true,false,false);

        //判断当前Calendar的时间是否小于当前系统时间，如果小于则加1天
        //这样是为了避免以下情况，即如果Calendar时间小于当前系统时间，alarm的第一次调用会立即开始
        if(triggerTimeCalendar.getTimeInMillis()<=eU.getSystemNowTimeMillis()){
            triggerTimeCalendar.add(Calendar.DAY_OF_YEAR,1);
        }
        //用Calendar设定相对时间的例子
        //设定一个五秒后的时间
//        Calendar calendar=Calendar.getInstance();
//        calendar.setTimeInMillis(eU.getSystemNowTimeMillis());
//        calendar.add(Calendar.SECOND, 5);
//        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    private String getExtra(Intent intent,String name){
        return eU.getExtraFromIntent(intent,name,null);
    }

    private String getShare(Context context, String name){
        return eU.getSharedPreferences(context,null,name,null);
    }
}