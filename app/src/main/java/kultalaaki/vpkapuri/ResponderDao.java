package kultalaaki.vpkapuri;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
