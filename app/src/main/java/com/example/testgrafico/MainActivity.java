package com.example.testgrafico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private EditText editText1;
    private TextView textView1;
    private TextView textView2;
    private EditText estremoAText;
    private EditText estremoBText;
    private int estremoA;
    private int estremoB;
    private boolean showed = false;
    private Menu menuList;

    private FragmentManager fm = getSupportFragmentManager();
    private FunctionFragment fragment = new FunctionFragment();
    private int cursor_position;


    private String clicked_editText;
    private String selected_Func;

    public String getClicked_editText() {
        return clicked_editText;
    }

    public void setClicked_editText(String clicked_editText) {
        this.clicked_editText = clicked_editText;
    }


    public String getSelected_Func() {
        return selected_Func;
    }

    public void setSelected_Func(String selected_Func) {
        this.selected_Func = selected_Func;
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        estremoAText = findViewById(R.id.editText2);
        estremoBText = findViewById(R.id.editText3);
        textView1.setText("");
        textView2.setText("");

        final Button button = findViewById(R.id.button);
        editText =findViewById(R.id.editText1);
        editText1 =findViewById(R.id.editText4);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            if (estremoAText.getText().toString().isEmpty() || estremoBText.getText().toString().isEmpty() ||
                    editText.getText().toString().isEmpty()){
                error(MainActivity.this, "Riempire tutti i campi");
                return;
            }

            try {
                estremoA = Integer.parseInt(estremoAText.getText().toString());
                estremoB = Integer.parseInt(estremoBText.getText().toString());
                if (estremoA >= estremoB){
                    error(MainActivity.this, "L'estremo A deve essere strettamente minore di B!");
                    return;
                }
            } catch (NumberFormatException n){
                error(MainActivity.this, "Inserire valori estremi corretti!");
                return;
            }

            setToDraw();
            }
        });





        //-----------------------------------DOUBLE TAP---------------------------------------------------------//

        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                setClicked_editText("editText");
                cursor_position = editText.getSelectionStart();
                /*FragmentManager fm = getSupportFragmentManager();
                FunctionFragment fragment = new FunctionFragment();*/
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





        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String input = mEdit.toString();
                // Controllo che il numero di parentesi inserito sia corretto
                // stesso numero di parentesi aperte e parentesi chiuse (appena finisco di scrivere)
                if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                    textView1.setText("Inserisci un numero corretto di parentesi!");
                    button.setClickable(false);
                } else {
                    textView1.setText("");
                    button.setClickable(true);
                }
            }



            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
                if (s.toString().equals("^") && !showed){
                    error(MainActivity.this, "ASPETTAAAAA!\nLa sintassi corretta è la seguente:\n(log(x_))^2\n(sin(x_))^(cos(x_))");
                    showed = true;
                }
            }
        });

        editText1.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String input = mEdit.toString();
                // Controllo che il numero di parentesi inserito sia corretto
                // stesso numero di parentesi aperte e parentesi chiuse (appena finisco di scrivere)
                if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                    textView2.setText("Inserisci un numero corretto di parentesi!");
                    button.setClickable(false);
                } else {
                    textView2.setText("");
                    button.setClickable(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

    }

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

        Bundle bundle = new Bundle();
        bundle.putInt("estremoA", estremoA);
        bundle.putInt("estremoB", estremoB);

        if (!editText.getText().toString().isEmpty()){
            bundle.putString("function1", editText.getText().toString());
        }

        if (!editText1.getText().toString().isEmpty()){
            bundle.putString("function2", editText1.getText().toString());
        }

        FragmentManager fm = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setArguments(bundle);
        myDialogFragment.show(fm, "dialog_fragment");
    }

    public static void error(Context context, String msg){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Errore!");
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
            editText.getText().insert(cursor_position, "cos()");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText.getText().insert(cursor_position, "cos()");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void func_Seno(View view) {

        if (getClicked_editText().equals("editText")){
            //editText.append("cos()");
            editText.getText().insert(cursor_position, "sin()");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }else if (getClicked_editText().equals("editText1")){
            //editText1.append("cos()");
            editText.getText().insert(cursor_position, "sin()");
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
