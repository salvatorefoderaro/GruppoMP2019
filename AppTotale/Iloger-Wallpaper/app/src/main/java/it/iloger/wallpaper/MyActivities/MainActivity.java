package it.iloger.wallpaper.MyActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import it.iloger.wallpaper.MyViews.MyButton;
import it.iloger.wallpaper.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButton btt_camera = findViewById(R.id.btt_salvatore);

    }

    @Override
    public void onBackPressed(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertBox));
        builder.setTitle("Chiusura dell'applicazioene");
        builder.setMessage("Sei sicuro di voler uscire dall'applicazione?");

        // add the buttons
        builder.setPositiveButton("Si, esci!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(1);
            }
        });
        builder.setNegativeButton("No!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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
