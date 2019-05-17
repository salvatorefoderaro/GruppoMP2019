package it.iloger.wallpaper.MyActivities;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import it.iloger.wallpaper.R;

public class CameraWallpaper extends AppCompatActivity {

    private static final int CAMERA_PHOTO = 111;
    private static final String[] READ_EXTERNAL_STORAGE_PERMISSION =
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String[] WRITE_EXTERNAL_STORAGE_PERMISSION =
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Uri imageToUploadUri;
    private int gotit = 0;

    ImageView iv_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActivityCompat.requestPermissions(CameraWallpaper.this, READ_EXTERNAL_STORAGE_PERMISSION, 1);
        ActivityCompat.requestPermissions(CameraWallpaper.this, WRITE_EXTERNAL_STORAGE_PERMISSION, 1);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Button btt_camera = findViewById(R.id.btt_camera);
        System.out.println(btt_camera);
        Button btt_setWallpaper = findViewById(R.id.btt_setWallpaper);

        iv_picture = findViewById(R.id.iv_picture);

        btt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCameraImage();
            }
        });

        btt_setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gotit==0){
                    Toast.makeText(
                            getApplicationContext(),
                            getText(R.string.noImage).toString(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(getBitmap(imageToUploadUri.getPath()));
                    Toast.makeText(
                            getApplicationContext(),
                            getText(R.string.wallpaperSet).toString(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void captureCameraImage() {

        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        imageToUploadUri = Uri.fromFile(f);
        startActivityForResult(chooserIntent, CAMERA_PHOTO);
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in;
        try {
            int desiredMinimumWidth = getWallpaperDesiredMinimumWidth();
            int desiredMinimumHeight = getWallpaperDesiredMinimumHeight();
            final int IMAGE_MAX_SIZE = desiredMinimumWidth * desiredMinimumHeight;
            in = getContentResolver().openInputStream(uri);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);

            assert in != null;
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(
                    "",
                    "scale = "
                            + scale
                            + ", orig-width: "
                            + o.outWidth
                            + ", orig-height: "
                            + o.outHeight
            );

            Bitmap b;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                assert b != null;
                int height = b.getHeight();
                int width = b.getWidth();

                Log.d(
                        "",
                        "1th scale operation dimenions - width: "
                                + width
                                + ", height: "
                                + height
                );

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap =
                        Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }

            assert in != null;
            in.close();

            Log.d("",
                    "bitmap size - width: "
                            + b.getWidth()
                            + ", height: "
                            + b.getHeight()
            );

            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
            if(imageToUploadUri != null){
                Uri selectedImage = imageToUploadUri;
                Bitmap reducedSizeBitmap = getBitmap(selectedImage.getPath());
                if(reducedSizeBitmap != null){
                    iv_picture.setImageBitmap(reducedSizeBitmap);
                }else{
                    gotit = 0;
                    Toast.makeText(
                            this,
                            getText(R.string.capturingError).toString(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }else{
                gotit = 0;
                Toast.makeText(
                        this,
                        getText(R.string.capturingError).toString(),
                        Toast.LENGTH_LONG
                ).show();
            }
            gotit = 1;
        }
    }
}