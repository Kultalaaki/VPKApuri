package kultalaaki.vpkapuri;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = FireAlarm.class, version = 1, exportSchema = false)
public abstract class FireAlarmsDatabase extends RoomDatabase {

    private static FireAlarmsDatabase instance;

    public abstract FireAlarmDao fireAlarmsDao();

    public static synchronized FireAlarmsDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FireAlarmsDatabase.class, "firealarms_table")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
