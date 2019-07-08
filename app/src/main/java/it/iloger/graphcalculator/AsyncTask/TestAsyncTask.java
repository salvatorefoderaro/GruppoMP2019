package it.iloger.graphcalculator.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.github.mikephil.charting.data.Entry;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.math.BigDecimal;
import java.util.ArrayList;

import it.iloger.graphcalculator.Fragment.FragmentDrawGraph;
import it.iloger.graphcalculator.R;

import static it.iloger.graphcalculator.Activity.MainActivity.error;
import static it.iloger.graphcalculator.MathHelper.StringParser.parseString;

public class TestAsyncTask extends AsyncTask<ArrayList<Entry>, String, ArrayList<Entry>> {

    private final ProgressDialog dialog;
    private final Context context;
    private String input;
    private final String originFunction;
    private final float estremoA;
    private final float estremoB;
    private final float precision;
    private final FragmentDrawGraph istance;
    private String valueToParse;
    private float maxY = 0, minY = 0, maxX = 0, minX = 0;

    public TestAsyncTask(Context context, String input, float estremoA,
                         float estremoB, float precision, ProgressDialog dialog, FragmentDrawGraph istance) {
        // list all the parameters like in normal class define
        this.context = context;
        this.input = this.originFunction = input;
        this.estremoA = estremoA;
        this.estremoB = estremoB;
        this.precision = precision;
        this.dialog = dialog;
        this.istance = istance;
    }

    // Le chiamate a publishProgress() chiamano il metodo onProgressUpdate() presente nell'AsyncTask
    @Override
    protected ArrayList<Entry> doInBackground(ArrayList<Entry>... arrayLists) {

        float value;
        boolean firstValue = true;
        boolean constantValue = false;

        Evaluator mathEvaluator = new Evaluator();
        ArrayList<Entry> entries = new ArrayList<>();

        input = parseString(input);
        if (input.contains("#{x_}")) {
            constantValue = true;
        }


        for (Float i = estremoA; i <= estremoB; i += precision) {

            // Utilizzo il BigDecimal per risolvere il problema della somma dei float che non viene "precisa"
            BigDecimal testBigDecimal = new BigDecimal(i);
            testBigDecimal = testBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
            i = testBigDecimal.floatValue();

            try {
                if (constantValue) {
                    valueToParse = mathEvaluator.evaluate(input);
                } else {
                    mathEvaluator.putVariable("x_", Float.toString(i));
                    valueToParse = mathEvaluator.evaluate(input.replace("x_", "#{x_}"));
                }

                // Controllo che il valore della funzione non sia NaN (non definito) o +/- infinito,
                // lo faccio sostituendo ad x_ il valore assunto da i nel ciclo for
                if ((!valueToParse.equals("NaN"))) {

                    value = Float.parseFloat(valueToParse);

                    // Aggiunta una mezza cosa per gli asintoti
                    if (valueToParse.equals("-Infinity")) {
                        entries.add(new Entry(i, minY - 50));
                        minY = minY - 50;
                        minX = i;
                        if (i == estremoA) {
                            firstValue = false;
                        }
                        continue;
                    }

                    if (valueToParse.equals("+Infinity")) {
                        entries.add(new Entry(i, maxY + 50));
                        maxY = maxY + 50;
                        maxX = i;
                        if (i == estremoA) {
                            firstValue = false;
                        }
                        continue;
                    }

                    if (String.valueOf(value).equals("Infinity") || String.valueOf(value).equals("-Infinity")) {
                        publishProgress(this.context.getText(R.string.troppoGrande).toString());
                        dialog.dismiss();
                        return null;
                    }

                    // Trovo massimo e minimo
                    if (firstValue) {
                        minY = maxY = value;
                        maxX = minX = i;
                    } else if (value > maxY) {
                        maxY = value;
                        maxX = i;
                    } else if (value < minY) {
                        minY = value;
                        minX = i;
                    }

                    // Aggiungo il valore calcolato al grafico
                    entries.add(new Entry(i, value));

                    if (i == estremoA) {
                        firstValue = false;
                    }

                } else {
                    publishProgress(this.context.getText(R.string.domainError).toString());
                    dialog.dismiss();
                    return null;
                }

            } catch (EvaluationException e) {
                e.printStackTrace();
                // Errore di sintassi nella stringa inserita dall'utente,
                // unico motivo per il quale jEval fallisce (quando non sa interpretare la stringa)

                publishProgress(this.context.getText(R.string.syntaxError).toString());
                dialog.dismiss();
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
        istance.getValueBack(result, this.originFunction, maxX, maxY, minX, minY);
    }

}