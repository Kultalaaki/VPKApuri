package kultalaaki.vpkapuri;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = Responder.class, version = 1, exportSchema = false)
public abstract class ResponderDatabase extends RoomDatabase {

    private static ResponderDatabase instance;

    public abstract ResponderDao responderDao();

    public static synchronized ResponderDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ResponderDatabase.class, "responder_table")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
