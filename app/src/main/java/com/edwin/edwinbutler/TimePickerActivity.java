package com.edwin.edwinbutler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by edwinchang on 2016-5-23.
 */
public class TimePickerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timepicker_test);
        TimePicker tp_test = (TimePicker) findViewById(R.id.tp_test);
        tp_test.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged( TimePicker view, int hourOfDay, int minute) {
                Toast.makeText(TimePickerActivity.this,"您选择的时间是："+hourOfDay+"时"+minute+"分!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}