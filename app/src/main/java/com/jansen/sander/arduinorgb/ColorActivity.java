package com.jansen.sander.arduinorgb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jansen.sander.arduinorgb.databinding.ActivityColorBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColorActivity extends AppCompatActivity {

    private ActivityColorBinding colorBinding;
    
    private int cid;
    private CustomColorDataAdapter mAdapter;
    private SwipeController swipeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorBinding = DataBindingUtil.setContentView(this, R.layout.activity_color);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getAllSavedColors();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_drop_database) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.deleteAll).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.database_options, menu);
        return true;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    new DeleteAllColorsTask().execute((Void)null);
                    int size = mAdapter.getCustomColors().size();
                    mAdapter.getCustomColors().clear();
                    mAdapter.notifyItemRangeRemoved(0, size);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void getAllSavedColors(){
        new GetAllSavedColorsTask().execute((Void)null);
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = colorBinding.contentColor.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                cid = mAdapter.getCid(position);
                new DeleteSavedColorsTask().execute((Void)null);

                mAdapter.getCustomColors().remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
            @Override
            public void onLeftClicked(int position) {
                cid = mAdapter.getCid(position);
                new GetSavedColorByIdTask().execute((Void)null);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    public class GetAllSavedColorsTask extends AsyncTask<Void, Void, List<CustomColor>> {
        private List<CustomColor> allSavedColors = new ArrayList<>();

        @Override
        protected List<CustomColor> doInBackground(Void... voids) {
            allSavedColors = AppDatabase.getInstance(getApplicationContext()).colorDB().getStoredColors();
            return allSavedColors;
        }

        @Override
        protected void onPostExecute(final List<CustomColor> allSavedColors) {
            mAdapter = new CustomColorDataAdapter(allSavedColors);
            setupRecyclerView();
        }
    }

    public class GetSavedColorByIdTask extends AsyncTask<Void, Void, Boolean > {
        @Override
        protected Boolean doInBackground(Void... voids) {
            List<CustomColor> colorByCid = AppDatabase.getInstance(getApplicationContext()).colorDB().colorByCid(cid);
            for (CustomColor colorX : colorByCid){
                MainActivity.getMainActivityBinding().contentMain.sliderRed.setProgress(colorX.getRed());
                MainActivity.getMainActivityBinding().contentMain.sliderGreen.setProgress(colorX.getGreen());
                MainActivity.getMainActivityBinding().contentMain.sliderBlue.setProgress(colorX.getBlue());
                try {
                    MainActivity.write(String.format(MainActivity.getContext().getResources().getString(R.string.colorMessage),colorX.getRed(), colorX.getGreen(), colorX.getBlue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                finish();
            } else {
                //mySnackbar.setText(R.string.errorLoad).show();
            }
        }
    }

    public class DeleteSavedColorsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase.getInstance(getApplicationContext()).colorDB().deleteById(cid);
            return true;
        }
    }

    public class DeleteAllColorsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase.getInstance(getApplicationContext()).colorDB().reset();
            return true;
        }
    }
}
