package com.cs185.brauliofonseca.braulio00photoeditor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;


public class ImageSelectActivity extends AppCompatActivity {

    private ImageButton image_button;
    private Toolbar bottomToolbar;

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
}
