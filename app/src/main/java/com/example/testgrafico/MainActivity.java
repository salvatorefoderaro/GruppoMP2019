package com.example.testgrafico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;

import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import com.example.testgrafico.Fragment.FragmentDrawGraph;
import com.example.testgrafico.Fragment.FragmentFunction;
import com.example.testgrafico.Fragment.FragmentHelp;

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

        setContentView(R.layout.activity_main);

        Button ita = findViewById(R.id.bttn_ita);
        Button eng = findViewById(R.id.bttn_en);
        //setAppLocale(language);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        A = findViewById(R.id.name_text_input2);
        B = findViewById(R.id.name_text_input3);
        drawGraph = findViewById(R.id.fab);
        test = findViewById(R.id.name_text_input);
        test1 = findViewById(R.id.name_text_input1);

        estremoAText = A.getEditText();
        estremoBText = B.getEditText();
        editText = test.getEditText();
        editText1 = test1.getEditText();

       ///////////////////////////////////////LANGUAGE////////////////////////////////////////

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLocale("US");
            }
        });

        ita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppLocale("values");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////


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
        this.menuList = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menuList.findItem(R.id.save).setVisible(false);
        menuList.findItem(R.id.share).setVisible(false);
        menuList.findItem(R.id.close).setVisible(false);
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
                FragmentManager fm = getSupportFragmentManager();
                FragmentHelp fragment = new FragmentHelp();
                fragment.show(fm, "dialog_fragment");
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
            editText.getText().insert(cursor_position, "(acos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(acos(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Asin(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(asin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(asin(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Atan(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(atan(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(atan(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Atan2(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "(atan2(x_))");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText1.getText().insert(cursor_position, "(atan2(x_))");
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

    private void setAppLocale(String localCode){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }
}
