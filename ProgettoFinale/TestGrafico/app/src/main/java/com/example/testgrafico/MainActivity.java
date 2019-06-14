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
import android.widget.Toast;

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
    private Button button;
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
        button = findViewById(R.id.button);
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
                if ((input.length() - input.replace(")", "").length()) - (input.length() - input.replace("(", "").length()) != 0){
                    textView.setText("Inserisci un numero corretto di parentesi!");
                } else {
                    textView.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
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
        input = input.replace("e", "exp");

        System.out.println("Stringa in input: " + input);

        String toLeft, toRight, leftString, rightString;

        if (!input.contains("x")){
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

        while(input.contains("^")) {
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

        for (double i = estremoA; i <= estremoB; i +=precision) {

            try {
                if ( (!mathEvaluator.evaluate(input.replace("x", Double.toString(i))).equals("NaN"))
                        && (!mathEvaluator.evaluate(input.replace("x", Double.toString(i))).equals("-Infinity"))
                        && (!mathEvaluator.evaluate(input.replace("x", Double.toString(i))).equals("+Infinity"))) {
                    entries.add(new Entry((float) i, Float.parseFloat(mathEvaluator.evaluate(input.replace("x", Double.toString(i))))));
                } else {
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

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        seekbar.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
