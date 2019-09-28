package com.austin.retro.database;

import android.app.WallpaperManager;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Entity
public class Background {

    @PrimaryKey
    private int id;

    private int color = Color.argb(0xFF, 0x11, 0x11, 0x11);
    private boolean usesImage = false;
    private String imageName;

    @Ignore
    private Bitmap image;

    public Background(int id, int color){
        this.id = id;
        this.color = color;
    }

    public Background(int id, String in) {
        this.id = id;
        this.usesImage = true;
        this.imageName = in;
    }

    public void draw(Canvas canvas, Context c) {
        //update();
        if(!usesImage) {
            canvas.drawColor(this.color);
        }
        else{
            /*WallpaperManager myWallpaperManager = WallpaperManager.getInstance(c);
            this.image = drawableToBitmap(myWallpaperManager.getDrawable());*/
            try {
                this.image = BitmapFactory.decodeFile(c.getFilesDir().getAbsolutePath()+"/"+imageName+".png");
            }
            catch (Exception e){
                e.printStackTrace();
            }

            int xPos = 0;
            int yPos = 0;
            canvas.drawBitmap(this.image, xPos, yPos, new Paint());
        }
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public boolean isUsesImage() {
        return usesImage;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setUsesImage(boolean usesImage) {
        this.usesImage = usesImage;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
