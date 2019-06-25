package com.example.testgrafico;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private EditText editText1;
    private TextView textView;
    private EditText estremoAText;
    private EditText estremoBText;
    private int estremoA;
    private int estremoB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.textView);
        estremoAText = findViewById(R.id.editText2);
        estremoBText = findViewById(R.id.editText3);
        textView.setText("");

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
                    }
                }catch (NumberFormatException n){
                    error(MainActivity.this, "Inserire valori estremi corretti!");
                    return;
                }

                drawExpression1();
            }
        });

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String input = mEdit.toString();
                // Controllo che il numero di parentesi inserito sia corretto
                // stesso numero di parentesi aperte e parentesi chiuse (appena finisco di scrivere)
                if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                    textView.setText("Inserisci un numero corretto di parentesi!");
                    button.setClickable(false);
                } else {
                    textView.setText("");
                    button.setClickable(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void drawExpression1(){

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
}
