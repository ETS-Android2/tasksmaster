package com.dandelion.tasksmaster;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM UserTasks")
    List<UserTasks> getAll();

    @Insert
    void insertAll(UserTasks... userTasks);

    @Delete
    void delete(UserTasks userTasks);
}
