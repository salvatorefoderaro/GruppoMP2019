package com.example.testgrafico.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.testgrafico.MathHelper.MaxMin_Singleton;
import com.example.testgrafico.R;
import com.example.testgrafico.AsyncTask.TestAsyncTask;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class FragmentDrawGraph extends DialogFragment {

    private String function1 = null;
    private String function2 = null;
    private int estremoA;
    private int estremoB;
    private float precision = 0.1f;
    private Context context;
    private SeekBar seekBar;
    private Chart<LineData> chart;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private Toolbar toolbar;
    private Menu menuList;
    private TestAsyncTask task;
    private ProgressDialog dialogBar;

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
        this.dialogBar.setTitle("Aspetta...");
        this.dialogBar.setMessage("Sto calcolando il valore della funzione...");
        this.dialogBar.setCancelable(false);
        this.dialogBar.setIndeterminate(true);
        this.dialogBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.dialogBar.show();

        // Se commento la linea di codice 91, la barra parte, altrimenti no!
        drawExpression();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Aggiungo le icone al menù
        View view = inflater.inflate(R.layout.fragment_app_bar, container);
        toolbar = view.findViewById(R.id.tb_func);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Grafico di funzione");
        setHasOptionsMenu(true);

        // Imposto i vari elementi dell'interfaccia grafica
        chart = view.findViewById(R.id.chart);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(0);

        // Imposto le funzioni per la seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                precision = 0.1f - ((seekBar.getProgress() + 0.1f) * 0.01f);
                drawExpression();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Ottengo le funzioni passate dalla main Activity
        function1 = getArguments().getString("function1");
        function2 = getArguments().getString("function2");
        estremoA = getArguments().getInt("estremoA");
        estremoB = getArguments().getInt("estremoB");

        return view;
    }

    public void drawExpression() {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<ILineDataSet> draw_Max = new ArrayList<>();
        ArrayList<ILineDataSet> draw_Min = new ArrayList<>();

        // Controllo quante funzioni ho ricevuto dalla Main activity e, per le funzioni
        // != null, ottengo i valori numerici da inserire nel grafico
        if (function1 != null) {
            ArrayList<Entry> entries1 = null;
            try {
                task = (TestAsyncTask) new TestAsyncTask(context, function1, estremoA, estremoB, precision, this.dialogBar).execute();
                entries1 = task.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (entries1 == null) {
                dismiss();
                return;
            }

            ArrayList<Entry> max = MaxMin_Singleton.getInstance().getValues().get(0);
            ArrayList<Entry> min = MaxMin_Singleton.getInstance().getValues().get(1);

            LineDataSet dataSet = new LineDataSet(entries1, function1);
            LineDataSet max_c = new LineDataSet(max, "max");
            LineDataSet min_c = new LineDataSet(min, "min");

            max_c.setColor(Color.BLACK);
            min_c.setColor(Color.GREEN);
            max_c.setDrawCircles(true);
            min_c.setDrawCircles(true);
            max_c.setCircleColor(Color.BLACK);
            min_c.setCircleColor(Color.GREEN);
            max_c.setDrawValues(false);
            min_c.setDrawValues(false);
            draw_Max.add(max_c);
            draw_Min.add(min_c);

            dataSet.setColor(Color.RED);
            dataSet.setDrawCircles(false);  //Disattivo i cerchi sui vari punti
            dataSet.setDrawValues(false);
            dataSets.add(dataSet);
            dataSets.add(max_c);            //Aggiunngo massimo e minimo
            dataSets.add(min_c);
        }

        if (function2 != null) {
            ArrayList<Entry> entries2 = null;
            try {
                entries2 = new TestAsyncTask(context, function1, estremoA, estremoB, precision, this.dialogBar).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (entries2 == null) {
                dismiss();
                return;
            }

            ArrayList<Entry> max = MaxMin_Singleton.getInstance().getValues().get(0);
            ArrayList<Entry> min = MaxMin_Singleton.getInstance().getValues().get(1);

            LineDataSet dataSet1 = new LineDataSet(entries2, function2);
            LineDataSet max_c = new LineDataSet(max, "max");
            LineDataSet min_c = new LineDataSet(min, "min");

            max_c.setColor(Color.BLACK);
            min_c.setColor(Color.GREEN);
            max_c.setDrawCircles(true);
            min_c.setDrawCircles(true);
            max_c.setCircleColor(Color.BLACK);
            min_c.setCircleColor(Color.GREEN);
            max_c.setDrawValues(false);
            min_c.setDrawValues(false);
            draw_Max.add(max_c);
            draw_Min.add(min_c);

            dataSet1.setDrawCircles(false);  //Disattivo i cerchi sui vari punti
            dataSet1.setDrawValues(false);
            dataSets.add(dataSet1);
            dataSets.add(max_c);            //Aggiunngo massimo e minimo
            dataSets.add(min_c);
        }

        // Popolo il grafico e lo mostro
        LineData lineData = new LineData(dataSets);

        /*LineData draw_max = new LineData(max);
        LineData draw_min = new LineData(min);*/

        chart.setData(lineData);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
        chart.setScaleX(1.0f);
        chart.setScaleY(1.0f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        /*chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

                System.out.println(chart.getXAxis().getXOffset());



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
        });*/

    }

    // Permessi necessari per l'intent della condivisione e per il salvataggio del grafico in galleria
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "You denied write external storage permission.", Toast.LENGTH_LONG).show();
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
            startActivity(Intent.createChooser(i, "Seleziona un applicazione ..."));
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
            Toast.makeText(context,"Immagine salvata correttamente in galleria!",Toast.LENGTH_SHORT).show();
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


}