package it.iloger.wallpaper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void liveWallpaper(View view){
        Intent intent1 = new Intent(this, LiveWallpaper.class);
        startActivity(intent1);
    }

    public void randomWallpaper(View view){
        Intent intent2 = new Intent(this, RandomWallpaper.class);
        startActivity(intent2);
    }

    public void cameraWallpaper(View view){
        Intent intent3 = new Intent(this, CameraWallpaper.class);
        startActivity(intent3);
    }

}
