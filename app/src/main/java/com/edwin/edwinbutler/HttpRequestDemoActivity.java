package com.edwin.edwinbutler;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by edwinchang on 2016-5-30.
 */
public class HttpRequestDemoActivity extends AppCompatActivity {
    private Button btn_1, btn_2, btn_3;
    private TextView txtView_1;
    private AppCompatActivity mContext = this;
    private EdwinUtil eU = new EdwinUtil();

    private String HTML_URL = "http://www.baidu.com";
    private String detail = "";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    txtView_1.setText(detail);
                    Toast.makeText(mContext, "文本获取成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    txtView_1.setText(detail);
                    Toast.makeText(mContext, "文本获取失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.httprequest_demo);

        btn_1 = (Button) findViewById(R.id.button1);
//        btn_2 = (Button) findViewById(R.id.button2);
        txtView_1=(TextView) findViewById(R.id.textView1);

        btn_1.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "Http访问Demo", true, true, true);
                //注：访问http等外网协议一定要通过后台线程进行，如在主线程中访问则报NetworkOnMainThreadException异常
                //如果希望与主线程交互，则使用handler方式进行，传递数据则通过主程序中的私有变量传递
                //这里用了detail变量，当然也可以通过handler直接传递数据
                new Thread() {
                    public void run() {
                        try {
                            detail=EdwinUtil.httpGetHtml(HTML_URL);
                            if(detail==null){
                                detail="文本获取失败";
                                handler.sendEmptyMessage(0x002);
                            }
                            else{
                                handler.sendEmptyMessage(0x001);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}