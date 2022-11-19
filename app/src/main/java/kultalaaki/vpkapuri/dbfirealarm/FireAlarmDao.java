/*
 * Created by Kultala Aki on 2.4.2022 10.05
 * Copyright (c) 2022. All rights reserved.
 * Last modified 1.8.2021 15.43
 */

package kultalaaki.vpkapuri.dbfirealarm;

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

    @Query("SELECT * FROM firealarm_table ORDER BY id DESC")
    List<FireAlarm> getAllFireAlarmsToList();
}
