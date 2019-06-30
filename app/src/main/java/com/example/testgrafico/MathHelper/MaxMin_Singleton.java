package com.example.testgrafico.MathHelper;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MaxMin_Singleton {

    private static MaxMin_Singleton instance = null;

    public ArrayList<ArrayList<Entry>> getValues() {
        return values;
    }

    public void setValues(ArrayList<ArrayList<Entry>> values) {
        this.values = values;
    }

    private ArrayList<ArrayList<Entry>> values;

    public static MaxMin_Singleton getInstance(){

        if (instance == null){
            instance = new MaxMin_Singleton();
        }
        return instance;

    }
}
