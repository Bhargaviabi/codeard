package com.jansen.sander.arduinorgb;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jansen.sander.arduinorgb.databinding.ActivityMainBinding;
import com.jansen.sander.arduinorgb.databinding.ContentMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    //private ContentMainBinding contentMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the content view (replacing `setContentView`)
        //contentMainBinding = DataBindingUtil.setContentView(this, R.layout.content_main);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Store the field now if you'd like without any need for casting
        //TextView testView = mainBinding.textView2;


        //setContentView(R.layout.activity_main);
        mainBinding.textView2.setText("test");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_colors) {
            startActivity(new Intent(this, ColorActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
