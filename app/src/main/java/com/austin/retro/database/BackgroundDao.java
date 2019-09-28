package com.austin.retro.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BackgroundDao {

    @Insert
    void insertBackground(Background bkg);

    @Delete
    void removeBackground(Background bkg);

    @Query("SELECT * FROM Background")
    List<Background> getBackground();
}

