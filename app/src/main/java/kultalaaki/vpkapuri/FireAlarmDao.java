/*
 * Created by Kultala Aki on 7.7.2019 12:26
 * Copyright (c) 2019. All rights reserved.
 * Last modified 4.7.2019 16:13
 */

package kultalaaki.vpkapuri;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FireAlarmDao {

    @Insert
    void insert(FireAlarm fireAlarm);

    @Update
    void update(FireAlarm fireAlarm);

    @Delete
    void delete(FireAlarm fireAlarm);

    @Query("DELETE FROM firealarm_table")
    void deleteAllFireAlarms();

    @Query("SELECT * FROM firealarm_table WHERE id = (SELECT MAX(id) FROM firealarm_table)")
    FireAlarm latest();

    @Query("SELECT * FROM firealarm_table WHERE id = (SELECT MAX(id) FROM firealarm_table)")
    LiveData<List<FireAlarm>> getLatest();

    @Query("SELECT * FROM firealarm_table ORDER BY id DESC")
    LiveData<List<FireAlarm>> getAllFireAlarms();
}
