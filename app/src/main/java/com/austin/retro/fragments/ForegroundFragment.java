package com.austin.retro.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.austin.retro.ForegroundAdapter;
import com.austin.retro.ForegroundObject;
import com.austin.retro.MainActivity;
import com.austin.retro.R;
import com.austin.retro.SwipeDeleteCallback;
import com.austin.retro.database.AppDatabase;
import com.austin.retro.database.RawObject;

import java.util.ArrayList;

public class ForegroundFragment extends Fragment {

    FloatingActionButton addObj;
    RecyclerView recyclerView;
    ForegroundAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    View view;
    ImageView clearAll;
    ImageView toggleAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_objects, container, false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);

        // Add objects
        addObj = (FloatingActionButton) view.findViewById(R.id.fab_add_obj);
        addObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int lastID = sharedPref.getInt("lastID", 0);
                RawObject rawObj = new RawObject(lastID+1);

                ForegroundFragment.putObjAsync putObjectsAsync = new ForegroundFragment.putObjAsync(view.getContext(),
                        MainActivity.objectDB, mAdapter, recyclerView, rawObj);
                putObjectsAsync.execute();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("lastID", rawObj.getId());
                editor.apply();

            }
        });

        clearAll = view.findViewById(R.id.clear_button);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true);
                builder.setTitle("Clear All?");
                builder.setMessage("This will delete all of your objects forever!");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ForegroundFragment.clearObjectsAsync clearObjectsAsync = new ForegroundFragment.clearObjectsAsync(
                                        view.getContext(), MainActivity.objectDB, mAdapter);
                                clearObjectsAsync.execute();

                                // resets sharedpref to zero to avoid big ol numbers
                                SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                                editor.putInt("lastID", 0);
                                editor.apply();

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ForegroundAdapter(view, recyclerView);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new
                SwipeDeleteCallback(mAdapter, getActivity(), view, recyclerView));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        final Drawable checkTrue = getResources().getDrawable( R.drawable.ic_checkbox_in);
        final Drawable checkFalse = getResources().getDrawable( R.drawable.ic_checkbox_out);

        toggleAll = view.findViewById(R.id.toggle_button);
        //toggleAll.setBackground(checkTrue);
        toggleAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if(toggleAll.getBackground() == checkTrue){
                    toggleAll.setBackground(checkFalse);
                }
                else{
                    toggleAll.setBackground(checkTrue);
                }

                ForegroundFragment.ToggleAllAsync toggleAllAsync = new ForegroundFragment.ToggleAllAsync(
                        MainActivity.objectDB, mAdapter, toggleAll.getBackground() == checkTrue);
                toggleAllAsync.execute();*/

                Toast.makeText(v.getContext(), "Coming Soon: Toggle All", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    public static class putObjAsync extends AsyncTask<Void, Void, RawObject>{

        private Context context;
        private AppDatabase database;
        private ForegroundAdapter adapter;
        private RecyclerView recyclerView;
        private RawObject rawObject;

        public putObjAsync(Context c , AppDatabase db, ForegroundAdapter adapter, RecyclerView recyclerView, RawObject rawObject){
            this.context = c;
            this.database = db;
            this.adapter = adapter;
            this.recyclerView = recyclerView;
            this.rawObject = rawObject;
        }

        @Override
        protected RawObject doInBackground(Void... voids) {

            database.objectDao().insertObject(rawObject);

            Log.d("Put Async","Successful");

            return rawObject;
        }

        @Override
        protected void onPostExecute(RawObject r) {
            super.onPostExecute(r);

            Log.d("ID", r.getId() + "");

            ForegroundObject foregroundObject = new ForegroundObject(context, r.getId(),
                    r.isEnabled(), r.isUsesImage(), r.getImageName(), r.getSize(), r.getSpeed(), r.getAngle());

            adapter.objects.add(foregroundObject);

            Log.d("size of objects", adapter.objects.size() + "");

            adapter.notifyItemInserted(adapter.objects.size() - 1); // questionable
            recyclerView.scrollToPosition(adapter.objects.size()-1);
        }
    }

    private static class clearObjectsAsync extends AsyncTask<Void, Void, Void>{

        private Context context;
        private AppDatabase database;
        private ForegroundAdapter adapter;

        clearObjectsAsync(Context c , AppDatabase db, ForegroundAdapter adapter){
            this.context = c;
            this.database = db;
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("Clear Async","Successful");

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();
            for (int i = 0; i < rawObjects.size(); i++){
                database.objectDao().removeObject(rawObjects.get(i));
            }

            Log.d("size of database", database.objectDao().getAllObjects().size() + "");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int size = adapter.objects.size();
            for (int i = 0; i < size; i++) {
                adapter.objects.remove(0);
                adapter.notifyItemRemoved(0); // ree
            }

            Log.d("size of objects", adapter.objects.size() + "");
            Toast.makeText( context, "Objects Removed.", Toast.LENGTH_SHORT ).show();
        }
    }

    private static class ToggleAllAsync extends AsyncTask<Void, Void, Void>{

        private AppDatabase database;
        private ForegroundAdapter adapter;
        private boolean toggle;

        ToggleAllAsync(AppDatabase db, ForegroundAdapter adapter, boolean toggle){
            this.database = db;
            this.adapter = adapter;
            this.toggle = toggle;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();
            for (int i = 0; i < rawObjects.size(); i++){

                RawObject copy = rawObjects.get(i);
                copy.setEnabled(toggle);
                database.objectDao().removeObject(rawObjects.get(i));
                database.objectDao().insertObject(copy);
                adapter.objects.get(i).setEnabled(toggle);
            }

            Log.d("Toggle",toggle + "");
            ArrayList<RawObject> rawObjects2 = (ArrayList<RawObject>) database.objectDao().getAllObjects();
            for (int i = 0; i < rawObjects2.size(); i++){

                Log.d("Value",rawObjects2.get(i).isEnabled() + "");
            }

            Log.d("Toggle Async","Successful");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter.notifyDataSetChanged();
        }
    }

}
