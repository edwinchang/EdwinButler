package com.edwin.edwinbutler;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by edwinchang on 2016-5-29.
 */
public class FileManagerDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editname;
    private EditText editdetail;
    private Button btnclean;
    private Button btnsave;
    private Button btnread;
    private Button btnsaveSD;
    private Button btnreadSD;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filemanager_demo);
        mContext = getApplicationContext();
        bindViews();
    }
    private void bindViews() {
        editdetail = (EditText) findViewById(R.id.editdetail);
        editname = (EditText) findViewById(R.id.editname);
        editname.setText("123.txt");    //默认创建的文件名
        btnclean = (Button) findViewById(R.id.btnclean);
        btnsave = (Button) findViewById(R.id.btnsave);
        btnread = (Button) findViewById(R.id.btnread);
        btnsaveSD = (Button) findViewById(R.id.btnsaveSD);
        btnreadSD = (Button) findViewById(R.id.btnreadSD);
        btnclean.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnread.setOnClickListener(this);
        btnsaveSD.setOnClickListener(this);
        btnreadSD.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnclean:
                editdetail.setText("");
                editname.setText("");
                break;
            case R.id.btnsave:
                FileManager fManager = new FileManager(mContext);
                String filename = editname.getText().toString();
                String filedetail = editdetail.getText().toString();
                try {
                    fManager.save(filename, filedetail);
                    Toast.makeText(mContext, "数据写入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "数据写入失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnread:
                String detail = "";
                FileManager fManager2 = new FileManager(mContext);
                try {
                    String fname = editname.getText().toString();
                    detail = fManager2.read(fname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext, detail, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnsaveSD:
                FileManager fManager3 = new FileManager(mContext);
                String filenameSD = editname.getText().toString();
                String filedetailSD = editdetail.getText().toString();
                try {
                    fManager3.savaFileToSD(filenameSD, filedetailSD);
                    Toast.makeText(mContext, "数据写入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "数据写入失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnreadSD:
                String detailSD = "";
                FileManager fManager4 = new FileManager(mContext);
                try {
                    String fname = editname.getText().toString();
                    detailSD = fManager4.readFromSD(fname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext, detailSD, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}