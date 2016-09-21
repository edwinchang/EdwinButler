package com.edwin.edwinbutler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwinchang on 2016-6-1.
 */
//程序会通过BootBroadcastReceiver被开机唤醒
//测试进程不需要前台界面也会每天自行同步，但前提是需要在前台界面进行一次设置并保存设置内容（勾选同步checkbox）
//程序会在休眠状态下被唤醒，并开始传输文件，传输过程中手机会保持休眠状态
//***注：由于Android的权限控制问题，setLastModified方法无法改变非本APP本地目录下的文件（如SD卡中或其他地方）
//因此为了APP的适用性（即对非root用户也可以使用. 2016-8-21 补充，由于本app本地目录在/data/data/目录下，但看到此目录就需要root权限，因此使用当前应用仍然需要root权限），还是使用setLastModified方法修改文件最后更新日期（方便同步功能）
//所以当前程序的文件获取的目录设定为放在APP所在安装目录下
//当然如果希望放在如SD卡等其他路径下，则不能使用setLastModified方法（无论是否取得了root用户权限）
//具体的方法是，在取得root权限后并使用shell方式，即调用
//
//除非，尝试反向更新远端的smb文件最终修改时间为当前生成的本地对应文件方式来同步
public class SmbFileManagerService extends Service {
//    private Button btn_1, btn_2, btn_3;
//    private EditText editUser,editPwd;
    private Service mContext = this;
    private EdwinUtil eU = new EdwinUtil();

    private String remoteSmbUsername="";
    private String remoteSmbPassword="";
    private String remoteSmbFolderOrFilePath=""; //注：如果为文件夹，最后必须加"/"
    private String remoteSmblocalFolderName="";

    private String remoteSmbUrl="";

    private String localFolderPath;

    //通知栏进度条
    private NotificationManager mNotificationManager=null;
    private Notification mNotification;
    private int downloadFileNumSum=0;
    private int downloadFileNumNow=0;
    private String downloadFileName="";
    //文件忽略更新、同步成功、同步失败数量
    private int downloadIgnoreNum=0;
    private int downloadSuccessNum=0;
    private int downloadFailedNum=0;

    //日志
    private String lastLog="";

    private Message msg = new Message();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 定义一个Handler，用于处理下载线程与UI间通讯
            switch (msg.what) {
                case 0x001: //传递下载进度
//                    int result = downloadFileSize * 100 / fileSize;
                    String notificationText=String.valueOf(downloadFileNumNow) +"/" +
                            String.valueOf(downloadFileNumSum)+" 文件:" +downloadFileName + "下载中...";
                    mNotification.tickerText=notificationText;
                    mNotification.contentView.setTextViewText(R.id.notify_msg,notificationText);
                    mNotification.contentView.setProgressBar(R.id.notify_progress,
                            downloadFileNumSum, downloadFileNumNow, false);
                    mNotificationManager.notify(0, mNotification);
                    eU.Log(mContext, null,notificationText,true,false,false);
                    break;
                case 0x002: //下载完成
                    String mReportPart1="";
                    String mReportPart2="";
                    mReportPart1="文件同步成功!远端文件总数:"+String.valueOf(downloadFileNumSum)+"\n";
                    mReportPart2="更新成功:"+String.valueOf(downloadSuccessNum)+
                            " ;忽略更新:"+String.valueOf(downloadIgnoreNum)+
                            " ;更新失败:"+String.valueOf(downloadFailedNum);
                    mNotification.contentView.setTextViewText(R.id.notify_msg, "EdwinButler: " + mReportPart1 + mReportPart2);

                    //更新日志并写入SharedPreferences（分别为默认的和SmbFileManagerServiceLog.XML）
                    lastLog=getNowStr()+" 总体报告:"+mReportPart1 +
                            getNowStr()+" 总体报告:"+mReportPart2 + "\n" + lastLog;
                    eU.putSharedPreferences(mContext,null,"SmbFileManagerServiceLastLog",lastLog);
                    eU.putSharedPreferences(mContext,"SmbFileManagerServiceLog",eU.getTimeDescStrFromNow("yyyyMMdd"),lastLog);

                    mNotification.tickerText="EdwinBulter: 文件同步完毕!";
                    mNotificationManager.notify(0, mNotification);

                    eU.Log(mContext, null,"EdwinBulter: 文件同步完毕!",true,false,false);
                    //任务执行完毕，把自己关闭
                    mContext.stopSelf();
                    break;
                case 0x003: //程序出现异常
                    mNotification.contentView.setTextViewText(R.id.notify_msg,"EdwinButler: 文件同步出现异常，同步失败!");
                    mNotification.tickerText="EdwinBulter: 文件同步出现异常，请检查!";
                    mNotificationManager.notify(0, mNotification);
                    eU.Log(mContext, null,"EdwinBulter: 文件同步出现异常。程序SmbFileManagerActivity异常!",true,false,false);

                    //更新日志并写入SharedPreferences（分别为默认的和SmbFileManagerServiceLog.XML）
                    lastLog=getNowStr() + "总体报告:" +
                            "文件同步出现异常。程序SmbFileManagerActivity异常!" + "\n" + lastLog;
                    eU.putSharedPreferences(mContext,null,"SmbFileManagerServiceLastLog",lastLog);
                    eU.putSharedPreferences(mContext,"SmbFileManagerServiceLog",eU.getTimeDescStrFromNow("yyyyMMdd"),lastLog);

                    //出现错误，把自己关闭
                    mContext.stopSelf();
                    break;
                case -1: //msg传递过程出现异常
                    String error = msg.getData().getString("error");
                    mNotification.contentView.setTextViewText(R.id.notify_msg,"EdwinButler: 文件同步出现系统性异常，同步失败!");
                    mNotificationManager.notify(0, mNotification);
                    eU.Log(mContext, null,"EdwinBulter: 文件msg传递出现系统性异常。程序SmbFileManagerActivity异常!error:"+error,true,false,false);

                    //更新日志并写入SharedPreferences（分别为默认的和SmbFileManagerServiceLog.XML）
                    lastLog=getNowStr() + "总体报告:" +
                            "文件同步出现异常。程序SmbFileManagerActivity异常!" + "\n" + lastLog;
                    eU.putSharedPreferences(mContext,null,"SmbFileManagerServiceLastLog",lastLog);
                    eU.putSharedPreferences(mContext,"SmbFileManagerServiceLog",eU.getTimeDescStrFromNow("yyyyMMdd"),lastLog);

                    //出现错误，把自己关闭
                    mContext.stopSelf();
                    break;
                default:
                    break;
            }
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: 2016-6-4 Smb协议共享文件管理系统的一些待完成任务
        //跳出提示是否开始同步(暂不进行)
        //APP通过接入手机的Mac地址作为校验依据之一(暂不进行)

        eU.Log(mContext, null, "EdwinButler:下载SMB共享文件到SD卡", true, true, true);

        //获取远端SMB的目标文件或文件夹地址、登录用户名、登录密码，这些信息由调用当前服务的Activity传入
        remoteSmbUsername=getExtra(intent,"remoteSmbUsername");
        remoteSmbPassword=getExtra(intent,"remoteSmbPassword");
        remoteSmbFolderOrFilePath=getExtra(intent,"remoteSmbFolderOrFilePath");
        remoteSmblocalFolderName=getExtra(intent,"remoteSmblocalFolderName");

        //如果没有从传来的intent中获得信息，就找对应的SharedPreferences的信息
        if(remoteSmbUsername==null){remoteSmbUsername=getShare(mContext,"remoteSmbUsername");}
        if(remoteSmbPassword==null){remoteSmbPassword=getShare(mContext,"remoteSmbPassword");}
        if(remoteSmbFolderOrFilePath==null){remoteSmbFolderOrFilePath=getShare(mContext,"remoteSmbFolderOrFilePath");}
        if(remoteSmblocalFolderName==null){remoteSmblocalFolderName=getShare(mContext,"remoteSmblocalFolderName");}
        //密码需要通过3次BASE64解密
        if(remoteSmbPassword!=null && remoteSmbPassword.length()>0){
            remoteSmbPassword=
                    eU.decryptBASE64(eU.decryptBASE64(eU.decryptBASE64(
                            remoteSmbPassword)));
        }

        //获取APP所在安装目录的files文件，之后再拼接
        localFolderPath = eU.getAppFilesFolderPath(mContext) + "/" + remoteSmblocalFolderName;
//        localFolderPath = Environment.getRootDirectory() + "/" + remoteSmblocalFolderName;
//        localFolderPath = "/" + remoteSmblocalFolderName;
//        localFolderPath = eU.getSDCardPath() + "/" + remoteSmblocalFolderName;

        //初始化通知栏
        notificationInit();

        //注：访问http等外网协议一定要通过后台线程进行，如在主线程中访问则报NetworkOnMainThreadException异常
        //如果希望与主线程交互，则使用handler方式进行，传递数据则通过主程序中的私有变量传递
        //这里用了detail变量，当然也可以通过handler直接传递数据
        //smb://xxx:xxx@192.168.2.1/testIndex/1.txt
        new Thread() {
            public void run() {
                try {
                    downloadFileNumSum=0;
                    downloadFileNumNow=0;
                    downloadFileName="";
                    //文件忽略更新、同步成功、同步失败数量
                    downloadIgnoreNum=0;
                    downloadSuccessNum=0;
                    downloadFailedNum=0;

                    //获取输入的用户名密码，并进行smb的URL拼接
//                                    remoteUsername=editUser.getText().toString();
//                                    remotePassword=editPwd.getText().toString();

                    //================
                    //密码中特殊字符的代码改造方法（暂不使用）
//                    private static String domainip = "10.1.44.193";
//                    private static String username = "administrator";
//                    private static String password = "1q2w3e4r5t!@#";
//                    private static String remoteurl = "smb://10.1.44.193/data";
//                    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domainip, username, password);  //先登录验证
//                    SmbFile fp = new SmbFile(remoteurl+"//"+dir,auth);
                    //================
                    remoteSmbUrl = "smb://" + remoteSmbUsername + ":" + remoteSmbPassword + "@"
                            + remoteSmbFolderOrFilePath;
                    //下载SMB共享文件到SD卡
                    //SMB目标是否为目录
                    boolean blnSmbDirectory = eU.isSmbDirectory(remoteSmbUrl);
                    String remoteSmbFileUrl = "";
                    //遍历文件夹（一层目录），并写入SD卡
                    List<String> fileNames = new ArrayList<String>();
                    fileNames = EdwinUtil.smbGetFileNamesFromSmb(remoteSmbUrl);
                    String filename = "";
                    //用来存储文件最后修改日期
                    long smbLastModified=0;
                    long localLastModified=0;
                    //用于指向生成的本地文件
                    File downloadedLocalFile=null;
                    //获取文件总数，用于传出到主界面
                    downloadFileNumSum = fileNames.size();
                    //初始化日志
                    lastLog="";
                    for (int i = 0; i < fileNames.size(); i++) {
                        //获取当前正在传输的文件号，用于传出到主界面
                        downloadFileNumNow = i + 1;
                        filename = fileNames.get(i);
                        //获取当前正在传输的文件名，用于传出到主界面
                        downloadFileName = filename;
                        //传出文件正在传输的信号到主日志
//                                handler.sendEmptyMessage(0x001);
                        sendMsg(0x001);
                        //拼接远端SMB的URL
                        //如果SMB目标是目录，则拼接遍历文件的SMB的URL，否则就是文件，则直接使用SMB目标URL即可
                        if (blnSmbDirectory) {
                            remoteSmbFileUrl = "smb://" + remoteSmbUsername + ":" + remoteSmbPassword + "@"
                                    + remoteSmbFolderOrFilePath + filename;
                        } else {
                            remoteSmbFileUrl = remoteSmbUrl;
                        }
                        //判断远端和本地的文件最后更新日期是否一致，一致则不更新
                        //获取Smb文件和本地对应文件的更新时间
                        smbLastModified=eU.getSmbFileLastModified(remoteSmbFileUrl);
                        localLastModified=eU.getFileLastModified(localFolderPath+"/"+filename);
                        //由于Android中getLastModified在毫秒会出现误差，因此去除毫秒
                        smbLastModified=smbLastModified/1000*1000;
                        localLastModified=localLastModified/1000*1000;
                        //文件一致，忽略更新写入操作
                        //忽略更新
                        if(smbLastModified==localLastModified && smbLastModified>0 && localLastModified>0){
                            downloadIgnoreNum++;
                            eU.Log(mContext, null, "文件一致，忽略更新：" + filename, true, false, true);
                            lastLog+=getNowStr() + " 忽略:" + filename + "\n";
                        }
                        else{  //远端和本地文件不一致，开始写入
                            eU.Log(mContext, null, "开始写入文件：" + filename, true, false, true);
                            //读取文件并写入SD卡
                            downloadedLocalFile=EdwinUtil.writeInputSteamToFile(
                                    localFolderPath,
                                    filename,
                                    EdwinUtil.smbGetFileToInputStream(remoteSmbFileUrl),
                                    true);
                            //根据本地文件生成情况进行进行处理
                            if(downloadedLocalFile==null){  //本地文件生成失败
                                downloadFailedNum++;
                                eU.Log(mContext, null, "文件写入失败：" + filename, true, false, true);
                                lastLog+=getNowStr() + " 失败:" + filename + "\n";
                            }
                            else{  //本地文件生成成功，修改本地文件最后更新时间为远端文件最后更新时间
                                downloadSuccessNum++;
                                //修改最后更新时间
                                if(smbLastModified>0){
                                    //***注：由于Android的权限控制非常严格，app只能访问自己目录下的文件，而不能放在SD卡中或其他地方
                                    //因此文件获取到本地的目录一定要放在APP所在安装目录下，否则setLastModified方法会失败
                                    if(downloadedLocalFile.setLastModified(smbLastModified)){
                                        eU.Log(mContext, null,"修改最终时间成功，文件："+ filename, true, false, true);
                                    }
                                    else{
                                        eU.Log(mContext, null,"修改最终时间失败，文件："+ filename, true, false, true);
                                    }
                                    //打印文件的最后修改时间
                                    eU.Log(mContext, null,"smbLastModified："
                                            +eU.getTimeDescStrFromTimeMillis(smbLastModified), true, false, true);
                                    eU.Log(mContext, null, "localLastModified："
                                            +eU.getTimeDescStrFromTimeMillis(localLastModified), true, false, true);
                                    eU.Log(mContext, null, "downloadedLocalFileModified："
                                            +eU.getTimeDescStrFromTimeMillis(downloadedLocalFile.lastModified()), true, false, true);
                                }
                                eU.Log(mContext, null, "文件写入成功：" + filename, true, false, true);
                                lastLog+=getNowStr() + " 成功:" + filename + "\n";
                            }
                        }
                    }
                    //返回成功标志给主界面
                    sendMsg(0x002);
                } catch (Exception e) {
                    //返回错误标志给主界面
                    sendMsg(0x003);
                    e.printStackTrace();
                }
            }
        }.start();
        //返回
        return super.onStartCommand(intent, flags, startId);
    }

    private void notificationInit(){
        //通知栏内显示下载进度条
        Intent intent=new Intent(this,SmbFileManagerActivity.class);//点击进度条，进入程序
        PendingIntent pIntent=PendingIntent.getActivity(this, 0, intent, 0);
        mNotificationManager=(NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mNotification=new Notification();
        mNotification.icon=R.mipmap.edwinbutler_icon;
        mNotification.flags=Notification.FLAG_AUTO_CANCEL; //点击后自动取消，如果是FLAG_ONGOING_EVENT，则为放置在"正在运行"栏目中
        mNotification.tickerText="EdwinButler: 开始同步传输文件";
        //通知栏中进度布局
        mNotification.contentView=new RemoteViews(getPackageName(),R.layout.notification_view_edwin);
        mNotification.contentIntent=pIntent;
        //  mNotificationManager.notify(0,mNotification);
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    private String getExtra(Intent intent,String name){
        return eU.getExtraFromIntent(intent,name,null);
    }

    private String getShare(Context context, String name){
        return eU.getSharedPreferences(context,null,name,null);
    }

    private String getNowStr(){
        return eU.getTimeDescStrFromNow("yyyyMMdd HH:mm:ss");
    }
//    public void down_file(String url, String path) throws IOException {
//        // 下载函数
//        String filename = url.substring(url.lastIndexOf("/") + 1);
//        // 获取文件名
//        URL myURL = new URL(url);
//        URLConnection conn = myURL.openConnection();
//        conn.connect();
//        InputStream is = conn.getInputStream();
//        this.fileSize = conn.getContentLength();// 根据响应获取文件大小
//        if (this.fileSize <= 0)
//            throw new RuntimeException("无法获知文件大小 ");
//        if (is == null)
//            throw new RuntimeException("stream is null");
//        FileOutputStream fos = new FileOutputStream(path + filename);
//        // 把数据存入路径+文件名
//        byte buf[] = new byte[1024];
//        downloadFileSize = 0;
//        sendMsg(0);
//        int numread=0;
//        while((numread = is.read(buf))!=-1){
//            fos.write(buf, 0, numread);
//            downloadFileSize += numread;
//            sendMsg(1);// 更新进度条
//        }
//        sendMsg(2);// 通知下载完成
//        try {
//            fos.close();
//            is.close();
//        } catch (Exception ex) {
//            Log.e("tag", "error: " + ex.getMessage(), ex);
//        }
//    }
}
