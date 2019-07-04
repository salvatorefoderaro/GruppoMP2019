package com.example.testgrafico.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.example.testgrafico.Fragment.FragmentDrawGraph;
import com.example.testgrafico.Fragment.FragmentFunction;
import com.example.testgrafico.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private EditText editText1;
    private EditText estremoAText;
    private EditText estremoBText;
    private TextInputLayout test;
    private TextInputLayout test1;
    private TextInputLayout A;
    private TextInputLayout B;
    private int estremoA;
    private int estremoB;
    private Menu menuList;
    private int cursor_position;
    private String clicked_editText;
    private FragmentManager fm = getSupportFragmentManager();
    private FragmentFunction fragment = new FragmentFunction();
    private FloatingActionButton drawGraph;

    public String getClicked_editText() {
        return clicked_editText;
    }

    public void setClicked_editText(String clicked_editText) {
        this.clicked_editText = clicked_editText;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadLocale();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        System.out.println("AAAAAAAAAAA" + this.getResources().getConfiguration().locale.toString() + this.getResources().getString(R.string.localCode));
        System.out.println("Default value is: " + prefs.getString("My_Lang", ""));
        if (prefs.getString("My_Lang", "").isEmpty()){
            System.out.println("E' vuoto?");
        }
        this.menuList = toolbar.getMenu();
        onCreateOptionsMenu(this.menuList);

        A = findViewById(R.id.name_text_input2);
        B = findViewById(R.id.name_text_input3);
        drawGraph = findViewById(R.id.fab);
        test = findViewById(R.id.name_text_input);
        test1 = findViewById(R.id.name_text_input1);

        estremoAText = A.getEditText();
        estremoBText = B.getEditText();
        editText = test.getEditText();
        editText1 = test1.getEditText();

        // Aggiungo tutti quanti i controlli per i vari input una volta premuto il FAB
        drawGraph.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

        if ((estremoAText.getText().toString().isEmpty() || estremoBText.getText().toString().isEmpty())){
            error(MainActivity.this, getText(R.string.riempiCampi).toString());
            return;
        }

        if ((editText.getText().toString().isEmpty() && editText1.getText().toString().isEmpty())){
            error(MainActivity.this, getText(R.string.riempiCampi).toString());
            return;
        }

        if (!editText.getText().toString().isEmpty()){
            String input = editText.getText().toString();
            if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                error(MainActivity.this, "Func 1\n\n" + getText(R.string.controllaParentesi).toString());
                return;
            }
        }

        if (!editText1.getText().toString().isEmpty()){
            String input = editText1.getText().toString();
            if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                error(MainActivity.this, "Func 2\n\n" + getText(R.string.controllaParentesi).toString());
                return;
            }
        }

        try {
            estremoA = Integer.parseInt(estremoAText.getText().toString());
            estremoB = Integer.parseInt(estremoBText.getText().toString());
            if (estremoA >= estremoB){
                error(MainActivity.this, getText(R.string.AminB).toString());
                return;
            }
        } catch (NumberFormatException n){
            error(MainActivity.this, getText(R.string.estremi).toString());
            return;
        }

        // Se tutti i controlli sono andati a buon fine, procedo impostando il fragment
        setToDraw();
        }
    });

        //-----------------------------------DOUBLE TAP---------------------------------------------------------//

        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                setClicked_editText("editText");   //Memorizzo l'editTex su cui viene fatto il doppio tap
                cursor_position = editText.getSelectionStart(); //Memorizzo la posizione del cursore, in modo da sapere dove inserire la funzione selezionata
                /*FragmentManager fm = getSupportFragmentManager();
                FragmentFunction fragment = new FragmentFunction();*/
                fragment.show(fm, "func");

                return true;
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        final GestureDetector gestureDetector1 = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                // start activity
                setClicked_editText("editText1");
                cursor_position = editText1.getSelectionStart();
                fragment.show(fm, "func");
                return true;
            }
        });

        editText1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector1.onTouchEvent(event);
            }
        });

        //-------------------------------------------------------------------------------------------------------------//
    }

    // Aggiungo le icone al menù
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("My_Lang", "").isEmpty()){
            if (this.getResources().getString(R.string.localCode).equals("it")){
                menu.findItem(R.id.italyButton).setVisible(false);
            } else {
                menu.findItem(R.id.engButton).setVisible(false);
            }
        } else {
            if (this.getResources().getString(R.string.localCode).equals("it")){
                menu.findItem(R.id.italyButton).setVisible(false);
            } else {
                menu.findItem(R.id.engButton).setVisible(false);
            }
        }
        return true;
    }

    public void setToDraw(){

        // Aggiungo in un bundle le funzioni da passare al Fragment
        Bundle bundle = new Bundle();
        bundle.putInt("estremoA", estremoA);
        bundle.putInt("estremoB", estremoB);

        if (!editText.getText().toString().isEmpty()){
            bundle.putString("function1", editText.getText().toString());
        }

        if (!editText1.getText().toString().isEmpty()){
            bundle.putString("function2", editText1.getText().toString());
        }

        // Faccio partire il fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentDrawGraph myDialogFragment = new FragmentDrawGraph();
        myDialogFragment.setArguments(bundle);
        myDialogFragment.show(fm, "dialog_fragment");
    }

    public static void error(Context context, String msg){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.errore);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = (this).getLayoutInflater();
                builder.setCancelable(false);
                builder.setView(inflater.inflate(R.layout.fragment_help, null));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create();
                builder.show();
                return true;

            case R.id.italyButton:
                setLocale("it");
                menuList.findItem(R.id.italyButton).setVisible(false);
                menuList.findItem(R.id.engButton).setVisible(true);
                recreate();
                return true;

            case R.id.engButton:
                setLocale("en");
                menuList.findItem(R.id.italyButton).setVisible(true);
                menuList.findItem(R.id.engButton).setVisible(false);
                recreate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void func_Coseno(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(cos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(cos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Seno(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(sin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(sin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Acos(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(a_cos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(a_cos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Asin(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(a_sin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(a_sin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Atan(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(a_tan(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(a_tan(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Atan2(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(atan2(x_, y))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(atan2(x_, y))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_log(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(log(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(log(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_tan(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(tan(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(tan(x_)(");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    /*public void get_POS(View view) {

        .setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //int size = series1.size();
                    float screenX = event.getX();
                    float screenY = event.getY();
                    float width_x = v.getWidth();
                    float viewX = screenX - v.getLeft();
                    float viewY = screenY - v.getTop();
                    float percent_x = (viewX / width_x);
                  //  int pos = (int) (size * percent_x);

                    System.out.println("X: " + viewX + " Y: " + viewY + " Percent = " + percent_x);
                   // System.out.println("YVal = " + series1.getY(pos));
                    //tvNum.setText(series1.getY(pos) + "");
                    return true;
                }
                return false;
            }
        });

    }*/

    /*private void setAppLocale(String localCode){

        Locale locale2 = new Locale(localCode);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;

    }*/

//    private void setAppLocale(String localCode){
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale(localCode));
//        res.updateConfiguration(conf, dm);
//        conf.setTo(conf);
//        }

    private void setLocale(String localCode){
        Locale locale = new Locale(localCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //salvo nelle sharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", localCode);
        editor.apply();
    }

    //carico la lingua salvata nelle SharedPreferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    @Override
    public void onBackPressed(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        builder.setTitle(getString(R.string.closeApp));
        builder.setMessage(getString(R.string.exitFromApp));

        // add the buttons
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

    }
}
