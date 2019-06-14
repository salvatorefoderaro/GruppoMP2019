package com.example.changebytime;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private ArrayList<Integer> mArraylist = new ArrayList<>();
    private EditText editText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startRepeating(View v) {
        mArraylist.add(R.drawable.a);
        mArraylist.add(R.drawable.b);
        mArraylist.add(R.drawable.c);
        mArraylist.add(R.drawable.d);
        mArraylist.add(R.drawable.e);
        editText = findViewById(R.id.secondEditText);
        rChange.run();
    }

    public void stopRepeating(View v) {
        mHandler.removeCallbacks(rChange);
    }

    public Integer randomElem(){

        Random random = new Random();
        int randomIndex = random.nextInt(mArraylist.size());
        return mArraylist.get(randomIndex);
    }

    private Runnable rChange = new Runnable() {
        @Override
        public void run() {

            setWallpaper();
            mHandler.postDelayed(this, (Integer.parseInt(editText.getText().toString()))*1000);
        }
    };

    private void setWallpaper(){

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setResource(randomElem());
            } catch (IOException e) {
                e.printStackTrace();
            }


    }
}