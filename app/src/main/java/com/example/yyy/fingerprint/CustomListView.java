package com.example.yyy.fingerprint;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by YYY on 2017/3/30.
 */

public class CustomListView extends ListView {

    public CustomListView(Context context) {
        super(context);
    }
    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec+getDividerHeight());
    }

}
