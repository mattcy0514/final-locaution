package com.matt.locaution.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LocationEntity.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {
    private static LocationDatabase minstance;
    private static final String DB_NAME = "location-db";
    public abstract LocationDAO getLocationDAO();
    public static synchronized LocationDatabase getInstance(Context context) {
        if (minstance == null) {
            minstance = Room.databaseBuilder(context.getApplicationContext(),
                    LocationDatabase.class, DB_NAME).fallbackToDestructiveMigration()
                    .build();
        }
        return minstance;
    }
}
