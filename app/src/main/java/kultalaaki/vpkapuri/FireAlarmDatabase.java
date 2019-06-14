package kultalaaki.vpkapuri;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = FireAlarm.class, version = 3)
public abstract class FireAlarmDatabase extends RoomDatabase {

    private static FireAlarmDatabase instance;

    private static final String DATABASE_NAME = "VPK_Apuri_Halytykset";

    public abstract FireAlarmDao fireAlarmsDao();

    public static synchronized FireAlarmDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FireAlarmDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }
        return instance;
    }
}
