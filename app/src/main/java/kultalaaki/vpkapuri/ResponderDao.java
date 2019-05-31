package kultalaaki.vpkapuri;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ResponderDao {

    @Insert
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
