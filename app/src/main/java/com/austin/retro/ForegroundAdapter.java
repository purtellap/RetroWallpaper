package com.austin.retro;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.austin.retro.database.AppDatabase;
import com.austin.retro.database.RawObject;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ForegroundAdapter extends RecyclerView.Adapter<ForegroundAdapter.MyViewHolder> {

    public ArrayList<ForegroundObject> objects = new ArrayList<>();

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView imageView;
        TextView nameView;
        TextView desc1View;
        TextView desc2View;
        TextView desc3View;
        TextView desc4View;
        CheckBox checkBox;
        TextView id;

        MyViewHolder(LinearLayout l) {
            super(l);
            layout = l;
            //id = (TextView) l.findViewById(R.id.id);
            imageView = (ImageView) l.findViewById(R.id.objImgPreview);
            desc1View = (TextView) l.findViewById(R.id.objSize);
            desc2View = (TextView) l.findViewById(R.id.objSpeed);
            desc3View = (TextView) l.findViewById(R.id.objAngle);
            checkBox = l.findViewById(R.id.isSelected);
        }
    }

    public ForegroundAdapter(View view, RecyclerView recyclerView) {
        ForegroundAdapter.getObjectsAsync getObjectsAsync = new ForegroundAdapter.getObjectsAsync(view, view.getContext(), MainActivity.objectDB, this, recyclerView);
        getObjectsAsync.execute();
    }

    @Override
    public @NonNull ForegroundAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.obj_view, parent, false);
        return new MyViewHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        ForegroundObject obj = objects.get(position);
        String sizeString = obj.getSize() + "%";
        String speedString = obj.getSpeed() + "%";
        String angleString = obj.getAngle() + "°";

        // fills single object recycle view
        holder.imageView.setImageBitmap(obj.getImage());
        holder.desc1View.setText(sizeString);
        holder.desc2View.setText(speedString);
        holder.desc3View.setText(angleString);
        holder.checkBox.setChecked(obj.isEnabled());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.checkBox.setChecked(isChecked);
                doToggleSingleAsync(holder.getAdapterPosition());
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEditSingleAsync(v, holder.getAdapterPosition());
                /*Intent intent = new Intent(v.getContext(), EditSingleActivity.class);
                intent.putExtra("position", holder.getAdapterPosition());
                v.getContext().startActivity (intent);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private void doToggleSingleAsync(int position){
        ForegroundAdapter adapter = this;
        ForegroundAdapter.ToggleSingleAsync toggleSingleAsync = new ForegroundAdapter.ToggleSingleAsync(
                MainActivity.objectDB, adapter, position);
        toggleSingleAsync.execute();
    }

    private void doEditSingleAsync(View v, int position){
        ForegroundAdapter adapter = this;
        ForegroundAdapter.EditSingleAsync editSingleAsync = new ForegroundAdapter.EditSingleAsync(v, v.getContext(),
                MainActivity.objectDB, adapter, position);
        editSingleAsync.execute();
    }

    private static class EditSingleAsync extends AsyncTask<Void, Void, RawObject>{

        private View view;
        private Context context;
        private AppDatabase database;
        private ForegroundAdapter adapter;
        private int position;

        EditSingleAsync(View v, Context c, AppDatabase db, ForegroundAdapter adapter, int position){
            this.view = v;
            this.context = c;
            this.database = db;
            this.adapter = adapter;
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

            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.obj_popup_2, null);

            SeekBar sizeSlider = popupView.findViewById(R.id.sbSize);
            SeekBar speedSlider = popupView.findViewById(R.id.sbSpeed);
            SeekBar angleSlider = popupView.findViewById(R.id.sbAngle);
            final TextView sizeText = popupView.findViewById(R.id.sizeText);
            final TextView speedText = popupView.findViewById(R.id.speedText);
            final TextView angleText = popupView.findViewById(R.id.angleText);
            ImageView imageView = popupView.findViewById(R.id.settingsImg);
            Button test = popupView.findViewById(R.id.testbutton);

            sizeSlider.setProgress(copy.getSize());
            speedSlider.setProgress(copy.getSpeed());
            angleSlider.setProgress(copy.getAngle());
            sizeText.setText(String.valueOf(copy.getSize()));
            speedText.setText(String.valueOf(copy.getSpeed()));
            angleText.setText(String.valueOf(copy.getAngle()));

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

            //int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            /*popupView.setOnTouchListener(new View.OnTouchListener() {
                *//*@Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }*//*
            });*/

        }
    }

    private static class getObjectsAsync extends AsyncTask<Void, Void, ArrayList<ForegroundObject>> {

        private View view;
        private Context context;
        private ArrayList<ForegroundObject> asyncObjects = new ArrayList<>();
        private AppDatabase database;
        private ForegroundAdapter adapter;
        private RecyclerView recyclerView;

        getObjectsAsync(View v, Context c , AppDatabase db, ForegroundAdapter adapter, RecyclerView recyclerView){
            this.view = v;
            this.context = c;
            this.database = db;
            this.adapter = adapter;
            this.recyclerView = recyclerView;
        }

        @Override
        protected ArrayList<ForegroundObject> doInBackground(Void... voids) {

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();

            Log.d("size of database", rawObjects.size() + "");

            Log.d(" Get Async","Successful");
            for (int i = 0; i < rawObjects.size(); i++){
                RawObject r = rawObjects.get(i);
                ForegroundObject obj = new ForegroundObject(context, r.getId(), r.isEnabled(),
                        r.isUsesImage(), r.getImageName(), r.getSize(), r.getSpeed(), r.getAngle());
                asyncObjects.add(obj);
                Log.i("Object",obj.toString());
            }

            return asyncObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<ForegroundObject> asyncObjects) {
            super.onPostExecute(asyncObjects);
            adapter.objects = asyncObjects;

            Log.d("size of objects", adapter.objects.size() + "");

            for (int i = 0; i < adapter.objects.size(); i++){
                Log.d("ID", adapter.objects.get(i).getID() + "");
                adapter.notifyItemInserted(i); // questionable
                recyclerView.scrollToPosition(i);
            }
        }
    }

    private static class ToggleSingleAsync extends AsyncTask<Void, Void, Void>{

        private AppDatabase database;
        private ForegroundAdapter adapter;
        private int position;

        ToggleSingleAsync(AppDatabase db, ForegroundAdapter adapter, int position){
            this.database = db;
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();

            int ID = adapter.objects.get(position).getID();

            for (int i = 0; i < rawObjects.size(); i++){
                RawObject r = rawObjects.get(i);
                Log.d("DB ID ", r.getId() + "");
                if(r.getId() == ID){

                    RawObject copy = rawObjects.get(i);
                    copy.toggleEnabled();
                    database.objectDao().removeObject(rawObjects.get(i));
                    database.objectDao().insertObject(copy);
                    adapter.objects.get(i).toggleEnabled();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}

