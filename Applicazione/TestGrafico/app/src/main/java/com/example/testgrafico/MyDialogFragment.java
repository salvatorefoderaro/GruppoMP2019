package com.example.testgrafico;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import static com.example.testgrafico.getValueList.getListValue;

public class MyDialogFragment extends DialogFragment {

    private String function1 = null;
    private String function2 = null;
    private int estremoA;
    private int estremoB;
    private float precision = 1;
    private Context context;
    private SeekBar seekBar;
    private Chart<LineData> chart;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sample_dialog, container);
        getDialog().setTitle("Simple Dialog");
        chart = view.findViewById(R.id.chart);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                precision = (seekBar.getProgress() + 1) * 0.20f;
                drawExpression();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        function1 = getArguments().getString("function1");
        function2 = getArguments().getString("function2");
        estremoA = getArguments().getInt("estremoA");
        estremoB = getArguments().getInt("estremoB");

        drawExpression();

        return view;
    }

    public void drawExpression(){

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        if (function1 != null) {
            ArrayList<Entry> entries1 = getListValue(context, function1, estremoA, estremoB, precision);
            if (entries1 == null){
                dismiss();
                return;
            }
            LineDataSet dataSet = new LineDataSet(entries1, function1);
            dataSet.setColor(Color.RED);
            dataSet.setCircleColor(Color.RED);
            dataSet.setDrawValues(false);
            dataSets.add(dataSet);
        }

        if (function2 != null){
            ArrayList<Entry> entries2 = getListValue(context, function2, estremoA, estremoB, precision);
            if (entries2 == null){
                dismiss();
                return;
            }
            LineDataSet dataSet1 = new LineDataSet(entries2, function2);
            dataSet1.setDrawValues(false);
            dataSets.add(dataSet1);
        }

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
    }

}