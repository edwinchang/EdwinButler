package com.edwin.edwinbutler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;

/**
 * Created by edwinchang on 2016-5-21.
 */
public class EdwinTestActivity extends AppCompatActivity {
    private Button btn_test,btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6,btn_7,btn_8,btn_9,btn_10,btn_11,btn_12;
    private AppCompatActivity mContext = this;
    private EdwinUtil eU = new EdwinUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edwin_test);

        //mContext = EdwinTestActivity.this;
        btn_test = (Button) findViewById(R.id.mybuttonTest);
        btn_0 = (Button) findViewById(R.id.mybutton00);
        btn_1 = (Button) findViewById(R.id.mybutton01);
        btn_2 = (Button) findViewById(R.id.mybutton02);
        btn_3 = (Button) findViewById(R.id.mybutton03);
        btn_4 = (Button) findViewById(R.id.mybutton04);
        btn_5 = (Button) findViewById(R.id.mybutton05);
        btn_6 = (Button) findViewById(R.id.mybutton06);
        btn_7 = (Button) findViewById(R.id.mybutton07);
        btn_8 = (Button) findViewById(R.id.mybutton08);
        btn_9 = (Button) findViewById(R.id.mybutton09);
        btn_10 = (Button) findViewById(R.id.mybutton10);
        btn_11 = (Button) findViewById(R.id.mybuttonSmb);
        btn_12 = (Button) findViewById(R.id.mybutton12);

        //用于测试
        btn_test.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eU.Log(mContext, null, "EdwinTest测试开始", true, true, true);
                        //=====================putSharedPreferences测试===============================
//                        eU.putSharedPreferences(mContext,null,"name","edwin");
//                        eU.putSharedPreferences(mContext,null,"age",30);
//                        //获取数据
//                        eU.Log(mContext, null,
//                                "作者："+eU.getSharedPreferences(mContext,null,"name","")
//                                        +"年龄："+eU.getSharedPreferences(mContext,null,"age",0),
//                                true,true, true);
//                        如果你想要删除通过SharedPreferences产生的文件，可以通过以下方法：
//                        File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","Activity.xml");
//                        if(file.exists()){file.delete();}
                        //=====================sendSimpleNotification测试===============================
//                        eU.sendSimpleNotification(mContext,"测试测试");
                        //=====================isInt测试===============================
//                        eU.Log(mContext,null,eU.isInt("123a")?"true":"false",
//                                true,true,true);
                        //=====================修改文件最后日期测试===============================
                        String dstFile="/sdcard/!EdwinTest/11.txt";
                        File f = new File(dstFile);
//                            f.setLastModified(f.lastModified()+1000*60*60*24);
                        eU.setFileLastModifiedByShell("20150909.100000",dstFile);
//                            OutputStream os=eU.shellCommandInit();
//                            eU.shellCommandDo(os,"chmod 777 " + "/sdcard/!EdwinTest/11.txt");
//                            eU.shellCommandDo(os,"touch -t 20150808.100000 /sdcard/!EdwinTest/11.txt");
//                            eU.shellCommandClose(os);
                        eU.Log(mContext,null,eU.getTimeDescStrFromTimeMillis(f.lastModified()),
                                true,false,true);
                    }
                });

        //短信管理
        btn_0.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "短信管理", true, true, true);
//                Log.i("Edwin", "显示和输出日志");
//                Toast.makeText(mContext, "显示和输出日志", Toast.LENGTH_SHORT).show();
//                System.out.println("地址aaaaaaaaa");
//                Log.i("Edwin", mContext.getPackageName());  //获取当前类的包名（路径）
//                Log.i("Edwin", mContext.getClass().getName());  //获取当前类的全名（包含包名）
//                Log.i("Edwin", mContext.getClass().getSimpleName());    //获取当前类的名称（不包含包名）

                //跳转到另一个窗体
                //SmsContactManagerActivity
                //SecondActivityDemoActivity
//                eU.JumpToAnotherActivity(mContext, SmsContactManagerActivity.class, false, 0);
                try {
                    eU.JumpToAnotherActivity(mContext,
                            Class.forName(mContext.getPackageName() + "." + "SmsContactManagerActivity"), false, 0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //startActivity(new Intent(mContext, SmsContactManagerActivity.class));
            }
        });

        //访问百度网页
        btn_1.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);
            }
        });

        //传递数据到另一个Activity
        //注：记得要在AndroidManifest.xml中声明SecondActivityDemoActivity，否则创建这个Activity时会程序出错
        btn_2.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "传递数据到另一个Activity", true, true, true);
                String strForward1 = "传输过来的信息：";
                String strForward2 = "";
                Intent it = new Intent(mContext, SecondActivityDemoActivity.class);
                strForward2 = btn_2.getText().toString();

                //新建Bundle对象,并把数据写入
                Bundle bd = new Bundle();
                bd.putCharSequence("txt1", strForward1);
                bd.putCharSequence("txt2", strForward2);

                //将数据包Bundle绑定到Intent上
                it.putExtras(bd);
                //这里采用startActivityForResult来做跳转，为了获取新活动再返回的值，如不需要接收返回的值则用startActivity即可
                startActivityForResult(it, 0x123);
                //startActivity(it);
                //关闭第一个Activity
                //finish();
            }
        });

        //为两个按钮设置点击事件,分别是启动与停止service
        //开启后台服务
        btn_3.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "开启服务（主动调用Service）", true, true, true);
//                Toast.makeText(mContext, "开启服务（主动调用Service）", Toast.LENGTH_SHORT).show();
//                Log.i("Edwin", "开启服务（主动调用Service）");
                //startService(intentService);
                //Intent intentService = new Intent(mContext,AlarmLongRunningService.class);
                //intentService.setPackage("com.edwin.edwinbutler.ServiceDemo.class");//mContext.getPackageName()。这里你需要设置你应用的包名，包名如：com.my.test

                //创建启动Service的Intent,以及Intent属性
                Intent intent = new Intent(mContext, ServiceDemo.class);
                //intent.setAction("com.edwin.edwinbutler.service.ServiceDemo");//你定义的service的action
                //startService(new Intent(getApplicationContext(), ServiceDemo.class));
                startService(intent);
                //mContext.startService(intentService);
            }
        });
        //停止后台服务
        btn_4.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "停止服务（主动调用Service）", true, true, true);
//                Toast.makeText(mContext, "停止服务（主动调用Service）", Toast.LENGTH_SHORT).show();
//                Log.i("Edwin", "停止服务（主动调用Service）");
//                intentService.setPackage(getPackageName());//这里你需要设置你应用的包名，包名如：com.my.test
//                mContext.stopService(intentService);
                Intent intent = new Intent(mContext, ServiceDemo.class);
                stopService(intent);
            }
        });

        //启动服务（通过BroadcastReceiver方式）
        btn_5.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果按钮字符串为“启动。。”则进行启动服务的广播，否则进行停止服务的广播
                if (btn_5.getText().toString().equals("启动服务（通过BroadcastReceiver方式）")) {
                    eU.Log(mContext, null, "启动服务（通过BroadcastReceiver方式）", true, true, true);
//                    Toast.makeText(mContext, "启动服务（通过BroadcastReceiver方式）", Toast.LENGTH_SHORT).show();
//                    Log.i("Edwin", "启动服务（通过BroadcastReceiver方式）");
                    btn_5.setText("停止服务（通过BroadcastReceiver方式）");
//                intentService.setPackage(getPackageName());//这里你需要设置你应用的包名，包名如：com.my.test
//                mContext.stopService(intentService);
                    //根据设定的Action进行广播，之后程序会根据intent的Action、Category等信息，自动匹配到已注册过的Reciever并调用
                    Intent intent = new Intent("com.edwin.edwinbutler.service.AlarmLongRunningService_Start");
                    sendBroadcast(intent);
                } else {
                    eU.Log(mContext, null, "停止服务（通过BroadcastReceiver方式）", true, true, true);
//                    Toast.makeText(mContext, "停止服务（通过BroadcastReceiver方式）", Toast.LENGTH_SHORT).show();
//                    Log.i("Edwin", "停止服务（通过BroadcastReceiver方式）");
                    btn_5.setText("启动服务（通过BroadcastReceiver方式）");
                    //根据设定的Action进行广播，之后程序会根据intent的Action、Category等信息，自动匹配到已注册过的Reciever并调用
                    Intent intent = new Intent("com.edwin.edwinbutler.service.AlarmLongRunningService_Stop");
                    sendBroadcast(intent);
                }
            }
        });

        //将循环播放的Alarm关闭
        btn_6.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "将循环播放的Alarm关闭", true, true, true);
//                Toast.makeText(mContext, "将循环播放的Alarm关闭", Toast.LENGTH_SHORT).show();
//                Log.i("Edwin", "将循环播放的Alarm关闭");
                //AlarmManager的取消：
                //其中需要注意的是取消的Intent必须与启动Intent保持绝对一致才能支持取消AlarmManager
                Intent intent = new Intent(mContext, AlarmLongRunningReceiver.class);
                //intent.setAction("repeating");
                PendingIntent sender1 = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarm.cancel(sender1);
            }
        });

        //底部任务栏Demo
        btn_7.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "底部导航栏Demo", true, true, true);
                try {
                    eU.JumpToAnotherActivity(mContext,
                            Class.forName(mContext.getPackageName() + "." + "BottomNaviBarDemoMainActivity"), false, 0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //文件管理
        btn_8.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "文件管理", true, true, true);
                try {
                    eU.JumpToAnotherActivity(mContext,
                            Class.forName(mContext.getPackageName() + "." + "FileManagerDemoActivity"),false, 0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_9.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "Http访问Demo", true, true, true);
                try {
                    eU.JumpToAnotherActivity(mContext,
                            Class.forName(mContext.getPackageName() + "." + "HttpRequestDemoActivity"),false, 0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_10.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "应用程序获得root权限", true, true, true);
                //获得root权限代码
                eU.rootCommand("chmod 777 "+getPackageCodePath());
            }
        });

        btn_11.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "下载SMB共享文件到SD卡", true, true, true);
                try {
                    eU.JumpToAnotherActivity(mContext,
                            Class.forName(mContext.getPackageName() + "." + "SmbFileManagerActivity"),false, 0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //通过shell控制音量(用root权限执行Linux下的Shell指令)
        //说明：
        //Runtime.getRuntime().exec("su").getOutputStream()，获取了一个具有Root权限的Process的输出流对象，向其中写入字符串即可以Root权限被Shell执行，ADB模拟按键的指令为 "input keyevent keyCode"，keyCode为按键的键值，例如KeyEvent.KEYCODE_VOLUME_UP表示音量加。
        //模拟按键时，不应每次都调用Runtime.getRuntime().exec("su")，因为每次调用这个代码的时候，都会获取Runtime实例，并且执行"su"请求Root权限，反应就会很慢（我的理解是相当于每次都新开一个命令行窗口）；而应该只是在一开始执行一次，并获取一个OutputStream实例，后来每次执行一条Shell指令，只需向其中写入相应字符串，这样就快了很多。
        btn_12.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eU.Log(mContext, null, "通过shell控制音量", true, true, true);

                        int keyCode= KeyEvent.KEYCODE_VOLUME_DOWN;
                        String cmd="input keyevent " + keyCode;

                        OutputStream os=eU.shellCommandInit();
                        eU.shellCommandDo(os,cmd);
                        eU.shellCommandClose(os);
                    }
                });

    }
    //================onCreate结束================

    //用于实例：传递数据到另一个Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x123 && resultCode == 0x234) {
            Bundle b = data.getExtras(); //data为B中回传的Intent
            String str1 = b.getString("strBackward");//strBackward即为回传的值
            btn_2.setText(str1);
            //Toast.makeText(mContext, str1, Toast.LENGTH_LONG); //传回值后Toast方法好像无法使用
        }
    }


}
