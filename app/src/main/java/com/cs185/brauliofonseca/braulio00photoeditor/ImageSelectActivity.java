package com.cs185.brauliofonseca.braulio00photoeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;


public class ImageSelectActivity extends AppCompatActivity {

    private ImageView image_button;
    private Toolbar bottomToolbar;
    private Uri selected_image_uri;
    public static final int RESULT_GALLERY = 0;
    public static final String DEBUG_GALLERY_CALLBACK = "DEBUG GALLERY CALLBACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        // Setup read and write permission for app
//        int write_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (write_permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
//
//        int read_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (read_permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }

        // Top toolbar implementation
        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(top_toolbar);

        // Overwrite functions for the ImageButton view
        image_button = (ImageView) findViewById(R.id.start_image);
        image_button.setImageResource(R.drawable.default_screen);
//        image_button.setOnClickListener(new ImageButton.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                executePhotoGalleryIntent();
//            }
//        });

        // Overwrite functions for the Toolbar
        bottomToolbar = (Toolbar) findViewById(R.id.bottom_bar);
        bottomToolbar.inflateMenu(R.menu.botton_bar_menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_bar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gallery_selection:
                // On selection of gallery button
                executePhotoGalleryIntent();
                return true;
            case R.id.revert_selection:
                return true;
            case R.id.save_selection:
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GALLERY:
                if (resultCode == RESULT_OK) {
                    selected_image_uri = data.getData();
                    image_button.setImageURI(selected_image_uri);
                    Log.d(DEBUG_GALLERY_CALLBACK, "Reached inner conditional");

                }
        }

    }


    // Function for bringing up the gallery intent
    public void executePhotoGalleryIntent() {
        Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery_intent, RESULT_GALLERY);
    }


    // Helper function for retrieving path of image URI
    public String getPathFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA );
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

}
