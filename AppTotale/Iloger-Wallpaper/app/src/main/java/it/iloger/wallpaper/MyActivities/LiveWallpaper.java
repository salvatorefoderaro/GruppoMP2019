package it.iloger.wallpaper.MyActivities;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import it.iloger.wallpaper.R;

public class LiveWallpaper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livewallpaper);
    }

    public void setWallpaper(View view) {
        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, MatrixWallLiveWallpaper.class));
        startActivity(intent);
    }

    public void wallpaperSettings(View view){
        Intent intent = new Intent(this, MatrixWallSettings.class);
        startActivity(intent);

    }
}
