package com.austin.retro;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.graphics.drawable.ArgbEvaluator;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;

import java.util.Random;

public class ForegroundObject {

    private int ID;
    private boolean isEnabled;
    private boolean usesImage;
    private String imageName;
    private int size;
    private int speed;
    private int angle;

    private Bitmap image;

    private int shape;
    private Paint paint = new Paint();
    private int xPos, yPos;

    private float xVelocity;
    private float yVelocity;
    private float speedScale = 0.1f;
    private int screenWidth = MainActivity.WIDTH;
    private int screenHeight = MainActivity.HEIGHT;

    /*public ForegroundObject(Bitmap bmp, Boolean usesImage) {
        this.image = bmp;
        this.usesImg = usesImage;
        this.xPos = (screenWidth/2) - (image.getWidth()/2); // middle
        this.yPos = (screenHeight/2) - (image.getHeight()/2);
        changeColor();
    }*/

    public ForegroundObject(Context c, int id, boolean is, boolean u, String im, int si, int sp, int an) {
        this.ID = id;
        this.isEnabled = is;
        this.usesImage = u;
        this.imageName = im;
        this.size = si;
        this.speed = sp;
        this.angle = an;

        if(!usesImage){
            int imageID = Integer.parseInt(imageName);
            Bitmap unscaled = BitmapFactory.decodeResource(c.getResources(),imageID);
            float ar = unscaled.getHeight()/(float)unscaled.getWidth();
            float width = MainActivity.WIDTH/3f;
            image = Bitmap.createScaledBitmap(unscaled, (int)width, (int)(width*ar), false);
        }
        else{
            // Right now it does the same thing but later it will have to get the image from the user's photos.
            int imageID = Integer.parseInt(imageName);
            Bitmap unscaled = BitmapFactory.decodeResource(c.getResources(),imageID);
            float ar = unscaled.getHeight()/(float)unscaled.getWidth();
            float width = MainActivity.WIDTH/3f;
            image = Bitmap.createScaledBitmap(unscaled, (int)width, (int)(width*ar), false);
        }

        this.xPos = (screenWidth/2) - (image.getWidth()/2); // middle
        this.yPos = (screenHeight/2) - (image.getHeight()/2);

        this.xVelocity = speed * (float) Math.cos(angle) * speedScale;
        this.yVelocity = speed * (float) Math.sin(angle) * speedScale;

        changeColor();
    }

    void draw(Canvas canvas) {
        update();
        canvas.drawBitmap(image, xPos, yPos, paint);
        /*if(usesImg) {
            paint.setShader(new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            if(shape == 0){
                canvas.drawRect(xPos,yPos,xPos+image.getWidth(),yPos+image.getHeight(),paint);
            }
            else if (shape == 1){
                canvas.drawCircle(xPos+image.getWidth()/2f,yPos+image.getWidth()/2f,image.getWidth()/2f,paint);
            }
        }
        else{
            canvas.drawBitmap(image, xPos, yPos, paint);
        }*/
        /*Paint p = new Paint();
            //canvas.drawBitmap(overlayImg, xPos, yPos, null);
            canvas.drawCircle(screenWidth/2f,screenHeight/2f,image.getWidth()/2f,p);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST));*/
        //canvas.drawBitmap(overlayImg, xPos, yPos, paint);
    }

    private void update() {
        xPos += xVelocity;
        yPos += yVelocity;
        if ((xPos > screenWidth - image.getWidth()) || (xPos < 0)) {
            xVelocity = xVelocity * -1;
            changeColor();
        }
        if ((yPos > screenHeight - image.getHeight()) || (yPos < 0)) {
            yVelocity = yVelocity * -1;
            changeColor();
        }
    }

    private void changeColor() {
        if (!usesImage) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public int getShape() {
        return shape;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isUsesImage() {
        return usesImage;
    }

    public String getImageName() {
        return imageName;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAngle() {
        return angle;
    }

    public void toggleEnabled() {
        isEnabled = !isEnabled;
    }

    public void setEnabled(boolean set) {
        isEnabled = set;
    }
}