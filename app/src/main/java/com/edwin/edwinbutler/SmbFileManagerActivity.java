package com.edwin.edwinbutler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

/**
 * Created by edwinchang on 2016-5-31.
 */
public class SmbFileManagerActivity extends AppCompatActivity {
    private AppCompatActivity mContext = this;

    private Button btnDownload, btnSaveSetting, btnServiceSwitch;
    private CheckBox checkboxPwdBack,checkboxSync;
    private EditText editUser, editPwd,editRemoteFolderOrFilePath,editLocalFolderName;
    private EditText editSyncHour,editSyncMin,editSyncSec;
    private EditText editLastLog;

    private EdwinUtil eU = new EdwinUtil();

    private String mCheckedLongRunningServiceName="com.edwin.edwinbutler.AlarmLongRunningService";

    @Override
    protected void onResume() {
        super.onResume();
        //更新服务状态按钮中的文字
        componentUpdateServiceText();
    }

    //更新SMB的基本数据
    private void componentUpdateSmbBaseText(){
        //从SharePreference的XML文件取出信息，如果取到则更新，没取到则默认值
        //用户名
        editUser.setText(getShare(mContext,"remoteSmbUsername"));
        if(editUser.getText().length()==0){
//            editUser.setText("ed_zhang");
        }
        //密码text不更新
        //远端目标路径
        editRemoteFolderOrFilePath.setText(getShare(mContext,"remoteSmbFolderOrFilePath"));
        if(editRemoteFolderOrFilePath.getText().length()==0){
//            editRemoteFolderOrFilePath.setText("192.168.0.5/Download/!!!test/");
        }
        //本地存放路径
        editLocalFolderName.setText(getShare(mContext,"remoteSmblocalFolderName"));
        if(editLocalFolderName.getText().length()==0){
//            editLocalFolderName.setText("!EdwinDailyUpdate");
        }
        //最新更新日志
        editLastLog.setText(getShare(mContext,"SmbFileManagerServiceLastLog"));
        if(editLastLog.getText().length()==0){
            editLastLog.setText("当前暂无更新日志!");
        }
    }
    //更新每天同步设置信息
    private void componentUpdateSyncText(){
        //从SharePreference的XML文件取出信息，如果取到则更新，没取到则默认值
        //每天同步check
        checkboxSync.setChecked(
                getShare(mContext,"remoteSmbSyncChecked")!=null &&
                        getShare(mContext,"remoteSmbSyncChecked").equals("true"));
        //同步时
        editSyncHour.setText(getShare(mContext,"remoteSmbSyncHour"));
        if(editSyncHour.getText().length()==0){
            editSyncHour.setText("21");
        }
        //同步分
        editSyncMin.setText(getShare(mContext,"remoteSmbSyncMin"));
        if(editSyncMin.getText().length()==0){
            editSyncMin.setText("0");
        }
        //同步秒
        editSyncSec.setText(getShare(mContext,"remoteSmbSyncSec"));
        if(editSyncSec.getText().length()==0){
            editSyncSec.setText("0");
        }
    }
    //更新服务状态按钮中的文字
    private void componentUpdateServiceText(){
        //判断常驻内存的AlarmLongRunningService服务当前是否已启动
        if(eU.isServiceRunning(mContext,mCheckedLongRunningServiceName)){
            btnServiceSwitch.setText("关闭常驻定时广播服务");
        }
        else{
            btnServiceSwitch.setText("启动常驻定时广播服务");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smbfilemanager);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnSaveSetting = (Button) findViewById(R.id.btnSaveSetting);
        btnServiceSwitch = (Button) findViewById(R.id.btnServiceSwitch);

        checkboxPwdBack=(CheckBox)findViewById(R.id.checkboxPwdBack);
        checkboxSync=(CheckBox)findViewById(R.id.checkboxSync);

        editUser = (EditText) findViewById(R.id.editUser);
        editPwd = (EditText) findViewById(R.id.editPwd);
        editRemoteFolderOrFilePath=(EditText) findViewById(R.id.editRemoteFolderOrFilePath);
        editLocalFolderName=(EditText) findViewById(R.id.editLocalFolderName);

        editSyncHour = (EditText) findViewById(R.id.editSyncHour);
        editSyncMin= (EditText) findViewById(R.id.editSyncMin);
        editSyncSec= (EditText) findViewById(R.id.editSyncSec);

        editLastLog= (EditText) findViewById(R.id.editLastLog);

        //初始化界面控件
        componentUpdateSmbBaseText();
        componentUpdateSyncText();
        componentUpdateServiceText();

        //下载按钮，通过SmbFileManagerService服务立即进行下载
        btnDownload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //将SMB基本信息值写入SharePreferences的XML文件
                        saveSmbBaseSetting();

                        //传入值，准备调用SmbFileManagerService服务
                        Intent serviceIntent = new Intent(mContext, SmbFileManagerService.class);
                        eU.putExtraToIntent(serviceIntent, "remoteSmbUsername", editUser.getText().toString());
                        eU.putExtraToIntent(serviceIntent, "remoteSmbPassword",
                                eU.encryptBASE64(eU.encryptBASE64(eU.encryptBASE64(
                                        editPwd.getText().toString()))));  //密码进行3次BASE64加密后再传输
                        eU.putExtraToIntent(serviceIntent, "remoteSmbFolderOrFilePath",
                                editRemoteFolderOrFilePath.getText().toString());
                        eU.putExtraToIntent(serviceIntent, "remoteSmblocalFolderName",
                                editLocalFolderName.getText().toString());
                        //开始调用同步文件服务
                        //startService(new Intent(getApplicationContext(), ServiceDemo.class));
                        mContext.startService(serviceIntent);
                    }
                });
        //保存设置按钮
        btnSaveSetting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //将SMB基本信息值写入SharePreferences的XML文件
                        saveSmbBaseSetting();
                        //将同步设置信息值写入SharePreferences的XML文件
                        saveSyncSetting();

                        eU.Log(mContext,null,"写入设置文件成功",true,true,true);
                    }
                });
        //启动和关闭服务按钮（即启动常驻内存的AlarmLongRunningService服务，并定时广播）
        btnServiceSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //判断常驻内存的AlarmLongRunningService服务当前是否已启动
                        //已启动，则关闭，反之亦然
                        //关闭
                        if(eU.isServiceRunning(mContext,mCheckedLongRunningServiceName)&&
                                btnServiceSwitch.getText().toString()=="关闭常驻定时广播服务"){
                            //关闭服务
                            //传入值，准备调用AlarmLongRunningService服务
                            Intent serviceIntent = new Intent(mContext, AlarmLongRunningService.class);
//                        eU.putExtraToIntent(serviceIntent, "remoteSmbUsername", editUser.getText().toString());
                            //开始调用服务
                            //startService(new Intent(getApplicationContext(), ServiceDemo.class));
                            mContext.stopService(serviceIntent);

                            //设置同步check为否，并更新同步check的SharePreference
                            checkboxSync.setChecked(false);
                            eU.putSharedPreferences(mContext,null,"remoteSmbSyncChecked","false");
                        }
                        //开启
                        else if(!eU.isServiceRunning(mContext,mCheckedLongRunningServiceName)&&
                                btnServiceSwitch.getText().toString()=="启动常驻定时广播服务"){
                            //启动服务
                            //校验，点击开启服务的时候必须勾选同步，并且时分秒不能为空。 否则退出click
                            if(!checkboxSync.isChecked() || editSyncHour.getText().length()==0 ||
                                    editSyncMin.getText().length()==0 || editSyncSec.getText().length()==0){
                                eU.Log(mContext,null,"点击开启服务的时候必须勾选同步，并且时分秒不能为空",true,true,true);
                                return;
                            }

                            //将SMB基本信息值写入SharePreferences的XML文件
                            saveSmbBaseSetting();
                            //将同步设置信息值写入SharePreferences的XML文件
                            saveSyncSetting();

                            //传入值，准备调用AlarmLongRunningService服务
                            Intent serviceIntent = new Intent(mContext, AlarmLongRunningService.class);
                            //-
                            eU.putExtraToIntent(serviceIntent, "remoteSmbSyncChecked",
                                    checkboxSync.isChecked()?"true":"false");
                            eU.putExtraToIntent(serviceIntent, "remoteSmbSyncHour",
                                    editSyncHour.getText().toString());
                            eU.putExtraToIntent(serviceIntent, "remoteSmbSyncMin",
                                    editSyncMin.getText().toString());
                            eU.putExtraToIntent(serviceIntent, "remoteSmbSyncSec",
                                    editSyncSec.getText().toString());
                            //开始调用服务
                            //startService(new Intent(getApplicationContext(), ServiceDemo.class));
                            mContext.startService(serviceIntent);
                        }
                        else{
                            //启动/关闭失败
                            eU.Log(mContext,null,"启动/关闭常驻定时广播失败，请检查",true,true,true);
                        }
                        //更新服务状态按钮中的文字
                        componentUpdateServiceText();
                    }
                });
        //恢复保存的密码勾选
        checkboxPwdBack.setOnCheckedChangeListener(
                new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //如果选中则尝试恢复密码
                        if(isChecked){
                            String remoteSmbPassword="";
                            remoteSmbPassword=eU.getSharedPreferences(mContext,null,"remoteSmbPassword",null);
                            if(remoteSmbPassword!=null && remoteSmbPassword.length()>0){
                                editPwd.setText(
                                        eU.decryptBASE64(eU.decryptBASE64(eU.decryptBASE64(
                                        remoteSmbPassword))));
                            }
                            else{
                                //未恢复
                                eU.Log(mContext,null,"未找到保存的密码",true,true,true);
                                checkboxPwdBack.setChecked(false);
                            }
                        }
                    }
                });
    }

    private String getExtra(Intent intent,String name){
        return eU.getExtraFromIntent(intent,name,null);
    }

    private String getShare(Context context, String name){
        return eU.getSharedPreferences(context,null,name,null);
    }

    //将SMB基本信息值写入SharePreferences的XML文件
    private void saveSmbBaseSetting(){
        //将值写入SharePreferences的XML文件
        eU.putSharedPreferences(mContext,null,"remoteSmbUsername", editUser.getText().toString());
        eU.putSharedPreferences(mContext,null,"remoteSmbPassword",
                eU.encryptBASE64(eU.encryptBASE64(eU.encryptBASE64(
                        editPwd.getText().toString())))
        );  //密码进行3次BASE64加密再放入XML文件中
        eU.putSharedPreferences(mContext,null,"remoteSmbFolderOrFilePath",
                editRemoteFolderOrFilePath.getText().toString());
        eU.putSharedPreferences(mContext,null,"remoteSmblocalFolderName",
                editLocalFolderName.getText().toString());
    }

    //将同步设置信息值写入SharePreferences的XML文件
    private void saveSyncSetting(){
        //将值写入SharePreferences的XML文件
        //保存是否每天勾选按钮
        eU.putSharedPreferences(mContext,null,"remoteSmbSyncChecked",
                checkboxSync.isChecked()?"true":"false");
        //无论是否勾选了每天同步，都将设置进行记录
        eU.putSharedPreferences(mContext,null,"remoteSmbSyncHour",
                editSyncHour.getText().toString());
        eU.putSharedPreferences(mContext,null,"remoteSmbSyncMin",
                editSyncMin.getText().toString());
        eU.putSharedPreferences(mContext,null,"remoteSmbSyncSec",
                editSyncSec.getText().toString());
    }
}