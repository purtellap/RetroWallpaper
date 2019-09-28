package com.austin.retro.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ObjectDao {

    @Insert
    void insertObject(RawObject object);

    @Delete
    void removeObject(RawObject object);

    @Query("SELECT * FROM RawObject")
    List<RawObject> getAllObjects();
}
