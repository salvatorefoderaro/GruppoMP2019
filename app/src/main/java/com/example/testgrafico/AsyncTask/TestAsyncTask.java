package com.example.testgrafico.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.testgrafico.Fragment.FragmentDrawGraph;
import com.example.testgrafico.MathHelper.MaxMin_Singleton;
import com.example.testgrafico.MathHelper.getValueList;
import com.github.mikephil.charting.data.Entry;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.testgrafico.MainActivity.error;
import static com.example.testgrafico.MathHelper.MathStringParser.isLeftDigit;
import static com.example.testgrafico.MathHelper.MathStringParser.isLeftString;
import static com.example.testgrafico.MathHelper.MathStringParser.isRightDigit;
import static com.example.testgrafico.MathHelper.MathStringParser.isRightString;

public class TestAsyncTask extends AsyncTask<ArrayList<Entry>, String, ArrayList<Entry>> {

    private ProgressDialog dialog;
    private Context context;
    private String input;
    private int estremoA;
    private int estremoB;
    private float precision;
    private FragmentDrawGraph istance;
    private String valueToParse;
    private ArrayList<Entry> max;
    private ArrayList<Entry> min;

    public TestAsyncTask(Context context, String input, int estremoA,
                         int estremoB, float precision, ProgressDialog dialog, FragmentDrawGraph istance) {
        // list all the parameters like in normal class define
        this.context = context;
        this.input = input;
        this.estremoA = estremoA;
        this.estremoB = estremoB;
        this.precision = precision;
        this.dialog = dialog;
        this.istance = istance;
    }

    // Le chiamate a publishProgress() chiamano il metodo onProgressUpdate() presente nell'AsyncTask

    @Override
    protected ArrayList<Entry> doInBackground(ArrayList<Entry>... arrayLists) {
        float maxY = 0, minY = 0, maxX = 0, minX = 0, value;
        boolean firstValue = true;

        Evaluator mathEvaluator = new Evaluator();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        String toLeft, toRight, leftString, rightString, betweenAbs;

        input = input.replace(" ", "");

        // Per evitare problemi con l'esponenziale, effettuo questa sostituzione

        while (input.contains("|")){
            betweenAbs = input.substring(input.indexOf("|") + 1,
                        input.substring(input.indexOf("|") +1).indexOf("|") + input.indexOf("|")+1);
            input = input.replace("|" + betweenAbs + "|", "abs(" + betweenAbs + ")");
        }

        while(input.contains("^")) {

            // Trasformo tutti i "cappelletti", per poterli far digerire a jEval
            leftString = input.substring(0, input.indexOf("^"));
            rightString = input.substring(input.indexOf("^") + 1);

            if (leftString.length() == 0 || rightString.length() == 0){
                return null;
            }

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

        input = input.replace("e", "exp(1)");
        System.out.println(input);
        // I valori del ciclo for vengono dati dalla seekbar, grazie alla quale sarà possibile modificare i valori di precision
        for (double i = estremoA; i <= estremoB; i +=precision) {



            try {
                if (!input.contains("x_")){
                    valueToParse = mathEvaluator.evaluate(input);
                } else {
                    valueToParse = mathEvaluator.evaluate(input.replace("x_", String.format(Locale.CANADA,"%.12f", i)));
                }

                // Controllo che il valore della funzione non sia NaN (non definito) o +/- infinito,
                // lo faccio sostituendo ad x_ il valore assunto da i nel ciclo for
                if ( (!valueToParse.equals("NaN")) ) {

                    value = Float.parseFloat(valueToParse);

                    // Aggiunta una mezza cosa per gli asintoti
                    if (valueToParse.equals("-Infinity")){
                        entries.add(new Entry((float) i, minY - 50));
                        minY = minY - 9999f;
                        minX = (float)i;
                        continue;
                    }

                    if (valueToParse.equals("+Infinity")){
                        entries.add(new Entry((float) i, maxY + 50));
                        maxY = maxY - 9999f;
                        maxX = (float)i;
                        continue;
                    }

                    if (valueToParse.equals("Infinity")){
                        publishProgress( "Valore troppo grande!");
                        return null;
                    }

                    // Trovo massimo e minimo
                    if (firstValue) {
                        minY = maxY = value;
                        maxX = minX = (float) i ;
                        firstValue = false;
                    } else if (value > maxY) {
                        maxY = value;
                        maxX = (float) i;
                    } else if (value < minY) {
                        System.out.println("\nValue is: " + value + " and minY is: " + minY);
                        minY = value;
                        minX = (float) i;
                    }

                    // Aggiungo il valore calcolato al grafico
                    entries.add(new Entry((float) i, value));
                    getValueList getValueList = new getValueList();
                    getValueList.MaxMin(maxX, maxY, minX, minY);

                } else {
                    publishProgress( "Errore nel dominio della funzione!");
                    return null;
                }

            } catch (EvaluationException e) {
                e.printStackTrace();
                // Errore di sintassi nella stringa inserita dall'utente,
                // unico motivo per il quale jEval fallisce (quando non sa interpretare la stringa)
                publishProgress("Errore di sintassi nella funzione inserita!");
                return null;
            }
        }
        return entries;
    }

    // Ho spostato qua la chiamata ad error(), in quanto qui viene chiamato l'UI Thread e quindi posso fare l'aggiornamento.
    // Farlo direttamente dalla parte sopra da errore in quanto si tratta di un Worker thread
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        error(context, values[0]);
    }

    // Al termine dell'esecuzione, chiamo il metodo all'interno del fragment per restituire la lista con i valori calcolati
    @Override
    protected void onPostExecute(ArrayList<Entry> result) {
        // execution of result of Long time consuming operation
        System.out.println("Ho terminato l'esecuzione!");
        istance.getValueBack(result, this.input);
    }

    // Prima dell'esecuzione
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}