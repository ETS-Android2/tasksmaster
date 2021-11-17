package com.dandelion.tasksmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserTasks.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}