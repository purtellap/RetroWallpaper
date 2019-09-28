package com.austin.retro;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.austin.retro.database.Background;
import com.austin.retro.fragments.ForegroundFragment;

import java.util.ArrayList;


public class WPService extends WallpaperService {

    // most of this can be attributed to https://www.techrepublic.com/blog/software-engineer/a-bare-bones-live-wallpaper-template-for-android/

    //our custom wallpaper
    public WPService() {
        super();
    }

    @Override
    public Engine onCreateEngine() {
        return new WPEngine();
    }

    public static ArrayList<ForegroundObject> wpObjects = new ArrayList<>();
    public static Background background;

    // ENGINE -- nested in WPService
    private class WPEngine extends Engine {
        private boolean mVisible = false;
        private final Handler mHandler = new Handler();

        private final Runnable mUpdateDisplay = new Runnable()
        {
            @Override
            public void run() {
                draw();
            }
        };

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                draw();
            } else {
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder){

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }

        private void draw() {

            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                //this is where you draw objects to canvas
                c = holder.lockCanvas();
                if (c != null) {

                    //c.drawColor(0xff111111); // 0x AA(alpha) RR GG BB (note: lowering alpha will leave residual images)
                    background.draw(c, getApplicationContext());

                    for (ForegroundObject object : wpObjects){
                        if(object.isEnabled()) {
                            object.draw(c);
                        }
                    }
                }
            } finally {
                //in here you post your paint to the canvas
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                mHandler.postDelayed(mUpdateDisplay, 0);
            }
        }
    }
}
