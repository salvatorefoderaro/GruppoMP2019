package com.example.testgrafico.MathHelper;

import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;

public class MaxMin_Singleton {

    private static MaxMin_Singleton instance = null;

    public ArrayList<ArrayList<Entry>> getValues() {
        return values;
    }

    public float maxX;
    public float minX;
    public float maxY;

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float minY;

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
