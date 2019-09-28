package com.austin.retro.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.austin.retro.R;

import java.util.Random;

@Entity
public class RawObject {

    @PrimaryKey
    private int id;

    private boolean isEnabled = true;
    private boolean usesImage = false;
    private String imageName = String.valueOf(R.raw.dvd_p);
    private int size = 100;
    private int speed = 100;
    private int angle = new Random().nextInt(360);

    public RawObject(int id){
        this.id = id;
    }

    public RawObject(int id, boolean is, boolean u, String im, int si, int sp, int an) {
        this.id = id;
        this.isEnabled = is;
        this.usesImage = u;
        this.imageName = im;
        this.size = si;
        this.speed = sp;
        this.angle = an;
    }

    public int getId() {
        return id;
    }

    public void setId( int id) {
        this.id = id;
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

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setUsesImage(boolean usesImage) {
        this.usesImage = usesImage;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
