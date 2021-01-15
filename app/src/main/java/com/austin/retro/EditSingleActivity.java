package com.austin.retro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.austin.retro.database.AppDatabase;
import com.austin.retro.database.RawObject;
import com.austin.retro.fragments.ForegroundFragment;

import java.util.ArrayList;

public class EditSingleActivity extends Activity {

    SeekBar sizeSlider;
    SeekBar speedSlider;
    SeekBar angleSlider;
    TextView sizeText;
    TextView speedText;
    TextView angleText;
    ImageView imageView;
    Button test;
    int position;
    RawObject copy;

    public EditSingleActivity() {

        //EditSingleActivity.EnterEditSingleAsync enterEditSingleAsync = new EditSingleActivity.EnterEditSingleAsync ();
        //enterEditSingleAsync.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.position = savedInstanceState.getInt("position");

        setContentView(R.layout.obj_popup);

        sizeSlider = findViewById(R.id.sbSize);
        speedSlider = findViewById(R.id.sbSpeed);
        angleSlider = findViewById(R.id.sbAngle);
        sizeText = findViewById(R.id.sizeText);
        speedText = findViewById(R.id.speedText);
        angleText = findViewById(R.id.angleText);
        imageView = findViewById(R.id.settingsImg);
        test = findViewById(R.id.testbutton);

        sizeSlider.setProgress(copy.getSize());
        speedSlider.setProgress(copy.getSpeed());
        angleSlider.setProgress(copy.getAngle());
        sizeText.setText(copy.getSize());
        speedText.setText(copy.getSpeed());
        angleText.setText(copy.getAngle());

        angleSlider.setMax(359);
        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String s;
                switch (seekBar.getId()){

                    case (R.id.sbSize):
                        //sizeText.setText(view.getContext().getResources().getString(R.string.objSize, progress));
                        s = String.valueOf(progress) + "%";
                        sizeText.setText(s);
                        break;
                    case (R.id.sbSpeed):
                        s = String.valueOf(progress) + "%";
                        speedText.setText(s);
                        break;
                    case (R.id.sbAngle):
                        s = String.valueOf(progress) + "°";
                        angleText.setText(s);
                        break;

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        sizeSlider.setOnSeekBarChangeListener(seekBarListener);
        speedSlider.setOnSeekBarChangeListener(seekBarListener);
        angleSlider.setOnSeekBarChangeListener(seekBarListener);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ForegroundFragment.class);
        startActivity(i);
    }

    /*private static class EnterEditSingleAsync extends AsyncTask<Void, Void, RawObject> {

        private View view;
        private Context context;
        private AppDatabase database;
        private EditSingleActivity activity;
        private int position;

        EnterEditSingleAsync(View v, Context c, AppDatabase db, EditSingleActivity a, int position){
            this.view = ;
            this.context = c;
            this.database = db;
            this.activity = a;
            this.position = position;
        }

        @Override
        protected RawObject doInBackground(Void... voids) {

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();

            int ID = adapter.objects.get(position).getID();
            RawObject copy = null;

            for (int i = 0; i < rawObjects.size(); i++){
                RawObject r = rawObjects.get(i);
                Log.d("DB ID ", r.getId() + "");
                if(r.getId() == ID){

                    copy = rawObjects.get(i);
                    copy.toggleEnabled();
                    database.objectDao().removeObject(rawObjects.get(i));
                    database.objectDao().insertObject(copy);
                    adapter.objects.get(i).toggleEnabled();

                }
            }
            return copy;
        }

        @Override
        protected void onPostExecute(RawObject copy) {
            super.onPostExecute(copy);



        }
    }*/
}
