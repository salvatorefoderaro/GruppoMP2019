package com.example.testgrafico.MathHelper;

import static com.example.testgrafico.MathHelper.MathFunctionParser.containsA;
import static com.example.testgrafico.MathHelper.MathFunctionParser.isLeftDigit;
import static com.example.testgrafico.MathHelper.MathFunctionParser.isLeftString;
import static com.example.testgrafico.MathHelper.MathFunctionParser.isRightDigit;
import static com.example.testgrafico.MathHelper.MathFunctionParser.isRightString;

public class StringParser {

     static public String parseString(String input) {

         String toLeft, toRight, leftString, rightString, betweenAbs;

         while (input.contains("|")) {
             betweenAbs = input.substring(input.indexOf("|") + 1,
                     input.substring(input.indexOf("|") + 1).indexOf("|") + input.indexOf("|") + 1);
             input = input.replace("|" + betweenAbs + "|", "abs(" + betweenAbs + ")");
         }

         while (input.contains("a_sin")) {
             input = containsA(input, "a_sin", "asin");
         }

         while (input.contains("a_cos")) {
             input = containsA(input, "a_cos", "acos");
         }

         while (input.contains("a_tan")) {
             input = containsA(input, "a_tan", "atan");
         }

         while (input.contains("|")) {
             betweenAbs = input.substring(input.indexOf("|") + 1,
                     input.substring(input.indexOf("|") + 1).indexOf("|") + input.indexOf("|") + 1);
             input = input.replace("|" + betweenAbs + "|", "abs(" + betweenAbs + ")");
         }

         while (input.contains("^")) {

             // Trasformo tutti i "cappelletti", per poterli far digerire a jEval
             leftString = input.substring(0, input.indexOf("^"));
             rightString = input.substring(input.indexOf("^") + 1);

             if (leftString.length() == 0 || rightString.length() == 0) {
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

         input = input.replace("e_", "exp(1)");
         return input;
     }
}
