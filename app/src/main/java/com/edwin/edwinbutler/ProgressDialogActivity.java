package com.edwin.edwinbutler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.os.Handler;

/**
 * Created by edwinchang on 2016-5-24.
 */
public class ProgressDialogActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private ProgressDialog pd1 = null;
    private ProgressDialog pd2 = null;
    private final static int MAXVALUE = 100;
    private int progressStart = 0;
    private int add = 0;
    private Context mContext = null;

    //定义一个用于更新进度的Handler,因为只能由主线程更新界面,所以要用Handler传递信息
    final Handler hand = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //这里的话如果接受到信息码是123
            if(msg.what == 123)
            {
                //设置进度条的当前值
                pd2.setProgress(progressStart);
            }
            //如果当前大于或等于进度条的最大值,调用dismiss()方法关闭对话框
            if(progressStart >= MAXVALUE)
            {
                pd2.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressdialog_demo);
        mContext = ProgressDialogActivity.this;
        bindViews();
    }

    private void bindViews() {
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_one:
                //这里的话参数依次为,上下文,标题,内容,是否显示进度,是否可以用取消按钮关闭
                ProgressDialog.show(ProgressDialogActivity.this, "资源加载中", "资源加载中,请稍后...",false,true);
                break;
            case R.id.btn_two:
                pd1 = new ProgressDialog(mContext);
                //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
                pd1.setTitle("软件更新中");
                pd1.setMessage("软件正在更新中,请稍后...");
                pd1.setCancelable(true);
                //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
                pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd1.setIndeterminate(true);
                //调用show()方法将ProgressDialog显示出来
                pd1.show();
                break;
            case R.id.btn_three:
                //初始化属性
                progressStart = 0;
                add = 0;
                //依次设置一些属性
                pd2 = new ProgressDialog(ProgressDialogActivity.this);
                pd2.setMax(MAXVALUE);
                pd2.setTitle("文件读取中");
                pd2.setMessage("文件加载中,请稍后...");
                //这里设置为不可以通过按取消按钮关闭进度条 comm：当前情况下为点击界面空白部分可取消，否则为不可取消
                pd2.setCancelable(true);
                pd2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //这里设置的是是否显示进度,设为false才是显示的哦！
                pd2.setIndeterminate(false);
                pd2.show();
                //这里的话新建一个线程,重写run()方法,
                new Thread()
                {
                    public void run()
                    {
                        while(progressStart < MAXVALUE)
                        {
                            //这里的算法是决定进度条变化的,可以按需要写
                            progressStart = 2 * usetime() ;
                            //把信息码发送给handle让更新界面
                            hand.sendEmptyMessage(123);
                        }
                    }
                }.start();
                break;
        }
    }

    //这里设置一个耗时的方法:
    private int usetime() {
        add++;
        try{
            Thread.sleep(100);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return add;
    }
}