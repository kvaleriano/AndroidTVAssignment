package com.example.assignmentenrique.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.assignmentenrique.data.Photo;

@Database(entities = {Photo.class}, version = 1, exportSchema = false)
public abstract class AssignmentDatabase extends RoomDatabase {

    public abstract PhotoDao photoDao();
    private static AssignmentDatabase INSTANCE = null;

    public static AssignmentDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AssignmentDatabase.class, "assignment.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}