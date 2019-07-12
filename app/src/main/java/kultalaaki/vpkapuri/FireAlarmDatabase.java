/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = FireAlarm.class, version = 5, exportSchema = false)
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
