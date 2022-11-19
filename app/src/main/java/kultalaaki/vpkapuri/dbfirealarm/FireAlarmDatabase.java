/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.dbfirealarm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = FireAlarm.class, version = 6, exportSchema = false)
public abstract class FireAlarmDatabase extends RoomDatabase {

    private static FireAlarmDatabase instance;

    private static final String DATABASE_NAME = "VPK_Apuri_Halytykset";

    abstract FireAlarmDao fireAlarmsDao();

    public static synchronized FireAlarmDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            FireAlarmDatabase.class, DATABASE_NAME)
                    .addMigrations(MIGRATION_4_5, MIGRATION_3_5, MIGRATION_2_5, MIGRATION_1_5, MIGRATION_5_6)
                    .allowMainThreadQueries()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }
        return instance;
    }

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    private static final Migration MIGRATION_3_5 = new Migration(3, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    private static final Migration MIGRATION_2_5 = new Migration(2, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    private static final Migration MIGRATION_1_5 = new Migration(1, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            database.execSQL("ALTER TABLE firealarm_table RENAME COLUMN tunnus TO tehtavaluokka");
            database.execSQL("ALTER TABLE firealarm_table RENAME COLUMN luokka TO kiireellisyystunnus");
        }
    };
}
