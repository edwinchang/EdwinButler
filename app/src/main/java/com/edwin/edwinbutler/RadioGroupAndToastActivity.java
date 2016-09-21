package com.edwin.edwinbutler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by edwinchang on 2016-5-23.
 */
public class RadioGroupAndToastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radiogp_test);

        RadioGroup radgroup = (RadioGroup) findViewById(R.id.radioGroup);
        //第一种获得单选按钮值的方法
        //为radioGroup设置一个监听器:setOnCheckedChanged()
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                Toast.makeText(getApplicationContext(), "按钮组值发生改变,你选了" + radbtn.getText(), Toast.LENGTH_LONG).show();
            }
        });

//        //实例：遍历RadioGroup找出被选中的单选按钮
//        for(int i = 0;i < rad.getChildCount();i++)
//        {
//            RadioButton rd = (RadioButton)rad.getChildAt(i);
//            if(rd.isChecked())
//            {
//                strName = rd.getText().toString();
//                break;
//            }
//        }
    }
}