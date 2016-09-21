package com.edwin.edwinbutler;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class DelEditText extends EditText {

    private Drawable imgClear;
    private Context mContext;

    public DelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
//        imgClear = mContext.getResources().getDrawable(R.drawable.del_edit_text_bg_frame_search);
        // comm：getResources().getDrawable已过时deprecated
        // 参见http://stackoverflow.com/questions/29041027/android-getresources-getdrawable-deprecated-api-22
        imgClear = ContextCompat.getDrawable(mContext, R.drawable.ic_close_01);
        imgClear.setBounds(1,1,100,100);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDrawable();
            }
        });
    }

    //绘制删除图片
    private void setDrawable(){
        if (length() < 1)
            //原有例子中使用了setCompoundDrawablesWithIntrinsicBounds，此方法无法调整图片大小，因此优化
            setCompoundDrawables(null, null, null, null);
        else
            setCompoundDrawables(null, null, imgClear, null);
    }

    //当触摸范围在右侧时，触发删除方法，隐藏叉叉
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(imgClear != null && event.getAction() == MotionEvent.ACTION_UP)
        {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}