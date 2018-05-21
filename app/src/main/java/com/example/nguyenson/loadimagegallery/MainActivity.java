package com.example.nguyenson.loadimagegallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private GalleyAdapter mGalleyAdapter;
    private ArrayList<String> mSaveData;
    static final int NUMBER_COLUMN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSaveData = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_galley);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUMBER_COLUMN);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mGalleyAdapter = new GalleyAdapter(getAllShownImagesPath(this), this);
        mRecyclerView.setAdapter(mGalleyAdapter);
    }

    private ArrayList<Bitmap> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Bitmap bmp;
        Cursor cursor;
        int column_index_data;
        ArrayList<Bitmap> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        final String orderBy = MediaStore.Images.Media._ID + " DESC";
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                               MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                               MediaStore.Images.Media._ID};

        cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            File f = new File(absolutePathOfImage);
            bmp = decodeFile(f, 300, 800);
            listOfAllImages.add(bmp);
            mSaveData.add(absolutePathOfImage);
            }
        return listOfAllImages;
    }

    public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
