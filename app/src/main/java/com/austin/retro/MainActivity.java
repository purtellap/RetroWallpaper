package com.austin.retro;

import android.Manifest;
import android.app.WallpaperManager;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.austin.retro.database.AppDatabase;
import com.austin.retro.database.Background;
import com.austin.retro.database.RawObject;
import com.austin.retro.fragments.BackgroundFragment;
import com.austin.retro.fragments.ForegroundFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    NavigationView navigationView;
    public static AppDatabase objectDB;

    public final int REQ_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // gets w/h of screen including navbar
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        if (size.y > size.x) {
            HEIGHT = size.y;
            WIDTH = size.x;
        } else {
            HEIGHT = size.x;
            WIDTH = size.y;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         objectDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "ObjectDB").build();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ForegroundFragment()).commit();
        navigationView.getMenu().findItem(R.id.nav_Foreground).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_preview) {

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_READ);
            }
            else{
                MainActivity.getWallpaperObjectsAsync async = new MainActivity.getWallpaperObjectsAsync(
                        getApplicationContext(), MainActivity.objectDB);
                async.execute();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_READ:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    MainActivity.getWallpaperObjectsAsync async = new MainActivity.getWallpaperObjectsAsync(
                            getApplicationContext(), MainActivity.objectDB);
                    async.execute();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        navigationView.getMenu().findItem(R.id.nav_Foreground).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_Background).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_Library).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_Upgrade).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_About).setChecked(false);

        switch (item.getItemId()) {
            case R.id.nav_Foreground:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ForegroundFragment()).commit();
                break;
            case R.id.nav_Background:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BackgroundFragment()).commit();
                break;
            case R.id.nav_Library:
                Toast.makeText(this, "Lib", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Upgrade:
                Toast.makeText(this, "Upgrade", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_About:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
        }

        item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static class getWallpaperObjectsAsync extends AsyncTask<Void, Void, ArrayList<ForegroundObject>> {

        private Context context;
        private ArrayList<ForegroundObject> asyncObjects = new ArrayList<>();
        private AppDatabase database;

        getWallpaperObjectsAsync(Context c , AppDatabase db){
            this.context = c;
            this.database = db;
        }

        @Override
        protected ArrayList<ForegroundObject> doInBackground(Void... voids) {

            ArrayList<RawObject> rawObjects = (ArrayList<RawObject>) database.objectDao().getAllObjects();
            ArrayList<Background> backgrounds = (ArrayList<Background>) database.backgroundDao().getBackground();

            for (int i = 0; i < rawObjects.size(); i++){
                RawObject r = rawObjects.get(i);
                ForegroundObject obj = new ForegroundObject(context, r.getId(), r.isEnabled(),
                        r.isUsesImage(), r.getImageName(), r.getSize(), r.getSpeed(), r.getAngle());
                asyncObjects.add(obj);
            }

            if(backgrounds.size() > 0) {
                WPService.background = backgrounds.get(0);
            }
            else{
                WPService.background = new Background(0, Color.argb(0xff, 0x11, 0x11, 0x11));
            }

            return asyncObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<ForegroundObject> asyncObjects) {
            super.onPostExecute(asyncObjects);
            WPService.wpObjects = asyncObjects;
            Log.d("WP SIZE", WPService.wpObjects.size() + "");

            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(context, WPService.class));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
    }



}
