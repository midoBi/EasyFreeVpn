package com.dev.mobile.vpn.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by mehdi on 01/03/2018.
 */

public class TextViewLabel extends android.support.v7.widget.AppCompatTextView {

    public TextViewLabel(Context context) {
        super(context);

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_condensed_bold.ttf");
        this.setTypeface(custom_font);

        this.setTextColor(Color.parseColor("#3F3832"));


    }

    public TextViewLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
