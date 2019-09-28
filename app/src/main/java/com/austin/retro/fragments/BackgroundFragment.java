package com.austin.retro.fragments;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.austin.retro.ForegroundAdapter;
import com.austin.retro.ForegroundObject;
import com.austin.retro.MainActivity;
import com.austin.retro.R;
import com.austin.retro.WPService;
import com.austin.retro.database.AppDatabase;
import com.austin.retro.database.Background;
import com.austin.retro.database.RawObject;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class BackgroundFragment extends Fragment {

    View view;
    Button setColor;
    Button setImage;
    private final int RESULT_IMG = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_background, container, false);
        setHasOptionsMenu(true);

        setColor  = (Button) view.findViewById(R.id.set_color);
        setImage = (Button) view.findViewById(R.id.set_image);

        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.color_popup, null);

                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                final ColorPicker picker = (ColorPicker) popupView.findViewById(R.id.picker);
                SaturationBar saturationBar = (SaturationBar) popupView.findViewById(R.id.saturationbar);
                ValueBar valueBar = (ValueBar) popupView.findViewById(R.id.valuebar);

                picker.addSaturationBar(saturationBar);
                picker.addValueBar(valueBar);

                picker.setShowOldCenterColor(false);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Button butt = popupView.findViewById(R.id.done_button);
                butt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        int color = picker.getColor();
                        Log.d("Color:", color + "");
                        BackgroundFragment.setBackgroundAsync setBackgroundAsync = new BackgroundFragment.setBackgroundAsync(
                                view.getContext(), MainActivity.objectDB, color);
                        setBackgroundAsync.execute();
                    }
                });
            }
        });

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //then start the intent
                startActivityForResult(i, RESULT_IMG);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_IMG && resultCode == RESULT_OK) {
            // Make sure the request was successful

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = view.getContext().getContentResolver().openInputStream(imageUri);
                String filename = imageUri.getLastPathSegment();
                String imagename = new File(filename).getName();
                Log.d("n",imagename);
                BackgroundFragment.setBackgroundAsync setBackgroundAsync = new BackgroundFragment.setBackgroundAsync(
                        view.getContext(), MainActivity.objectDB, imagename);
                setBackgroundAsync.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
/*   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == RESULT_IMG && resultCode == RESULT_OK) {
           // Make sure the request was successful
           try {

               Uri image = data.getData();

               WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getContext());

               Intent i = myWallpaperManager.getCropAndSetWallpaperIntent(image);
               startActivity(i);

               BackgroundFragment.setBackgroundAsync setBackgroundAsync = new BackgroundFragment.setBackgroundAsync(
                       view.getContext(), MainActivity.objectDB, "");
               setBackgroundAsync.execute();

           } catch (Exception e) {
               e.printStackTrace();
               Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
           }
       }
       else {
           Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
       }
   }*/

    private static class setBackgroundAsync extends AsyncTask<Void, Void, Void>{

        private Context context;
        private AppDatabase database;
        private int color;
        private String fileName;
        private boolean usesImage = false;

        setBackgroundAsync (Context c , AppDatabase db, int color){
            this.context = c;
            this.database = db;
            this.color = color;
        }

        setBackgroundAsync (Context c , AppDatabase db, String fileName){
            this.context = c;
            this.database = db;
            this.fileName = fileName;
            this.usesImage = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // Removes background
            ArrayList<Background> bkgs = (ArrayList<Background>) database.backgroundDao().getBackground();

            for (int i = 0; i < bkgs.size(); i++){
                database.backgroundDao().removeBackground(bkgs.get(i));
            }

            Background background;
            if(!usesImage) {
                background = new Background(0,color);
            }
            else{
                background = new Background(0, fileName);
            }
            database.backgroundDao().insertBackground(background);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText( context, "Background Set.", Toast.LENGTH_SHORT ).show();
        }
    }
}
