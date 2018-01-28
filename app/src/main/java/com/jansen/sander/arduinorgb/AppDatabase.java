package com.jansen.sander.arduinorgb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Sander on 20/12/2017.
 */

@Database(entities = {CustomColor.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance = null;

    // This is a singleton
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, "Colors").build();
        }

        return instance;
    }

    public static void destroy() {
        // Works because of the garbage collector
        instance = null;
    }


    public abstract ColorDB colorDB();
}