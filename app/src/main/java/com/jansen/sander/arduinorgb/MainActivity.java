package com.jansen.sander.arduinorgb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import com.jansen.sander.arduinorgb.databinding.ActivityMainBinding;
import com.jansen.sander.arduinorgb.databinding.ContentMainBinding;

public class MainActivity extends AppCompatActivity {
    private final static int VIBRATION_TIME = 100;

    private SharedPreferences sharedPref;

    private ActivityMainBinding mainBinding;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bindButtons();
    }

    protected void onStart() {
        super.onStart();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }

    private void bindButtons(){
        mainBinding.contentMain.fabBRUP.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBRDO.setOnClickListener(fabListener);
        mainBinding.contentMain.fabOFF.setOnClickListener(fabListener);
        mainBinding.contentMain.fabON.setOnClickListener(fabListener);

        mainBinding.contentMain.fabRED.setOnClickListener(fabListener);
        mainBinding.contentMain.fabGREEN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabWHITE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabORANGE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabDARKYELLOW.setOnClickListener(fabListener);
        mainBinding.contentMain.fabYELLOW.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSTRAWYELLOW.setOnClickListener(fabListener);

        mainBinding.contentMain.fabPEAGREEN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabCYAN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabLIGHTBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSKYBLUE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabDARKBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabDARKPINK.setOnClickListener(fabListener);
        mainBinding.contentMain.fabPINK.setOnClickListener(fabListener);
        mainBinding.contentMain.fabPURPLE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabFLASH.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSTROBE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabFADE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSMOOTH.setOnClickListener(fabListener);

        mainBinding.contentMain.fabDelay.setOnClickListener(fabListener);
        mainBinding.contentMain.fabColor.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBeat.setOnClickListener(fabListener);
    }

    protected OnClickListener fabListener = new FloatingActionButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(sharedPref.getBoolean(SettingsActivity.ENABLE_HAPTIC_FEEDBACK, true)) {
                vibrator.vibrate(VIBRATION_TIME);
            }
        }
    };

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

        return super.onOptionsItemSelected(item);
    }
}
