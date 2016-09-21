package com.edwin.edwinbutler;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class EdwinUtil {
    private final static String SERVICE_CODE="serviceCode";
    private final static int EDWIN_NORMAL_NOTIFICATION_ID=901;
    private final static int EDWIN_SIMPLE_NOTIFICATION_ID=903;

    private final static String EDWIN_SHAREDPREFERENCES_XML_FILENAME="EdwinButler_Setting";
    //===================================一些有用的代码===================================
//    ======获取包和类的名称======
//    mContext = EdwinTestActivity.this;
//    Log.i("Edwin", mContext.getPackageName());  //获取当前类的包名（路径）
//    Log.i("Edwin", mContext.getClass().getName());  //获取当前类的全名（包含包名）
//    Log.i("Edwin", mContext.getClass().getSimpleName());    //获取当前类的名称（不包含包名）
//    ======通过类全名获得类或类的实例======
//    Class.forName(mContext.getPackageName() + "." + "Abc")
//    注：Class.forName("Abc")：返回的是一个类（即类的类型，并且java中类的类型也是一个对象），用了java的反射机制，
//          即不需要得到实际对象就可以知道类的相关属性；
//    注：Class.forName("Abc").newInstance()：返回的是一个类的对象。


    //===================================输出日志===================================
    public void Log(Context context, String tag, String msg,
                    boolean isLog_i, boolean isToast, boolean isSystem) {
        //设置tag的默认值，用于Log
        if (tag == null) tag = "Edwin";
        if (isLog_i) Log.i(tag, msg);
        if (isToast) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        if (isSystem) System.out.println(msg);
    }

    //===================================类型判断相关===================================
    //判断是否为整型，输入参数为null返回false
    public static boolean isInt(String str){
        try{
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //判断是否为英文字母，输入参数为null返回false
    public static boolean isEngLetter(String str){
        try{
            Pattern pattern = Pattern.compile("[a-zA-Z]*");
            return pattern.matcher(str).matches();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //判断是否为中文汉字，输入参数为null返回false
    public static boolean isChsLetter(String str){
        try{
            Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]*");
            return pattern.matcher(str).matches();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //=====================================Activity相关==============================
    //跳转到另一个活动中
    public void JumpToAnotherActivity(AppCompatActivity context, Class<?> cls, boolean isForResult, int requestCode) {
        //bForResult：TURE表示原活动需要获取新活动返回的值，并需要传入requestCode，如不需要接收返回的值则用FALSE
        if (isForResult) {
            context.startActivityForResult(new Intent(context, cls), requestCode);
        } else {
            context.startActivity(new Intent(context, cls));
        }
    }

    //===================================通知Notification相关=============================
    //发送通知
    public Notification sendNotification(Context context,String contentTitleString,String contentText,
                                 String ticker){
//        NotificationManager mNotificationManager=null;
//        mNotificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotification=new Notification();
//        mNotification.icon=R.mipmap.alertdialog_demo;
//        mNotification.flags=Notification.FLAG_AUTO_CANCEL; //点击后自动取消，如果是FLAG_ONGOING_EVENT，则为放置在"正在运行"栏目中
//        mNotification.tickerText=text;
//        //======
//        Intent mIntent = new Intent(context, NotificationDemoActivity_1.class);
//        PendingIntent pit = PendingIntent.getActivity(context, 0, new Intent(), 0);

        //设置图片,通知标题,发送时间,提示方式等属性
        NotificationManager mNotification;
        Notification mNotify;
        mNotification = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(contentTitleString)                        //标题
                .setContentText(contentText)      //内容
                .setTicker(ticker)             //收到信息后状态栏显示的文字信息
                .setWhen(System.currentTimeMillis())           //设置通知时间
                .setSmallIcon(R.mipmap.edwinbutler_icon)            //设置小图标
                .setLargeIcon((Bitmap)null)                     //设置大图标
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(context,0,new Intent(),0))//设置PendingIntent，一定要设置，否则点击不消失
                .setAutoCancel(true);                           //设置点击后取消Notification
//                .setSubText("——我是subText")                    //内容下面的一小段文字
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
//                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
//                .setContentIntent(pit);//设置PendingIntent
        mNotify = mBuilder.build();
        //发出通知
        mNotification.notify(EDWIN_NORMAL_NOTIFICATION_ID, mNotify);
        return mNotify;
    }
    //发送通知，不包含通知内容，只有tickertext，之后自动消失
    public void sendSimpleNotification(Context context,String ticker){
        //设置图片,通知标题,发送时间,提示方式等属性
        NotificationManager mNotification;
        Notification mNotify;
        mNotification = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setTicker(ticker)             //收到信息后状态栏显示的文字信息
                .setWhen(System.currentTimeMillis())           //设置通知时间
                .setSmallIcon(R.mipmap.edwinbutler_icon)            //设置小图标
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(false);                           //设置点击后取消Notification
//                .setSubText("——我是subText")                    //内容下面的一小段文字
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
//                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
//                .setContentIntent(pit);//设置PendingIntent
        mNotify = mBuilder.build();
        //发出通知
        mNotification.notify(EDWIN_SIMPLE_NOTIFICATION_ID, mNotify);
        mNotification.cancel(EDWIN_SIMPLE_NOTIFICATION_ID);
    }

    //===============================Intent管理相关==================================
    //将特定的serviceCode写到Intent中并传到BroadcastReceiver中来判断调用哪个service
    public void putServiceCodeToIntent(Intent intent,String serviceCode){
        intent.putExtra(SERVICE_CODE, serviceCode);
    }
    //将值写入Intent的Extra中
    public void putExtraToIntent(Intent intent,String name,String Value){
        intent.putExtra(name, Value);
    }
    //---
    //获得Intent的Extra中的值
    public String getExtraFromIntent(Intent intent,String name){
        try{
            if(intent.getExtras().isEmpty()){return null;}
            else{return intent.getExtras().getString(name);}
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public String getExtraFromIntent(Intent intent,String name,String defaultValue){
        try{
            if(intent.getExtras().isEmpty()){return null;}
            else{return intent.getExtras().getString(name,defaultValue);}
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //==============================SharedPreferences管理======================
    //保存的文件保存在\data\data\<package name>\shared_prefs\目录下面
    //写入（如果没有创建过XML，则自动先进行创建）SharedPreferences到对应XML文件中
    //如果你想要删除通过SharedPreferences产生的文件，可以通过以下方法：
    //File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","Activity.xml");
    //if(file.exists()){file.delete();}
    public SharedPreferences putSharedPreferences(Context context,String xmlFileName,String name,String value){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit(); //编辑文件
        editor.putString(name, value);         //根据键值对添加数据
        editor.commit();  //保存数据信息
        return share;
    }
    public SharedPreferences putSharedPreferences(Context context,String xmlFileName,String name,int value){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit(); //编辑文件
        editor.putInt(name, value);         //根据键值对添加数据
        editor.commit();  //保存数据信息
        return share;
    }
    public SharedPreferences putSharedPreferences(Context context,String xmlFileName,String name,boolean value){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit(); //编辑文件
        editor.putBoolean(name, value);         //根据键值对添加数据
        editor.commit();  //保存数据信息
        return share;
    }
    //读取SharedPreferences的XML文件中的值
    public String getSharedPreferences(Context context,String xmlFileName,String name,String defaultValue){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        return share.getString(name, defaultValue);
    }
    public int getSharedPreferences(Context context,String xmlFileName,String name,int defaultValue){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        return share.getInt(name, defaultValue);
    }
    public boolean getSharedPreferences(Context context, String xmlFileName, String name, boolean defaultValue){
        SharedPreferences share = context.getSharedPreferences(
                (xmlFileName == null) ? EDWIN_SHAREDPREFERENCES_XML_FILENAME : xmlFileName ,
                context.MODE_PRIVATE);
        return share.getBoolean(name, defaultValue);
    }


    //================================Android4.4中获取资源路径问题===============================
    //来源：http://blog.csdn.net/huangyanan1989/article/details/17263203
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getResourcePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    // 定义一个获取网络图片数据的方法:
    public static byte[] httpGetImage(String url) throws Exception {
        URL mUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = streamToByteArray(inStream);
        inStream.close();
        return bt;
    }

    //================================Http通信相关=====================================
    // 获取网页的html源代码
    public static String httpGetHtml(String url) throws Exception {
        URL mUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] data = streamToByteArray(in);
            String html = new String(data, "UTF-8");
            return html;
        }
        return null;
    }
    //根据URL下载文件，前提是这个文件当中的内容是文本，函数的返回值就是文件当中的内容
    public String getFileStrFromUrl(String urlStr){
        StringBuffer sb=new StringBuffer();
        String line=null;
        BufferedReader buffer=null;
        try {
            //创建一个URL对象
            URL url=new URL(urlStr);
            //创建一个Http连接
            try {
                HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
                //使用IO流读取数据
                buffer=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while ((line = buffer.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    //根据URL得到输入流
    public InputStream getInputStreamFromUrl(String urlStr)
            throws MalformedURLException, IOException {
        URL url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConn.getInputStream();
        return inputStream;
    }

    //==========================输入流、输出流相关===================================
    //将数据流转换为字节数组
    public static byte[] streamToByteArray(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
    //根据输入流将数据写入目标文件
    public void writeInputSteamToFile(InputStream input, File desFile) throws Exception {
        OutputStream output = new FileOutputStream(desFile);
        byte[] buffer = new byte[1024];
        int i = 0, total = 0;
        while ((i = input.read(buffer)) != -1) {
            output.write(buffer, 0, i);
        }
        output.flush();
        output.close();
        input.close();
    }

    //===========================时间相关========================================
    //返回系统当前时间的毫秒数
    public long getSystemNowTimeMillis(){
        return System.currentTimeMillis();
    }
    //根据传来的毫秒数返回文字描述
    //实例：1969-12-31 16:00:00.000
    public String getTimeDescStrFromTimeMillis(long timeMillis) {
//        return new Date(timeMillis).toString(); //实例：Fri Jun 03 21:06:05 GMT+08:00 2016
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.format(new Date(timeMillis));
    }
    //根据传来的毫秒数，根据格式返回文字描述
//    formatter实例：
//    yyyyMMdd                   19691231
//    yyyy-MM-dd                 1969-12-31
//    yyyy-MM-dd HH:mm           1969-12-31 16:00
//    yyyy-MM-dd HH:mmZ          1969-12-31 16:00-0800
//    yyyy-MM-dd HH:mm:ss.SSS    1969-12-31 16:00:00.000
//    yyyy-MM-dd HH:mm:ss.SSSZ   1969-12-31 16:00:00.000-0800
//    yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
    public String getTimeDescStrFromTimeMillis(long timeMillis,String formatString){
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.format(new Date(timeMillis));
    }
    //===
    //获取当前时间的文字描述
    public String getTimeDescStrFromNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.format(new Date(getSystemNowTimeMillis()));
    }
    //根据格式返回当前时间的文字描述
    public String getTimeDescStrFromNow(String formatString){
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.format(new Date(getSystemNowTimeMillis()));
    }

    //===========================判断文件、文件夹相关==============================
    //获取文件
    public File getFile(String filePathFull){
        File file =new File(filePathFull);
        return file;
    }
    //判断文件或文件夹是否存在
    public boolean isFileOrFolderExist(String filePathFull){
        return getFile(filePathFull).exists();
    }
    //判断是不是文件夹
    //返回true=文件夹，false表示不为文件夹或为文件不存在
    public boolean isDirectory(String filePathFull){
        return getFile(filePathFull).isDirectory();
    }
    //在本地创建文件
    //注：如果在SD卡上创建，则使用getSDCardPath()获取SD卡目录后再对路径进行拼接
    public static File createFile(String sdFilePathFull, boolean isDelExistFile){
        File file =new File(sdFilePathFull);
        try {
            //如果文件不存在，则创建空文件；如果文件存在，但标志为“需要删除文件”，则将文件删除后再创建一个空文件；否则保留原文件
            if (!file.exists()){
                file.createNewFile();
            }
            else{
                if(isDelExistFile){
                    file.delete();
                    file.createNewFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    //在本地创建目录
    //注：如果在SD卡上创建，则使用getSDCardPath()获取SD卡目录后再对路径进行拼接
    public static File createDir(String sdDirPathFull){
        File dir=new File(sdDirPathFull);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        return dir;
    }
    //通过遍历方式获取文件夹中所有子文件夹和文件
    //实例：
    //        public static void main(String[] args) throws Exception {
    //            //递归显示C盘下所有文件夹及其中文件
    //            File root = new File("c:");
    //            showAllFiles(root);
    //        }
    public final static void showAllFiles(File dir) throws Exception{
        File[] fs = dir.listFiles();
        for(int i=0; i<fs.length; i++){
            System.out.println(fs[i].getAbsolutePath());
            if(fs[i].isDirectory()){
                try{
                    showAllFiles(fs[i]);
                }catch(Exception e){}
            }
        }
    }
    //获取文件的最终修改时间
    public long getFileLastModified(String filePathFull){
        try{
            File file=getFile(filePathFull);
            //文件不存在，返回0
            if(!file.exists()){
                return 0;
            }
            else{
                return file.lastModified();
            }
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    //将一个InputSteam里面的数据写入到本地
    //注：如果在SD卡上创建，则使用getSDCardPath()获取SD卡目录后再对路径进行拼接
    //注：desFolderPathFull最后不带"/"
    public static File writeInputSteamToFile(String desFolderPathFull, String desfileName, InputStream input,
                                             boolean isOverwriteExistFile){
        System.out.println("FolderPath="+desFolderPathFull+";fileName="+desfileName+";");
        File file =null;
        File folder=null;
        OutputStream output=null;
        try {
            //如果文件夹不存在，则创建文件夹
            folder=createDir(desFolderPathFull);
            System.out.println("folder="+folder);
            //如果文件不存在，则创建空文件
            file=createFile(desFolderPathFull+"/"+desfileName,true);
            System.out.println("file="+file);
            //绑定空文件到输出流
            //注：使用FileOutputStream或FileWriter创建文件输出流对象时，Java系统不论语句中指定的文件是否存在，都会创建一个。
            //因此，为了避免将数据写入一个已经存在的文件中（它会覆盖文件中的原有内容）。
            //程序中一般需要使用File对象来判断某个文件是否已经存在。
            //---
            //如果文件已存在，并且传入参数为“不覆盖已有文件”，则什么都不做
            if(file.exists() && file.length()>0 && isOverwriteExistFile==false){
                //do nothing
            }
            else {  //否则将输入流通过输出流写入目标文件
                //绑定文件到输入流
                output = new FileOutputStream(file);
                //创建缓存文件
                byte buffer[] = new byte[1024*10];
                int len  = 0;
                //如果下载成功就往SD卡里些数据
                while((len =input.read(buffer)) != -1){
                    output.write(buffer,0,len);
                }
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            try{
                input.close();
                output.close();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    //====================使用SMB协议读写共享文件，使用jcifs-1.3.18.jar=====================
    //from: http://dongisland.iteye.com/blog/1453613
    //smb:域名;用户名:密码@目的IP/文件夹/文件名.xxx，其中域名可以不填
    //如：文件：  smb://xxx:xxx@192.168.2.1/testIndex/1.txt
    //如：文件夹：smb://xxx:xxx@192.168.2.1/testIndex/
    //xxx:xxx是共享机器的用户名密码
//    实例：smbGet("smb://myDomain;xxx:xxx@192.168.2.1/testIndex/1.txt","c://Temp/");
    //---
    //获取Smb文件
    public SmbFile getSmbFile(String remoteFileSmbUrl){
        SmbFile remoteFile = null;
        try {
            remoteFile = new SmbFile(remoteFileSmbUrl);
            //确认文件是否存在，如果不存在，返回null
            remoteFile.connect();
            return remoteFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //判断SMB协议的文件或文件夹是否存在
    public boolean isSmbFileOrFolderExist(String remoteFileSmbUrl){
        try {
            return getSmbFile(remoteFileSmbUrl).exists();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    //判断SMB协议的是不是文件夹
    //返回true=文件夹，false表示不为文件夹或为文件不存在
    public boolean isSmbDirectory(String remoteFileOrFolderSmbUrl){
        try {
            return getSmbFile(remoteFileOrFolderSmbUrl).isDirectory();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    //获取SMB文件的最终修改时间
    public long getSmbFileLastModified(String remoteFileSmbUrl){
        try{
            SmbFile file=getSmbFile(remoteFileSmbUrl);
            //文件不存在，返回0
            if(!file.exists()){
                return 0;
            }
            else{
                return file.lastModified();
            }
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    //遍历文件夹并获得文件夹中所有文件（文件夹）名称，并返回
    public static List<String> smbGetFileNamesFromSmb(String remoteFolderSmbUrl){
        //String url="smb://192.168.2.1/testIndex/";
        List<String> fileNames = new ArrayList<String>();
        try {
            SmbFile file = new SmbFile(remoteFolderSmbUrl);
            if(file.exists()){
                if(file.isFile()){  //表示方法输入的路径为文件
                    fileNames.add(file.getName());
                }
                else {  //表示方法输入的路径为文件夹
                    SmbFile[] files = file.listFiles();
                    for (SmbFile f : files) {
                        //如果要找特定的文件名，如exe文件，则加上条件：if(smbFile.getName().indexOf(".EXE")!=-1)
                        fileNames.add(f.getName());
                        //System.out.println(f.getName());
                    }
                }
            }
            //其它写法参考：
//            for (int i = 0; i < tempList.length; i++) {
//                if (tempList[i].isFile()) {
//                    System.out.println("文     件：" + tempList[i]);
//                }
//                if (tempList[i].isDirectory()) {
//                    System.out.println("文件夹：" + tempList[i]);
//                }
//            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (SmbException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
    //从共享目录获取文件并放入输入流并返回
    public static InputStream smbGetFileToInputStream(String remoteFileSmbUrl)
    //public InputStream smbGetFileToInputStream(String remoteFileUrl, String localDir)
    {
        InputStream in = null;
//        OutputStream out = null;
        try
        {
            SmbFile remoteFile = new SmbFile(remoteFileSmbUrl);
            //确认文件是否存在，如果不存在，返回null
            remoteFile.connect();
            if (remoteFile == null)
            {
                System.out.println("共享文件不存在");
                return null;
            }
//            String fileName = remoteFile.getName();
//            File localFile = new File(localDir + File.separator + fileName);
//            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
//            out = new BufferedOutputStream(new FileOutputStream(localFile));
            in = new SmbFileInputStream(remoteFile);
//            out = new FileOutputStream(localFile);
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            while ((len = in.read(buffer)) != -1) {
//                out.write(buffer, 0, len);
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
//        finally
//        {
//            try
//            {
//                out.close();
//                in.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
        return (InputStream)in;
    }
    //从本地上传文件到共享目录
    public static void smbPutFileToSmbServer(String remoteFolderSmbUrl, String localFilePath)
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            File localFile = new File(localFilePath);
            String fileName = localFile.getName();
            SmbFile remoteFile = new SmbFile(remoteFolderSmbUrl + "/" + fileName);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //===========================文件SD卡读写相关方法============================
    //获取APP所在安装目录的files文件
    //例如：/data/data/com.edwin.edwinbutler/files
    public String getAppFilesFolderPath(Context context){
        //System.out.println("sdCardPath="+sdCardPath);
        return context.getApplicationContext().getFilesDir().getAbsolutePath()+"";
    }

    //===========================文件SD卡读写相关方法============================
    //得到当前外部存储设备(SD卡)的目录
    public String getSDCardPath(){
        //System.out.println("sdCardPath="+sdCardPath);
        return Environment.getExternalStorageDirectory()+"";
    }


    //===========================获取APP、process、service、memory、task等信息==============================
    //参考：http://blog.sina.com.cn/s/blog_7a9ade2c0100zyb2.html
    //获取文件
    private ActivityManager activityManager = null;
    public void AppInformation(Context context){
        this.activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    }
    //判断后台一个Service是否在运行
    //实例：isServiceRunning(mContext,"com.edwin.edwinbutler.AlarmLongRunningService")
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    public boolean isAppRunning(String packagename){
        //具体可参考：isServiceRunning方法
        System.out.println("appIsRunning()...");
        return false;
    }
    public int killProcessByName(String killProcessName){
        System.out.println("killProcessByName()...");
        int killNum = 0;
        List appProcessList = activityManager.getRunningAppProcesses();
        for(int i=0; i<appProcessList.size(); i++){
            ActivityManager.RunningAppProcessInfo appProcessInfo = (ActivityManager.RunningAppProcessInfo) appProcessList.get(i);
            //进程ID
            int pid = appProcessInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = appProcessInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = appProcessInfo.processName;
            //获得该进程占用的内存
            int[] memPid = new int[]{ pid };
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            System.out.println("process name: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");
            //获得每个进程里运行的应用程序(包)，即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            for(String pkg : packageList){
                System.out.println("package name " + pkg + " in process id is -->" + pid);
            }
            if(killProcessName.equals(processName)){
                System.out.println("===============killProcess pid-->" + pid);
                android.os.Process.killProcess(pid);
                killNum++;
            }
        }
        return killNum;
    }
    public long getSystemAvaialbeMemorySize(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        System.out.println("getSystemAvaialbeMemorySize()...memory size: " + memSize);
        return memSize;
        //调用系统函数，字符串转换long -String KB/MB
        //return Formatter.formatFileSize(context, memSize);
    }
    public List getRunningAppProcessInfo(){
        System.out.println("getRunningAppProcessInfo()...");
        List appProcessList = activityManager.getRunningAppProcesses();
        for(int i=0; i<appProcessList.size(); i++){
            ActivityManager.RunningAppProcessInfo appProcessInfo = (ActivityManager.RunningAppProcessInfo) appProcessList.get(i);
            //进程ID
            int pid = appProcessInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = appProcessInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = appProcessInfo.processName;
            //获得该进程占用的内存
            int[] memPid = new int[]{ pid };
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            System.out.println("process name: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");
            //获得每个进程里运行的应用程序(包)，即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            for(String pkg : packageList){
                System.out.println("package name " + pkg + " in process id is -->" + pid);
            }
        }
        return appProcessList;
    }
    public List getRunningServiceInfo(){
        System.out.println("getRunningServiceInfo()...");
        List serviceList = activityManager.getRunningServices(30);
        for(int i=0; i<serviceList.size(); i++){
            ActivityManager.RunningServiceInfo serviceInfo = (ActivityManager.RunningServiceInfo) serviceList.get(i);
            //进程ID
            int pid = serviceInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = serviceInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = serviceInfo.process;
            String serviceStr = serviceInfo.toString();
            System.out.println("serviceStr: " + serviceStr);
            //获得该进程占用的内存
            int[] memPid = new int[]{ pid };
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            System.out.println("The name of the process this service runs in: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");
        }
        return serviceList;
    }
    public List getRunningTaskInfo(){
        System.out.println("getRunningServiceInfo()...");
        List taskList = activityManager.getRunningTasks(30);
        for(int i=0; i<taskList.size(); i++){
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo) taskList.get(i);
            String packageName = taskInfo.baseActivity.getPackageName();
            System.out.println("package name: " + packageName);
        }
        return taskList;
    }

    //===============================Shell调用（Linux Shell）===============================
    //此处为Shell的简单用法，如果要执行复杂用法，则使用ShellUtils.Class
    //实例：
    //最简单调用（只调用一句命令就关闭Shell）
//    shellCommandOnce("touch -t 20150808.100000 /sdcard/ABC/11.txt");
    //简单调用（不接收错误数据）
//    OutputStream os=eU.shellCommandInit();
//    shellCommandDo(os,"chmod 777 " + "/Abc/11.txt");
//    shellCommandDo(os,"touch -t 20140801.100000 /sdcard/!Abc/11.txt");
//    shellCommandClose(os);
    //复杂调用（接收错误数据）
//    OutputStream os=null;
//    try {
//        os=eU.shellCommandInit();
//        if(os!=null){
//            eU.shellCommandDo(os,"input keyevent " + KeyEvent.KEYCODE_VOLUME_DOWN);
//            eU.shellCommandDo(os,"chmod 777 " + "/Abc/11.txt");
//            eU.shellCommandDo(os,"touch -t 20140801.100000 /sdcard/!Abc/11.txt");
//        }
//        else{
//            Log.d("错误","Shell初始化时错误");
//        }
//    }
//    catch (Exception e) {
//        e.printStackTrace();
//    }
//    finally{
//        if(!eU.shellCommandClose(os)){
//            Log.d("错误","Shell关闭时错误");
//        }
//    }
    //======
    //对shell进行初始化，传回OutputStream，之后对OutputStream进行写入操作即等于对命令行进行输入操作
    public OutputStream shellCommandInit(){
        try {
            Log.d("Edwin", "ShellCommandInit succeed!");
            return Runtime.getRuntime().exec("su").getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //对shell进行命令传入并执行，执行结束后传回是否成功标志
    public boolean shellCommandDo(OutputStream os,String cmd){
        try {
            OutputStream mOs=os;
            if (os == null) {   //获取root权限(su=superuser),如果失败则理论上应该使用sh命令
                mOs = Runtime.getRuntime().exec("su").getOutputStream();
            }
            mOs.write((cmd+"\n").getBytes());
            mOs.flush();
            Log.d("Edwin", "ShellCommandDo succeed, cmd= " + cmd);
        }
        catch (Exception e) {
//            e.printStackTrace();
            Log.d("Edwin", "ShellCommandDo failed, errorMsg= " + e.getMessage() + ",cmd= " + cmd);
            return false;
        }
        return true;
    }
    //shell使用完毕后，对shell进行关闭操作，每次shellCommandInit后都必须调用当前方法，否则会引起内存泄露
    public boolean shellCommandClose(OutputStream os){
        try {
            if (os != null) {
                os.write("exit\n".getBytes());
                os.flush();
                os.close();
            }
        }
        catch (Exception e) {
//            e.printStackTrace();
            Log.d("Edwin", "ShellCommandClose failed, errorMsg=" + e.getMessage());
            return false;
        }
        Log.d("Edwin", "ShellCommandClose succeed!");
        return true;
    }
    //简单调用方法，即只调用一句命令就关闭Shell
    public void shellCommandOnce(String cmd){
        OutputStream os=shellCommandInit();
        shellCommandDo(os,cmd);
        shellCommandClose(os);
    }
    //通过调用touch命令修改文件最后更新日期
    //注：time的格式YYYYMMDD[.hhmmss]，例如：20150808.100000
    //具体touch用法touch [-alm] [-t YYYYMMDD[.hhmmss]] <file>，可以在adb中通过touch -help查看
    public void setFileLastModifiedByShell(String time,String filePathFull){
        shellCommandOnce("touch -t " + time + " " + filePathFull);
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
//    实例：
//    public class MainActivity extends Activity
//    {
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.main);
//            String apkRoot="chmod 777 "+getPackageCodePath();
//            SystemManager.RootCommand(apkRoot);
//        }
//    }
    public static boolean rootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }

    //========================字符和字符串控制========================
    /**
     * 十六进制字符串 转换为 byte[]
     * @param hexString
     *            the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c
     *            char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
        // return (byte) "0123456789ABCDEF".indexOf(c);
    }
    /**
     * byte[] 转换为 十六进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    //========================加密解密========================
    //from：http://www.cnblogs.com/wanqieddy/p/3522049.html
    //from: http://www.open-open.com/lib/view/open1410508672086.html
    //注解：
//    如基本的单向加密算法：
//    BASE64 严格地说，属于编码格式，而非加密算法
//    MD5(Message Digest algorithm 5，信息摘要算法)
//    SHA(Secure Hash Algorithm，安全散列算法)
//    HMAC(Hash Message Authentication Code，散列消息鉴别码)复杂的对称加密（DES、PBE）、非对称加密算法：
//    DES(Data Encryption Standard，数据加密算法)
//    PBE(Password-based encryption，基于密码验证)
//    RSA(算法的名字以发明者的名字命名：Ron Rivest, AdiShamir 和Leonard Adleman)
//    DH(Diffie-Hellman算法，密钥一致协议)
//    DSA(Digital Signature Algorithm，数字签名)
//    ECC(Elliptic Curves Cryptography，椭圆曲线密码编码学)
//    MD5、SHA、HMAC这三种加密算法，可谓是非可逆加密，就是不可解密的加密方法。我们通常只把他们作为加密的基础。单纯的以上三种的加密并不可靠。
//    BASE64
//    按照RFC2045的定义，Base64被定义为：Base64内容传送编码被设计用来把任意序列的8位字节描述为一种不易被人直接识别的形式。（The Base64 Content-Transfer-Encoding is designed to represent arbitrary sequences of octets in a form that need not be humanly readable.）
//    常见于邮件、http加密，截取http信息，你就会发现登录操作的用户名、密码字段通过BASE64加密的。
//    主要就是BASE64Encoder、BASE64Decoder两个类，我们只需要知道使用对应的方法即可。另，BASE加密后产生的字节位数是8的倍数，如果不够位数以=符号填充。
//    MD5
//    MD5 -- message-digest algorithm 5 （信息-摘要算法）缩写，广泛用于加密和解密技术，常用于文件校验。校验？不管文件多大，经过MD5后都能生成唯一的MD5值。好比现在的ISO校验，都是MD5校验。怎么用？当然是把ISO经过MD5后产生MD5的值。
//    通常我们不直接使用上述MD5加密。通常将MD5产生的字节数组交给BASE64再加密一把，得到相应的字符串。
//    SHA
//    SHA(Secure Hash Algorithm，安全散列算法），数字签名等密码学应用中重要的工具，被广泛地应用于电子商务等信息安全领域。虽然，SHA与MD5通过碰撞法都被破解了， 但是SHA仍然是公认的安全加密算法，较之MD5更为安全。
//            HMAC
//            HMAC(Hash Message Authentication Code，散列消息鉴别码，基于密钥的Hash算法的认证协议。消息鉴别码实现鉴别的原理是，用公开函数和密钥产生一个固定长度的值作为认证标识，用这个标识鉴别消息的完整性。使用一个密钥生成一个固定大小的小数据块，即MAC，并将其加入到消息中，然后传输。接收方利用与发送方共享的密钥进行鉴别认证等。
//            BASE64的加密解密是双向的，可以求反解。
//            MD5、SHA以及HMAC是单向加密，任何数据加密后只会产生唯一的一个加密串，通常用来校验数据在传输过程中是否被修改。其中HMAC算法有一个密钥，增强了数据传输过程中的安全性，强化了算法外的不可控因素。
//            单向加密的用途主要是为了校验数据在传输过程中是否被修改。
    //===实例：===
//    public class CoderTest {
//        @Test
//        public void test() throws Exception {
//            String inputStr = "简单加密";
//            System.err.println("原文:\n" + inputStr);
//            byte[] inputData = inputStr.getBytes();
//            String code = Coder.encryptBASE64(inputData);
//            System.err.println("BASE64加密后:\n" + code);
//            byte[] output = Coder.decryptBASE64(code);
//            String outputStr = new String(output);
//            System.err.println("BASE64解密后:\n" + outputStr);
//            // 验证BASE64加密解密一致性
//            assertEquals(inputStr, outputStr);
//            // 验证MD5对于同一内容加密是否一致
//            assertArrayEquals(Coder.encryptMD5(inputData), Coder
//                    .encryptMD5(inputData));
//            // 验证SHA对于同一内容加密是否一致
//            assertArrayEquals(Coder.encryptSHA(inputData), Coder
//                    .encryptSHA(inputData));
//            String key = Coder.initMacKey();
//            System.err.println("Mac密钥:\n" + key);
//            // 验证HMAC对于同一内容，同一密钥加密是否一致
//            assertArrayEquals(Coder.encryptHMAC(inputData, key), Coder.encryptHMAC(
//                    inputData, key));
//            BigInteger md5 = new BigInteger(Coder.encryptMD5(inputData));
//            System.err.println("MD5:\n" + md5.toString(16));
//            BigInteger sha = new BigInteger(Coder.encryptSHA(inputData));
//            System.err.println("SHA:\n" + sha.toString(32));
//            BigInteger mac = new BigInteger(Coder.encryptHMAC(inputData, inputStr));
//            System.err.println("HMAC:\n" + mac.toString(16));
//        }
//    }
    private static final String KEY_SHA = "SHA";
    private static final String KEY_MD5 = "MD5";
    private static final String KEY_MAC = "HmacMD5";
    private static final int BUFFER_SIZE = 1024;
    /**
     * BASE64 加密
     * @param str
     * @return
     */
    public static String encryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 加密
            return new String(Base64.encode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * BASE64 解密
     * @param str
     * @return
     */
    public static  String decryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * GZIP 加密
     * @param str
     * @return
     */
    public static  byte[] encryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes("UTF-8"));
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            // base64 加密
            return encode;
            //          return new String(encode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * GZIP 解密
     * @param str
     * @return
     */
    public static  String decryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] decode = str.getBytes("UTF-8");
            //gzip 解压缩
            ByteArrayInputStream bais = new ByteArrayInputStream(decode);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len=gzip.read(buf, 0, BUFFER_SIZE))!=-1){
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();
            decode = baos.toByteArray();
            baos.close();
            return new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * MD5加密
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);
        return md5.digest();
    }
    /**
     * SHA加密
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }
    /**
     * 初始化HMAC密钥
     * @return
     * @throws Exception
     */
    public static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded().toString());
    }
    /**
     * HMAC加密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key).getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }
}


