package com.edwin.edwinbutler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by edwinchang on 2016-5-25.
 */
public class SecondActivityDemoActivity extends AppCompatActivity {
    private TextView txtshow;
    private String str1;
    private String str2;
    private Button btn_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity_demo);

        txtshow = (TextView) findViewById(R.id.txtshow);
        //获得Intent对象,并且用Bundle出去里面的数据
        Intent it = getIntent();
        Bundle bd = it.getExtras();
        //按键值的方式取出Bundle中的数据
        str1 = bd.getCharSequence("txt1").toString();
        str2 = bd.getCharSequence("txt2").toString();
        txtshow.setText(str1 + " " + str2);

        btn_1 = (Button) findViewById(R.id.mybutton_back);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = getIntent();
                Bundle bd = new Bundle();
                bd.putString("strBackward", "已处理传回的值：" + txtshow.getText().toString());
                it.putExtras(bd);
                setResult(0x234, it); //在B中回传数据时采用setResult方法回传值
                finish(); //此处一定要调用finish()方法，之后finish后才会真正进行值的回传
            }
        });
    }//onCreate结束

    @Override
    public void onBackPressed() {
//        Log.i(TAG, "onBackPressed");
        //模拟点击返回按钮
        btn_1.performClick();
//        super.onBackPressed();
    }
}
