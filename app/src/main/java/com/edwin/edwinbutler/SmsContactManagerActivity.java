package com.edwin.edwinbutler;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by edwinchang on 2016-5-26.
 */
public class SmsContactManagerActivity extends AppCompatActivity {
    private Button btn_0, btn_1, btn_2, btn_3, btn_4;

    private EdwinUtil eU=new EdwinUtil();

    private Context mContext=this;
    private SmsContactManager smsContactManager=new SmsContactManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smscontactmanager_demo);

//        mContext = SmsContactManagerActivity.this;
        btn_0 = (Button) findViewById(R.id.mybutton00);
        btn_1 = (Button) findViewById(R.id.mybutton01);
        btn_2 = (Button) findViewById(R.id.mybutton02);
        btn_3 = (Button) findViewById(R.id.mybutton03);
        btn_4 = (Button) findViewById(R.id.mybutton04);

        //获取短信
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "获取短信", true, true, true);
//                Toast.makeText(mContext, "获取短信", Toast.LENGTH_SHORT).show();
//                Log.i("Edwin", "获取短信");
                smsContactManager.getMsgs(mContext);
            }
        });

        //插入短信
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "插入短信", true, true, true);
                smsContactManager.insertMsg(mContext);

            }
        });

        //获取通讯录
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "获取通讯录", true, true, true);
                smsContactManager.getContacts(mContext);
            }
        });

        //查找通讯录
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "查找通讯录", true, true, true);
                smsContactManager.queryContact(mContext,"test1");
            }
        });

        //添加联系人
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eU.Log(mContext, null, "添加联系人", true, true, true);
                try {
                    smsContactManager.AddContact(mContext);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
