package com.jansen.sander.arduinorgb;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

import com.jansen.sander.arduinorgb.databinding.ActivityMainBinding;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static int VIBRATION_TIME = 100;

    private SharedPreferences sharedPref;

    private ActivityMainBinding mainBinding;
    private Vibrator vibrator;

    private Snackbar snackbar;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);

        bindButtons();
        bindColorSliders();
    }

    protected void onStart() {
        super.onStart();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBluetooth();
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

        return super.onOptionsItemSelected(item);
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

    private void bindColorSliders(){
        mainBinding.contentMain.sliderRed.setOnSeekBarChangeListener(slideListener);
        mainBinding.contentMain.sliderGreen.setOnSeekBarChangeListener(slideListener);
        mainBinding.contentMain.sliderBlue.setOnSeekBarChangeListener(slideListener);
    }

    private void vibrate(){
        if(sharedPref.getBoolean(SettingsActivity.ENABLE_HAPTIC_FEEDBACK, true)) {
            vibrator.vibrate(VIBRATION_TIME);
        }
    }

    protected OnClickListener fabListener = new FloatingActionButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (v.getContentDescription() != null){
                Log.d("IR-VALUE", v.getContentDescription().toString());
            }
            vibrate();
        }
    };

    protected SeekBar.OnSeekBarChangeListener slideListener = new SeekBar.OnSeekBarChangeListener() {
        int red, green, blue;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            red     = mainBinding.contentMain.sliderRed.getProgress();
            green   = mainBinding.contentMain.sliderGreen.getProgress();
            blue    = mainBinding.contentMain.sliderBlue.getProgress();

            Log.d("COLOR", String.format(getResources().getString(R.string.color_value), red, green, blue));
        }
    };


    private void listBluetoothDevices(){

    }

    private void initBluetooth(){
        if (checkBluetoothCompatibility()){
            enableBluetooth();
        } else {
            snackbar.setText("Bluetooth not supported").show();
        }
    }

    private boolean checkBluetoothCompatibility(){
        if (mBluetoothAdapter != null){
            return true;
        }
        return false;
    }

    private void enableBluetooth(){
        final int REQUEST_ENABLE_BT = 1;
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void enableDiscoverability(){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    private void queryPairedDevives(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    }

    private void discoverBluetoothDevices(){

    }
}
