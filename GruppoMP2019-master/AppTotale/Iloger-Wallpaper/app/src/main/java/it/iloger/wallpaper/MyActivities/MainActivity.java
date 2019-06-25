package it.iloger.wallpaper.MyActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import it.iloger.wallpaper.R;
import net.sourceforge.jeval.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertBox));
        builder.setTitle(getString(R.string.closeApp));
        builder.setMessage(getString(R.string.exitFromApp));

        builder.setPositiveButton(getString(R.string.confirmExit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(1);
            }
        });
        builder.setNegativeButton(getString(R.string.noExit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        Evaluator mathEvaluator = new Evaluator();
        String expression = "log(exp(100))*(pow(-1,1/2)";
        try {
            System.out.println(mathEvaluator.evaluate("log(exp(100))*pow(-1,1/2)"));
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
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
