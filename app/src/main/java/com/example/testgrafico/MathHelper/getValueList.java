package com.example.testgrafico.MathHelper;

import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;

public class getValueList {

    public void MaxMin(float maxX, float maxY, float minX, float minY){

        ArrayList<Entry> max = new ArrayList<>();
        ArrayList<Entry> min = new ArrayList<>();

        ArrayList<ArrayList<Entry>> mEm_coord = new ArrayList<>();

        max.add(new Entry(maxX, maxY));
        min.add(new Entry(minX, minY));

        mEm_coord.add(0, max);
        mEm_coord.add(1, min);

        MaxMin_Singleton.getInstance().setValues(mEm_coord);
    }

}
