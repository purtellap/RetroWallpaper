package com.austin.retro.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {RawObject.class, Background.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ObjectDao objectDao();
    public abstract BackgroundDao backgroundDao();
}
