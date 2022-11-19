/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.dbresponder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ResponderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Responder responder);

    @Update
    void update(Responder responder);

    @Delete
    void delete(Responder responder);

    @Query("DELETE FROM responder_table")
    void deleteAllResponders();

    @Query("SELECT * FROM responder_table ORDER BY id DESC")
    LiveData<List<Responder>> getAllResponders();
}
