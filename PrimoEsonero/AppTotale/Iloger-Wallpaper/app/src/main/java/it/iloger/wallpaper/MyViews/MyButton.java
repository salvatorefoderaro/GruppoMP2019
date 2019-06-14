package it.iloger.wallpaper.MyViews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import it.iloger.wallpaper.R;

public class MyButton extends android.support.v7.widget.AppCompatButton {
    public MyButton(Context context) {
        super(context);
        this.setTextColor(Color.parseColor("white"));
        this.setBackgroundColor(getResources().getColor(R.color.buttonColor));
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.parseColor("white"));
        this.setBackgroundColor(getResources().getColor(R.color.buttonColor));
    }

    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }
}