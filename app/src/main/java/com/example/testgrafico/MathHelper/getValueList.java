package com.example.testgrafico.MathHelper;

import android.content.Context;
import com.github.mikephil.charting.data.Entry;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import static com.example.testgrafico.MathHelper.MathStringParser.isLeftDigit;
import static com.example.testgrafico.MathHelper.MathStringParser.isLeftString;
import static com.example.testgrafico.MathHelper.MathStringParser.isRightDigit;
import static com.example.testgrafico.MathHelper.MathStringParser.isRightString;
import static com.example.testgrafico.MainActivity.error;

public class getValueList {

    static public ArrayList<Entry> getListValue(Context context, String input, int estremoA,
                                                int estremoB, float precision){

       float maxElem = 0, minElem = 0, value;


        Evaluator mathEvaluator = new Evaluator();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        String toLeft, toRight, leftString, rightString, betweenAbs;

        input = input.replace(" ", "");

        if (!input.contains("x_")){

            error(context, "Errore di sintassi nella funzione inserita!");
            return null;
        }

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

            input = input.replace("(" + toLeft + "^" + toRight + ")", "pow(" + toLeft + "," + toRight + ")");
        }

        input = input.replace("e", "exp(1)");

        try {
            mathEvaluator.evaluate("(pow(1,2) + 2)");
        } catch (EvaluationException e) {
            e.printStackTrace();
        }


        System.out.println("\n"+input+"\n");
        
        // I valori del ciclo for vengono dati dalla seekbar, grazie alla quale sarà possibile modificare i valori di precision
        for (double i = estremoA; i <= estremoB; i +=precision) {

            try {

                // Controllo che il valore della funzione non sia NaN (non definito) o +/- infinito,
                // lo faccio sostituendo ad x_ il valore assunto da i nel ciclo for
                if (
                        (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("NaN"))
                            && (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("-Infinity"))
                            && (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("+Infinity"))
                            && (!mathEvaluator.evaluate(input.replace("x_", Double.toString(i))).equals("Infinity"))
                ) {

                    value = Float.parseFloat(mathEvaluator.evaluate(input.replace("x_", Double.toString(i))));

                    // Trovo massimo e minimo
                    if (minElem == 0 || maxElem == 0) {
                        minElem = maxElem = value;
                    } else if (value > maxElem) {
                        maxElem = value;
                    } else if (value < minElem) {
                        minElem = value;
                    }


                    /*TODO Massimo e minimo

                    *   */

                    // Aggiungo il valore calcolato al grafico
                    entries.add(new Entry((float) i, value));
                } else {
                    error(context, "Errore nel dominio o valore non calcolabile!");
                    return null;
                }

            } catch (EvaluationException e) {
                e.printStackTrace();
                // Errore di sintassi nella stringa inserita dall'utente,
                // unico motivo per il quale jEval fallisce (quando non sa interpretare la stringa)
                error(context, "Errore di sintassi nella funzione inserita!");
                return null;
            }

        }


        return entries;
    }

}
