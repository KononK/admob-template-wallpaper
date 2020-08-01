package com.sakakibara.wallpaper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Gallery gallery;
    ImageView fullImage;
    Button btnSetAsWallpaper;
    public static List<Integer> imagesUrl;
    private Bitmap bitmap;
    private int presentSelectedWallpaper = 0;
    public final static int ADDED_EXTRA = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        // Load an ad into the AdMob banner view.
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        this.populateList();
        gallery = this.findViewById(R.id.gallery1);
        GridImageAdapter adapter = new GridImageAdapter(MainActivity.this, imagesUrl);
        gallery.setAdapter(adapter);

        gallery.setOnItemSelectedListener(this);

        fullImage = this.findViewById(R.id.full_image1);

        btnSetAsWallpaper = this.findViewById(R.id.btnSetAsWallpaper);

        btnSetAsWallpaper.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.btn_save:
                bitmap = BitmapFactory.decodeResource(getResources(),
                        imagesUrl.get(presentSelectedWallpaper));
                new saveEditedImage(false,false).execute();
                break;
            case R.id.btn_share:
                bitmap = BitmapFactory.decodeResource(getResources(),
                        imagesUrl.get(presentSelectedWallpaper));
                new saveEditedImage(true,false).execute();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateList(){
        imagesUrl = new ArrayList<>();
        imagesUrl.add(R.drawable.img1);
        imagesUrl.add(R.drawable.img2);
        imagesUrl.add(R.drawable.img3);
        imagesUrl.add(R.drawable.img4);
        imagesUrl.add(R.drawable.img5);
        imagesUrl.add(R.drawable.img6);
        imagesUrl.add(R.drawable.img7);
        imagesUrl.add(R.drawable.img8);
        imagesUrl.add(R.drawable.img9);
        imagesUrl.add(R.drawable.img10);
        imagesUrl.add(R.drawable.img11);
        imagesUrl.add(R.drawable.img12);
        imagesUrl.add(R.drawable.img13);
        imagesUrl.add(R.drawable.img14);
        imagesUrl.add(R.drawable.img15);
        imagesUrl.add(R.drawable.img16);
        imagesUrl.add(R.drawable.img17);
        imagesUrl.add(R.drawable.img18);
        imagesUrl.add(R.drawable.img19);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 < ADDED_EXTRA / 2)
            gallery.setSelection(imagesUrl.size() + 2, false);

        if (arg2 > imagesUrl.size() + ADDED_EXTRA / 2) {
            gallery.setSelection(4, false);
        }

        View v = gallery.getSelectedView();
        ImageView imgView = v.findViewById(R.id.gridview_image);
        int position = Integer.parseInt(""+imgView.getTag());
        fullImage.setImageDrawable(getResources().getDrawable(imagesUrl.get(position)));

        presentSelectedWallpaper = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        gallery.setSelection(3, false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSetAsWallpaper) {
            bitmap = BitmapFactory.decodeResource(getResources(), imagesUrl.get(presentSelectedWallpaper));
            new saveEditedImage(false,true).execute();
        }
    }

    class saveEditedImage extends AsyncTask<Void, Void, String> {
        boolean isShare;
        boolean _setWall;

        saveEditedImage(boolean share,boolean setWall) {
            this.isShare = share;
            this._setWall = setWall;
        }

        @Override
        protected String doInBackground(Void... params) {
            String SAVED_MEDIA_PATH = null;
            FileOutputStream out;
            try {
                SAVED_MEDIA_PATH = getOutputMediaFile().getPath();
                out = new FileOutputStream(new File(SAVED_MEDIA_PATH));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ""+SAVED_MEDIA_PATH;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                Toast.makeText(getBaseContext(), "Wallpaper Saved @\n"+result,Toast.LENGTH_LONG).show();
                if(isShare){
                    Context context = getBaseContext();
                    Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(result));
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    startActivity(Intent.createChooser(intent, "Share"));
                }
                if(this._setWall){
                    createSetAsIntent(result);
                }
            }

            super.onPostExecute(result);
        }
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("APP ERROR", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        Log.i("APP ERROR", "" + mediaFile.getPath());
        return mediaFile;
    }

    public void createSetAsIntent(String image) {
        try {
            Context context = getBaseContext();
            Uri u = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(image));
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(u,"image/*");
            intent.putExtra("mimeType", "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
