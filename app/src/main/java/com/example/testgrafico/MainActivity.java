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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

}
