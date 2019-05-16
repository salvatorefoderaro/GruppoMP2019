package it.iloger.wallpaper.MyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }
}