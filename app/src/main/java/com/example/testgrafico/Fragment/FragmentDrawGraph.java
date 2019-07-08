package com.example.testgrafico.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.testgrafico.AsyncTask.TestAsyncTask;
import com.example.testgrafico.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentDrawGraph extends DialogFragment {

    private String function1 = null;
    private String function2 = null;
    private float estremoA;
    private float estremoB;
    private final float precision = 0.1f;
    private Context context;
    private LineChart chart;  //Ho cambiato il tipo di chart per avere più opzioni disponibili
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private ProgressDialog dialogBar;
    private ArrayList<ILineDataSet> dataSets;
    private int toPlot;
    private int requested;
    private final ArrayList<Object> wewe = new ArrayList<>();

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

        this.dialogBar = new ProgressDialog(context);
        this.dialogBar.setTitle(getText(R.string.wait).toString());
        this.dialogBar.setMessage(getText(R.string.calc).toString());
        this.dialogBar.setCancelable(false);
        this.dialogBar.setIndeterminate(true);
        this.dialogBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.dialogBar.show();

        // Procedo con la funzione per l'avvio degli AsyncTask
        drawExpression();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Aggiungo le icone al menù
        View view = inflater.inflate(R.layout.fragment_app_bar, container);
        Toolbar toolbar = view.findViewById(R.id.tb_func);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getText(R.string.grafico).toString());
        setHasOptionsMenu(true);

        // Imposto i vari elementi dell'interfaccia grafica
        chart = view.findViewById(R.id.chart);

        // Ottengo le funzioni passate dalla main Activity
        function1 = getArguments().getString("function1");
        function2 = getArguments().getString("function2");
        estremoA = getArguments().getFloat("estremoA");
        estremoB = getArguments().getFloat("estremoB");

        return view;
    }

    public void getValueBack(ArrayList<Entry> resultList, String functionName, float maxX, float maxY, float minX, float minY){

        // Se c'è stato un errore nel calcolo dei valori numerici, chiudo il Fragment
        if (resultList == null) {
            dismiss();
            return;
        }

        wewe.add(functionName);
        wewe.add(maxX);
        wewe.add(maxY);
        wewe.add(minX);
        wewe.add(minY);

        ArrayList<Entry> max = new ArrayList<>();
        ArrayList<Entry> min = new ArrayList<>();
        max.add(new Entry(maxX, maxY));
        min.add(new Entry(minX, minY));

        LineDataSet lineDataSet = new LineDataSet(resultList, functionName);
        LineDataSet lineDataSet_max_c = new LineDataSet(max, "max");
        LineDataSet lineDataSet_min_c =  new LineDataSet(min,  "min");

        lineDataSet_max_c.setColor(Color.BLACK);
        lineDataSet_min_c.setColor(Color.GREEN);
        lineDataSet_max_c.setDrawCircles(true);
        lineDataSet_min_c.setDrawCircles(true);
        lineDataSet_max_c.setCircleColor(Color.BLACK);
        lineDataSet_min_c.setCircleColor(Color.GREEN);
        lineDataSet_max_c.setDrawValues(true);  // Disegno i valori di max e min
        lineDataSet_min_c.setDrawValues(true);

        lineDataSet.setColor(Color.RED);
        lineDataSet.setDrawCircles(false);  // Disattivo i cerchi sui vari punti
        lineDataSet.setDrawValues(false);

        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet_max_c);            // Aggiunngo massimo e minimo
        dataSets.add(lineDataSet_min_c);

        // Controllo se ci sono altre funzioni di cui dover calcolare i valori numerici,
        // in caso non ce ne siano altre procedo alla creazione del grafico
        toPlot = toPlot - 1;
        if (toPlot == 0){
            lineDataSet.setColor(Color.BLUE);
            plotGraph();
        }
    }

    // Specifica il tipo di DataClass che verrà passata al grafico

    private void drawExpression() {

        dataSets = new ArrayList<>();  //private ArrayList<ILineDataSet> dataSets;

        // Imposto il numero di funzioni di cui devo disegnare il grafico
        if (function1 != null && function2 != null){
            toPlot = 2;
        } else {
            toPlot = 1;
        }

        // Faccio partire gli Async task per il calcolo dei valori
        if (function1 != null) {
            new TestAsyncTask(context, function1, estremoA, estremoB, precision, this.dialogBar, this).execute();
        }

        if (function2 != null) {
            new TestAsyncTask(context, function2, estremoA, estremoB, precision, this.dialogBar, this).execute();
        }
    }

    // Procedo con la creazione del grafico
    private void plotGraph() {

        LineData lineData = new LineData(dataSets);

        chart.setData(lineData);
        chart.setPinchZoom(true);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineColor(Color.BLACK);
        yAxis.setZeroLineWidth(1.5f);
        chart.getAxisRight().setEnabled(false);
        chart.getData().setHighlightEnabled(false);
        chart.setScaleX(1.0f);
        chart.setScaleY(1.0f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDoubleTapToZoomEnabled(false);

        // Nascondo la ProgressBar
        this.dialogBar.dismiss();

        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getText(R.string.graphInfo).toString());

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");

                String message = context.getText(R.string.function) + ": " + wewe.get(0) + "\n\n" +
                        context.getText(R.string.max).toString()+": \n" + "\t X: " + decimalFormat.format(wewe.get(1)) + "\t Y: " + decimalFormat.format(wewe.get(2))
                                + "\n"+ context.getText(R.string.min).toString()+ ": \n" + "\t X: " + decimalFormat.format(wewe.get(3)) + "\t Y: " + decimalFormat.format(wewe.get(4));

                if ((wewe.size() > 5)){
                    message = message + "\n\n" + context.getText(R.string.function) + ": " + wewe.get(5) + "\n\n" + context.getText(R.string.max).toString()+": \n" +
                            "\t X: " + decimalFormat.format(wewe.get(6)) + "\t Y: " +decimalFormat.format(wewe.get(7))
                            + "\n"+ context.getText(R.string.min).toString()+ ": \n" + "\t X: " + decimalFormat.format(wewe.get(8)) + "\t Y: " + decimalFormat.format(wewe.get(9));
                }



                alertDialog.setMessage(message);


                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

                float tappedX = me.getX();
                float tappedY = me.getY();
                MPPointD point = chart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(tappedX, tappedY);
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");


                Toast.makeText(context, "(x,y) = ( " + decimalFormat.format(point.x) + " , " + decimalFormat.format(point.y) + " )", Toast.LENGTH_LONG).show();

                //Stampo in un toast le coordinate del punto cliccato
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    // Permessi necessari per l'intent della condivisione e per il salvataggio del grafico in galleria
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
                System.out.println("Ouuu");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (requested) {
                        case 0:
                            shareGraph();
                            return;
                        case 1:
                            saveTempBitmap(chart.getChartBitmap());
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.noExternalPermission), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // Condividi grafico

    private void shareGraph() {
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requested = 0;
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        i.putExtra(Intent.EXTRA_STREAM, getImageUri(context, chart.getChartBitmap()));
        try {
            startActivity(Intent.createChooser(i, getText(R.string.chooseApp).toString()));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // Salva immagine

    private void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        }else {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requested = 1;
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requested = 1;
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/graphImage");
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, new String[]{file.getName()}, null);
            out.close();
            Toast.makeText(context,getText(R.string.img_in_gallery).toString(),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Creazione del menù
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        Menu menuList = menu;
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                shareGraph();
                return true;

            case R.id.save:
                saveTempBitmap(chart.getChartBitmap());
                return true;

            case R.id.close:
                dismiss();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface){
        this.dialogBar.dismiss();
    }




}