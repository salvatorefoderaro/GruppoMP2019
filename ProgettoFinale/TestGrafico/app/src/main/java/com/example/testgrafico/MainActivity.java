package com.example.testgrafico;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import java.util.LinkedList;
import static com.example.testgrafico.MathStringParser.*;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private EditText editText;
    private TextView textView;
    private SeekBar seekbar;
    private EditText estremoAText;
    private EditText estremoBText;
    private float precision;
    private int estremoA;
    private int estremoB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chart = findViewById(R.id.chart);
        textView = findViewById(R.id.textView);
        estremoAText = findViewById(R.id.editText2);
        estremoBText = findViewById(R.id.editText3);
        textView.setText("");

        Button button = findViewById(R.id.button);
        editText =findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                estremoA = Integer.parseInt(estremoAText.getText().toString());
                estremoB = Integer.parseInt(estremoBText.getText().toString());
                drawExpression();
            }
        });
        seekbar = findViewById(R.id.seekBar);
        precision = 0.25f;

        // Seekbar necessaria per modificare la precisione del disegno del grafico,
        // aumentando o diminuendo il numero di punti che devo andare a disegnare
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                precision = (seekbar.getProgress() + 1) * 0.25f;
                System.out.println(precision);
                drawExpression();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekbar.setVisibility(View.GONE);
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
                } else {
                    textView.setText("");
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

    public void drawExpression(){

        Evaluator mathEvaluator = new Evaluator();
        LinkedList<Entry> entries = new LinkedList<Entry>();
        String input = editText.getText().toString();

        System.out.println("Stringa in input: " + input);

        String toLeft, toRight, leftString, rightString;

        if (!input.contains("x_")){

            // La funzione dovra essere f(x_), dunque controllo che il termine sia presente
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Errore!");
            alertDialog.setMessage("Errore di sintassi nella funzione inserita!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        // Per evitare problemi con l'esponenziale, effettuo questa sostituzione
        input = input.replace("e", "(exp(1))");

        while(input.contains("^")) {

            // Trasformo tutti i "cappelletti", per poterli far digerire a jEval
            leftString = input.substring(0, input.indexOf("^"));
            rightString = input.substring(input.indexOf("^") + 1);

            if (leftString.charAt(leftString.length() - 1) != ')') {
                toLeft = isLeftDigit(leftString);
            } else {
                toLeft = isLeftString(leftString);
            }
            if (rightString.charAt(0) != '(') {
                toRight = isRightDigit(rightString);
            } else {
                toRight = isRightString(rightString);
            }
            input = input.replace(toLeft + "^" + toRight, "pow(" + toLeft + "," + toRight + ")");
        }

        // I valori del ciclo for vengono dati dalla seekbar, grazie alla quale sarà possibile modificare i valori di precision
        for (double i = estremoA; i <= estremoB; i +=precision) {

            try {

                // Controllo che il valore della funzione non sia NaN (non definito) o +/- infinito,
                // lo faccio sostituendo ad x_ il valore assunto da i nel ciclo for
                if ((!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("NaN"))
                        && (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("-Infinity"))
                        && (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("+Infinity"))) {

                    // Aggiungo il valore calcolato al grafico
                    entries.add(new Entry((float) i, Float.parseFloat(mathEvaluator.evaluate(input.replace("x_", Double.toString(i))))));
                    entries = null;

                } else {

                    // In caso contrario, non aggiungo i valori e mostro un messaggio di errore
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Errore!");
                    alertDialog.setMessage("Errore nel dominio della funzione!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

            } catch (EvaluationException e) {

                // Errore di sintassi nella stringa inserita dall'utente,
                // unico motivo per il quale jEval fallisce (quando non sa interpretare la stringa)
                e.printStackTrace();
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Errore!");
                alertDialog.setMessage("Errore di sintassi nella funzione inserita!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }

        }

        // Aggiungo i valori ad un dataset e creo tutti gli elementi necessari per il grafico
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        seekbar.setVisibility(View.VISIBLE);
    }
}
