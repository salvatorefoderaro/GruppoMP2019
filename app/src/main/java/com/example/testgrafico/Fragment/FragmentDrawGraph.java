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
import android.support.v4.app.ActivityCompat;
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

import com.example.testgrafico.R;
import com.example.testgrafico.AsyncTask.TestAsyncTask;
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
    private int estremoA;
    private int estremoB;
    private float precision = 0.1f;
    private Context context;
    private LineChart chart;  //Ho cambiato il tipo di chart per avere più opzioni disponibili
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private Toolbar toolbar;
    private Menu menuList;
    private TestAsyncTask task;
    private ProgressDialog dialogBar;
    private ArrayList<ILineDataSet> dataSets;
    private int toPlot;
    private ArrayList<Object> wewe = new ArrayList<>();

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
        toolbar = view.findViewById(R.id.tb_func);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getText(R.string.grafico).toString());
        setHasOptionsMenu(true);

        // Imposto i vari elementi dell'interfaccia grafica
        chart = view.findViewById(R.id.chart);

        // Ottengo le funzioni passate dalla main Activity
        function1 = getArguments().getString("function1");
        function2 = getArguments().getString("function2");
        estremoA = getArguments().getInt("estremoA");
        estremoB = getArguments().getInt("estremoB");

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

        /*TODO
        *
        * Inserire label massimo e minimo
        * Funzioni acos, atan ecc
        * Doppio tap sul grafico
        *
        *
        * */
        ArrayList<Entry> max = new ArrayList<Entry>();
        ArrayList<Entry> min = new ArrayList<Entry>();
        max.add(new Entry(maxX, maxY));
        min.add(new Entry(minX, minY));

        LineDataSet dataSet = new LineDataSet(resultList, functionName);
        LineDataSet max_c = new LineDataSet(max, "max");
        LineDataSet min_c =  new LineDataSet(min,  "min");

        max_c.setColor(Color.BLACK);
        min_c.setColor(Color.GREEN);
        max_c.setDrawCircles(true);
        min_c.setDrawCircles(true);
        max_c.setCircleColor(Color.BLACK);
        min_c.setCircleColor(Color.GREEN);
        max_c.setDrawValues(true);  // Disegno i valori di max e min
        min_c.setDrawValues(true);

        dataSet.setColor(Color.RED);
        dataSet.setDrawCircles(false);  //Disattivo i cerchi sui vari punti
        dataSet.setDrawValues(false);

        dataSets.add(dataSet);
        dataSets.add(max_c);            //Aggiunngo massimo e minimo
        dataSets.add(min_c);

        // Controllo se ci sono altre funzioni di cui dover calcolare i valori numerici,
        // in caso non ce ne siano altre procedo alla creazione del grafico
        toPlot = toPlot - 1;
        if (toPlot == 0){
            plotGraph();
        }
    }

    public void drawExpression() {

        dataSets = new ArrayList<>();

        // Imposto il numero di funzioni di cui devo disegnare il grafico
        if (function1 != null && function2 != null){
            toPlot = 2;
        } else {
            toPlot = 1;
        }

        // Faccio partire gli Async task per il calcolo dei valori
        if (function1 != null) {
            task = (TestAsyncTask) new TestAsyncTask(context, function1, estremoA, estremoB, precision, this.dialogBar, this).execute();
        }

        if (function2 != null) {
            task = (TestAsyncTask) new TestAsyncTask(context, function2, estremoA, estremoB, precision, this.dialogBar, this).execute();
        }

    }

    // Procedo con la creazione del grafico
    public void plotGraph(){

        LineData lineData = new LineData(dataSets);

        chart.setData(lineData);
        chart.setPinchZoom(true);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
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

                DecimalFormat decimalFormat = new DecimalFormat("#.00");

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
                DecimalFormat decimalFormat = new DecimalFormat("#.00");


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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, context.getResources().getString(R.string.okExternalPermission), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.noExternalPermission), Toast.LENGTH_LONG).show();
            }
        }

    }

    // Condividi grafico

    public void shareGraph() {
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // Salva immagine

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        }else {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                return;
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Creazione del menù
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menuList = menu;
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menuList.findItem(R.id.help).setVisible(false);
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

    /*public MPPointD getValuesByTouchPoint(float x, float y){

    }*/


}