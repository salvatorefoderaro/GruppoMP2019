package it.iloger.wallpaper.MyViews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends android.support.v7.widget.AppCompatButton {
    public MyButton(Context context) {
        super(context);
        this.setTextColor(Color.parseColor("BLUE"));
        this.setTextColor(Color.parseColor("#FF0000"));
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }
}