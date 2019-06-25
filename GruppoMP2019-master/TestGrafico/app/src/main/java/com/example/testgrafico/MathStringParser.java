package com.example.testgrafico;

import java.util.ArrayList;
import java.util.List;

public class MathStringParser {

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
        List<Integer> charCountMap = new ArrayList<Integer>();
        for (int i = 0; i<string.length(); i++){
            if (string.charAt(i) == '('){
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace("(", "").length()) - (string.length() - string.replace(")", "").length()));
        return string.substring(indexToStart);
    }

    static public String isRightString(String string){
        List<Integer> charCountMap = new ArrayList<Integer>();
        for (int i = 0; i<string.length(); i++){
            if (string.charAt(i) == ')'){
                charCountMap.add(i);
            }
        }
        int indexToStart = charCountMap.get((string.length() - string.replace(")", "").length()) - (string.length() - string.replace("(", "").length()));
        return string.substring(0, indexToStart+1);
    }
}
