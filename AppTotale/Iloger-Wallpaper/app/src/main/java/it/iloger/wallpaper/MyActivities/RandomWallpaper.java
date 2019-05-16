package it.iloger.wallpaper.MyActivities;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import it.iloger.wallpaper.R;

public class RandomWallpaper extends AppCompatActivity {


    private Handler mHandler = new Handler();
    private ArrayList<Integer> mArraylist = new ArrayList<>();
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomwallpaper);
    }

    //---------------------Metodo per il controllo inziiale sul valore inserito (secondi),
    // riempimento dell'array degli sfondi disponibili, lancio thread del cambio sfondo-------------------//


    public void startRepeating(View v) {

        editText = findViewById(R.id.secondEditText);

        if (editText.getText().toString().isEmpty() || editText.getText().toString().equals("0")){

            AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
            miaAlert.setTitle("Error!");
            miaAlert.setMessage("Insert a correct value!");
            miaAlert.setPositiveButton("Ok", null);
            AlertDialog alert = miaAlert.create();
            alert.show();

        }else {

            mArraylist.add(R.drawable.a);
            mArraylist.add(R.drawable.b);
            mArraylist.add(R.drawable.c);
            mArraylist.add(R.drawable.d);
            mArraylist.add(R.drawable.e);
            rChange.run();

        }

    }

    //---------------------------------------------------------------------------//


    //---------------Funzione per fermare il cambio sfondo-----------------------//

    public void stopRepeating(View v) {
        mArraylist.clear();
        System.out.println(mArraylist.size());
        mHandler.removeCallbacks(rChange);
    }

    //---------------------------------------------------------------------------//


    //-----------------------Indice Randomico------------------------------------//

    public Integer randomElem(){
        Random random = new Random();
        int randomIndex = random.nextInt(mArraylist.size());
        return mArraylist.get(randomIndex);
    }

    //---------------------------------------------------------------------------//


    //-----------------------Thread per il cambio sfondo------------------------//

    private Runnable rChange = new Runnable() {
        @Override
        public void run() {
            setWallpaper();
            mHandler.postDelayed(this, (Integer.parseInt(editText.getText().toString()))*1000);
        }
    };

    //---------------------------------------------------------------------------//


    //----------------------Funzione per il cambio sfondo----------------------//

    private void setWallpaper(){

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setResource(randomElem());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}