package com.cs185.brauliofonseca.braulio00photoeditor;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;


public class ImageSelectActivity extends AppCompatActivity {

    private ImageButton image_button;
    private Toolbar bottomToolbar;
    public static final int RESULT_GALLERY = 0;
    public static final String DEBUG_GALLERY_CALLBACK = "DEBUG GALLERY CALLBACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        // Top toolbar implementation
        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(top_toolbar);

        // Overwrite functions for the ImageButton view
        image_button = (ImageButton) findViewById(R.id.start_image_selection);
        image_button.setImageResource(R.drawable.default_screen);
        image_button.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePhotoGalleryIntent();
            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selected_image_uri = data.getData();
                    String image_path = getPathFromUri(selected_image_uri);


                }
        }


        Log.d(DEBUG_GALLERY_CALLBACK, "Reached callback function");
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
