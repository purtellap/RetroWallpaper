package com.austin.retro;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

public class GlowDrawable extends ShapeDrawable {

    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mOffsetX = 40.0f;
    private float mOffsetY = 80.0f;
    private float mRadius = 0.0f;
    private float mSpeedX = 10.0f;
    private float mSpeedY = 20.0f;

    private int screenWidth = MainActivity.WIDTH;
    private int screenHeight = MainActivity.HEIGHT;

    private int mColorFG = Color.rgb(0xFF, 0xFF, 0x00); // yellow
    private int mColorBG = Color.rgb(0xFF, 0x66, 0x33); // orange

    public GlowDrawable() {
        setBounds();
    }

    public void setBounds() {
        if (mRadius == 0.0f) {
            mCenterX = (screenWidth)/2.0f;
            mCenterY = (screenHeight)/2.0f;
            mRadius = mCenterX + mCenterY;
        }
    }

    public void update() {
        mCenterX += mSpeedX;
        mCenterY += mSpeedY;

        if (mCenterX < 0  || mCenterX > screenWidth - mOffsetX) {
            mSpeedX *= -1.0f;
        }

        if (mCenterY < 0 ||
                mCenterY > screenHeight - mOffsetY) {
            mSpeedY *= -1.0f;
        }
    }

    public Paint getPaint() {

        update();

        RadialGradient shader = new RadialGradient(
                mCenterX, mCenterY, mRadius,
                mColorFG, mColorBG,
                Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setShader(shader);
        return paint;
    }

    public void draw(Canvas c) {
        c.drawRect(0, 0, screenWidth, screenHeight, getPaint());
    }
}
