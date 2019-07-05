package com.example.testgrafico.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class MathFunctionParser {

    static public String isLeftDigit(String string){
        if (string.contains("(")){
            return string.substring(string.lastIndexOf("(")+1);
        } else {
            return string;
        }
    }

    static public String isRightDigit(String string){
        if (string.contains(")")) {
            return string.substring(0, string.indexOf(")"));
        } else {
            return string;
        }
    }

    static public String isLeftString(String string){
        if (string.contains("*")) {
            string = string.substring(string.lastIndexOf("*"));
        }
        List<Integer> charCountMap = new ArrayList<>();
        for (int i = 0; i<string.length(); i++){
            if (string.charAt(i) == '('){
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace("(", "").length()) - (string.length() - string.replace(")", "").length()));
        return string.substring(indexToStart);
    }

    static public String isRightString(String string){
        if (string.contains("*")){
            string = string.substring(string.indexOf("*"));
        }
        List<Integer> charCountMap = new ArrayList<>();
        for (int i = 0; i<string.length(); i++){
            if (string.charAt(i) == ')'){
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace(")", "").length()) - (string.length() - string.replace("(", "").length()));
        return string.substring(0, indexToStart+1);
    }

    static public String containsAtan(String input){
        String function = "a_tan";
        String string = input.substring(input.indexOf("a_tan") + function.length());
        System.out.println("String is: " + string);
        List<Integer> charCountMap = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ')') {
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace(")", "").length()) - (string.length() - string.replace("(", "").length()));
        String inside = string.substring(0, indexToStart + 1);
        System.out.println("Inside is: " + inside);
        return input.replace("a_tan" + inside + "", "atan(toRadians" + inside + ")");
    }

    static public String containsAcos(String input){
        String function = "a_cos";
        String string = input.substring(input.indexOf("a_cos") + function.length());
        System.out.println("String is: " + string);
        List<Integer> charCountMap = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ')') {
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace(")", "").length()) - (string.length() - string.replace("(", "").length()));
        String inside = string.substring(0, indexToStart + 1);
        System.out.println("Inside is: " + inside);
        return input.replace("a_cos" + inside + "", "acos(toRadians" + inside + ")");
    }

    static public String containsAsin(String input){
        String function = "a_sin";
        String string = input.substring(input.indexOf("a_sin") + function.length());
        System.out.println("String is: " + string);
        List<Integer> charCountMap = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ')') {
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace(")", "").length()) - (string.length() - string.replace("(", "").length()));
        String inside = string.substring(0, indexToStart + 1);
        System.out.println("Inside is: " + inside);
        return input.replace("a_sin" + inside + "", "asin(toRadians" + inside + ")");
    }
}
