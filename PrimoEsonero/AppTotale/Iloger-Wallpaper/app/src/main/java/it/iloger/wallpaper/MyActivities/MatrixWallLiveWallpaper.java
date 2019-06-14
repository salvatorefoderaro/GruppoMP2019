package it.iloger.wallpaper.MyActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.enrico.colorpicker.colorDialog;

import java.util.Random;

import it.iloger.wallpaper.R;

/**
 * Created by sapan on 1/10/2017.
 */

public class MatrixWallLiveWallpaper extends WallpaperService {

    private boolean mVisible;  // visible flag
    Canvas canvas;      // canvas reference
    int Drawspeed=10;   // thread call delay time
    Context mcontext;   //reference to the current context

    int background_color= Color.parseColor("#FF000000");
    int text_color=Color.parseColor("#FF8BFF4A");

    int width = 1000000; //default initial width
    int height = 100; //default initial height
    int fontSize = 15; //font size of the text which will fall
    int columnSize = width/fontSize; //column size ; no of digit required to fill the screen
    int parentWidth;
    String text = "iLoger Wallpaper";  // Text which need to be drawn
    char[] textChar = text.toCharArray(); // split the character of the text
    int textLength = textChar.length;   //length of the length text
    Random rand = new Random(); //random generater

    int[]  textPosition; // contain the position which will help to draw the text

    @Override
    public Engine onCreateEngine() {
        mcontext = this;  //set the current context

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        text = sharedPref.getString("matrix_scroll_text", "MATRIX");
        Drawspeed = Integer.parseInt(sharedPref.getString("matrix_falling_speed","10"));
        fontSize = Integer.parseInt(sharedPref.getString("matrix_font_size","15"));
        background_color = colorDialog.getPickerColor(getBaseContext(), 1);
        text_color =colorDialog.getPickerColor(getBaseContext(), 2);

        textChar = text.toCharArray(); // split the character of the text
        textLength = textChar.length;
        columnSize = width/fontSize;

        return new LiveWall(); // this calls contain the wallpaper code
    }


    /*
    * this class extends the engine for the live wallpaper
    * THis class implements all the draw calls required to draw the wallpaper
    */
    public class LiveWall extends Engine
    {

        final Handler mHandler = new Handler(); // this is to handle the thread

        //the tread responsibe for drawing this thread get calls every time
        // drawspeed vars set the execution speed
        private final Runnable mDrawFrame = new Runnable() {
            public void run() {

                background_color = colorDialog.getPickerColor(getBaseContext(), 1);
                text_color =colorDialog.getPickerColor(getBaseContext(), 2);

                drawFrame();
            }
        };


        //Called when the surface is created
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            //update  the matrix variables
            width = getDesiredMinimumWidth();
            height = getDesiredMinimumHeight();
            columnSize = width/fontSize;
            textPosition = new int[columnSize+1];
            for(int x = 0; x < columnSize; x++) {
                textPosition[x] = 1;
            }

            drawFrame();

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawFrame);
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mcontext);
                text = sharedPref.getString("matrix_scroll_text", "MATRIX");
                Drawspeed = Integer.parseInt(sharedPref.getString("matrix_falling_speed","10"));
                fontSize = Integer.parseInt(sharedPref.getString("matrix_font_size","15"));
                background_color = colorDialog.getPickerColor(getBaseContext(), 1);
                text_color =colorDialog.getPickerColor(getBaseContext(), 2);

                textChar = text.toCharArray(); // split the character of the text
                textLength = textChar.length;
                columnSize = width/fontSize;
                drawFrame();
            } else {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mcontext);
                text = sharedPref.getString("matrix_scroll_text", "MATRIX");
                Drawspeed = Integer.parseInt(sharedPref.getString("matrix_falling_speed","10"));
                fontSize = Integer.parseInt(sharedPref.getString("matrix_font_size","15"));
                background_color = colorDialog.getPickerColor(getBaseContext(), 1);
                text_color =colorDialog.getPickerColor(getBaseContext(), 2);

                textChar = text.toCharArray(); // split the character of the text
                textLength = textChar.length;
                columnSize = width/fontSize;

                mHandler.removeCallbacks(mDrawFrame);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            //this is necessay to remove the call back
            mHandler.removeCallbacks(mDrawFrame);
        }

        // the thread call this functioin with some delay "drawspeed"
        void drawFrame()
        {
            final SurfaceHolder holder = getSurfaceHolder();

            canvas = null;  // canvas
            try {
                canvas = holder.lockCanvas();  //get the canvas
                if (canvas != null) {
                    canvasDraw();
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }

            // Reschedule the next redraw
            // this is the replacement for the invilidate funtion
            // every time call the drawFrame to draw the matrix
            mHandler.removeCallbacks(mDrawFrame);
            if (mVisible) {
                    // set the execution delay
                mHandler.postDelayed(mDrawFrame, Drawspeed);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            Paint paint = new Paint();
            paint.setColor(background_color);
            paint.setAlpha(255); //set the alpha
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, width, height, paint);

        }
    }

    void drawText()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(text_color);
        paint.setTextSize(15);

        for(int i =0 ;i<textPosition.length;i++)
        {
            // draw the text at the random position
            canvas.drawText(""+textChar[rand.nextInt(textLength)+0],i*fontSize,textPosition[i]*fontSize,paint);
            // check if text has reached bottom or not
            if(textPosition[i]*fontSize > height && Math.random() > 0.975)
                textPosition[i] = 0;   // change text position to zero when 0 when text is at the bottom

            textPosition[i]++; //increment the position array
        }
    }

    public void canvasDraw()
    {
        Paint paint = new Paint();
        paint.setColor(background_color);

        paint.setAlpha(5);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);//draw rect to clear the canvas

        drawText();
    }
}
