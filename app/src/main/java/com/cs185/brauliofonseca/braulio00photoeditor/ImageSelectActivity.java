package com.cs185.brauliofonseca.braulio00photoeditor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.ActionMenuView;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import com.almeros.android.multitouch.BaseGestureDetector;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.almeros.android.multitouch.ShoveGestureDetector;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class ImageSelectActivity extends AppCompatActivity implements View.OnTouchListener {

    private Context mContext;
    private ImageView image_button;
    private ImageView holder_view;
    private Toolbar bottomToolbar;
    private Uri selected_image_uri;
    public static final int RESULT_GALLERY = 0;
    public static final String DEBUG_GALLERY_CALLBACK = "DEBUG GALLERY CALLBACK";
    private int edited_count = 0;
    private ArrayList<Transformation> transform_applied;
    private BlurTransformation blur_transform;
    private GrayscaleTransformation gray_transform;
    private CropCircleTransformation crop_transform;
    private int array_list_index = 0;

    private Matrix mMatrix = new Matrix();
    private ScaleGestureDetector mScaleDetector;
    private ShoveGestureDetector mShoveDetector;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;
    private int mImageHeight;
    private int mImageWidth;
    private int mAlpha = 255;
    private float mScaleFactor = 1.f;
    private float mRotationDegree = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;
    private int image_has_been_selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        mContext = this;
        gray_transform = new GrayscaleTransformation();
        blur_transform = new BlurTransformation(mContext);
        crop_transform = new CropCircleTransformation();



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
        final float screenWidth = display.getWidth();
        final float screenHeight = display.getHeight();



        // Top toolbar implementation
        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(top_toolbar);

        // Overwrite functions for the ImageButton view
        image_button = (ImageView) findViewById(R.id.start_image);
        image_button.setImageResource(R.drawable.default_screen);
//        mImageHeight = image_button.getDrawable().getIntrinsicHeight();
//        mImageWidth = image_button.getDrawable().getIntrinsicWidth();
//        final float scale = Math.max(screenHeight/mImageHeight, screenWidth/mImageWidth);
        image_button.setOnTouchListener(this);
        mMatrix.postScale(mScaleFactor, mScaleFactor);
        image_button.setImageMatrix(mMatrix);


        // Overwrite functions for the Toolbar
        bottomToolbar = (Toolbar) findViewById(R.id.bottom_bar);
        bottomToolbar.inflateMenu(R.menu.botton_bar_menu);
        bottomToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.grayscale_selection:
                        Log.d(DEBUG_GALLERY_CALLBACK, "Entered grayscale selection");
                        if (image_has_been_selected == 1) {
                            transform_applied.add(gray_transform);
                            Iterator<Transformation> iterator = transform_applied.iterator();
                            Picasso.with(mContext).load(selected_image_uri)
                                    .transform(transform_applied)
                                    .into(image_button);
                        }
                        return true;
                    case R.id.blur_selection:
                        Log.d(DEBUG_GALLERY_CALLBACK, "Entered blur selection");
                        if (image_has_been_selected == 1) {
                            transform_applied.add(blur_transform);
                            Picasso.with(mContext).load(selected_image_uri)
                                    .transform(transform_applied)
                                    .into(image_button);
                        }
                        return true;
                    case R.id.circle_crop_selection:
                        Log.d(DEBUG_GALLERY_CALLBACK, "Entered circle crop selection");
                        if (image_has_been_selected == 1) {
                            transform_applied.add(crop_transform);
                            Picasso.with(mContext).load(selected_image_uri)
                                    .transform(transform_applied)
                                    .into(image_button);
                        }
                        return true;

                }
                return true;
            }
        });


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
                transform_applied = new ArrayList();
                image_has_been_selected = 0;
                executePhotoGalleryIntent();
                return true;
            case R.id.revert_selection:
                return true;
            case R.id.save_selection:
                // Save the current ImageView to file system
                saveCurrentImageView(image_button);
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
                    image_has_been_selected = 1;
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
        float scaledImageCenterX = (mImageWidth*mScaleFactor);
        float scaledImageCenterY = (mImageHeight*mScaleFactor);


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
        image_has_been_selected = 0;
        Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery_intent, RESULT_GALLERY);
    }


    // Helper function for retrieving path of image URI
    private void getImageSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Log.d(DEBUG_GALLERY_CALLBACK, "Within the getImageSize function");
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        mImageHeight = options.outHeight;
        mImageWidth = options.outWidth;
    }

    // Function to save the current ImageView
    private void saveCurrentImageView(ImageView imageview) {

        String filename = "PhotoEditor_" + Integer.toString(edited_count);
        edited_count += 1;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ File.separator + "PhotoEditor";
        File imageFolder = new File(path);
        if (!imageFolder.exists()) {
            Log.d(DEBUG_GALLERY_CALLBACK, "Did not create new folder");
            imageFolder.mkdirs();
        }

        File image = null;
        image = new File(imageFolder,filename);
        try {
            image.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(image);
        } catch (FileNotFoundException e) {
            Log.d(DEBUG_GALLERY_CALLBACK, "Did not create the output stream");
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(imageview.getWidth(), imageview.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageview.draw(canvas);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();

    }

}
