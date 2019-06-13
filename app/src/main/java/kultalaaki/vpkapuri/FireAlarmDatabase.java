package kultalaaki.vpkapuri;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = FireAlarm.class, version = 1, exportSchema = false)
public abstract class FireAlarmDatabase extends RoomDatabase {

    private static FireAlarmDatabase instance;

    public abstract FireAlarmDao fireAlarmsDao();

    public static synchronized FireAlarmDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FireAlarmDatabase.class, "firealarms_table")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
