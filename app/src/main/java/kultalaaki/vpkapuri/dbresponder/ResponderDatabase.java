/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.dbresponder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Responder.class, version = 4, exportSchema = false)
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
