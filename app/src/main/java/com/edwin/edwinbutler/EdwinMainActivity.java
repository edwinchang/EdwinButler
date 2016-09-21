package com.edwin.edwinbutler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by edwinchang on 2016-5-21.
 */
public class EdwinMainActivity extends AppCompatActivity {
    private Button btn_smb;
    private AppCompatActivity mContext = this;
    private EdwinUtil eU = new EdwinUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edwin_main);

        //mContext = EdwinTestActivity.this;
        btn_smb = (Button) findViewById(R.id.mybuttonSmb);

        btn_smb.setOnClickListener(
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

    }
    //================onCreate结束================

}
