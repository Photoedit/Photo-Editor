package com.cs185.brauliofonseca.braulio00photoeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import com.almeros.android.multitouch.BaseGestureDetector;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.almeros.android.multitouch.ShoveGestureDetector;

import java.io.File;

public class ImageSelectActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView image_button;
    private Toolbar bottomToolbar;
    private Uri selected_image_uri;
    public static final int RESULT_GALLERY = 0;
    public static final String DEBUG_GALLERY_CALLBACK = "DEBUG GALLERY CALLBACK";

    private Matrix mMatrix = new Matrix();
    private ScaleGestureDetector mScaleDetector;
    private ShoveGestureDetector mShoveDetector;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;
    private int mImageHeight;
    private int mImageWidth;
    private int mAlpha = 255;
    private float mScaleFactor = .4f;
    private float mRotationDegree = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;

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

        // Determine the center of the screen to center 'earth'
        Display display = getWindowManager().getDefaultDisplay();
        mFocusX = display.getWidth()/2f;
        mFocusY = display.getHeight()/2f;


        // Top toolbar implementation
        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(top_toolbar);

        // Overwrite functions for the ImageButton view
        image_button = (ImageView) findViewById(R.id.start_image);
        image_button.setImageResource(R.drawable.default_screen);
        image_button.setOnTouchListener(this);
        image_button.setImageMatrix(mMatrix);

        // Overwrite functions for the Toolbar
        bottomToolbar = (Toolbar) findViewById(R.id.bottom_bar);
        bottomToolbar.inflateMenu(R.menu.botton_bar_menu);

        // Initialize the motion detector variables
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        mMoveDetector = new MoveGestureDetector(getApplicationContext(), new MovingListener());
        mShoveDetector = new ShoveGestureDetector(getApplicationContext(), new ShoveListener());
        mRotateDetector = new RotateGestureDetector(getApplicationContext(), new RotationListener());

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
                    // Get the information on the imported image
                    getImageSize(selected_image_uri);
                    image_button.setImageURI(selected_image_uri);
                    Log.d(DEBUG_GALLERY_CALLBACK, "Reached inner conditional");

                }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mShoveDetector.onTouchEvent(event);
        mMoveDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);

        // View is scaled and translated by matrix, so scale and translate initially
        float scaledImageCenterX = (mImageWidth*mScaleFactor)/2;
        float scaledImageCenterY = (mImageHeight*mScaleFactor)/2;


        mMatrix.reset();
        mMatrix.postScale(mScaleFactor, mScaleFactor);
        mMatrix.postRotate(mRotationDegree, scaledImageCenterX, scaledImageCenterY);
        mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);

        ImageView view = (ImageView) v;
        view.setImageMatrix(mMatrix);
        view.setAlpha(mAlpha);
        return true;
    }

    // Private class for the implementation of scaling
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            return true;
        }
    }

    // Private class for extension of rotation
    private class RotationListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegree -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    // Private class for extension of moving
    private class MovingListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX += d.x;
            mFocusY += d.y;
            return true;
        }
    }

    // Private class for extension of shoving
    private class ShoveListener extends ShoveGestureDetector.SimpleOnShoveGestureListener {
        @Override
        public boolean onShove(ShoveGestureDetector detector) {
            mAlpha += detector.getShovePixelsDelta();
            if (mAlpha > 255)
                mAlpha = 255;
            else if (mAlpha < 0)
                mAlpha = 0;

            return true;
        }
    }


    // Function for bringing up the gallery intent
    public void executePhotoGalleryIntent() {
        Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery_intent, RESULT_GALLERY);
    }


    // Helper function for retrieving path of image URI
    private void getImageSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        mImageHeight = options.outHeight;
        mImageWidth = options.outWidth;
    }

}
