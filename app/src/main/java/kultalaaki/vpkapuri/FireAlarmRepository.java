/*
 * Created by Kultala Aki on 4/24/21 9:34 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 4/5/21 5:05 PM
 */

package kultalaaki.vpkapuri;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FireAlarmRepository {
    private FireAlarmDao fireAlarmDao;
    private LiveData<List<FireAlarm>> allFireAlarms;
    private LiveData<List<FireAlarm>> fireAlarmLastEntry;

    FireAlarmRepository(Application application) {
        FireAlarmDatabase database = FireAlarmDatabase.getInstance(application);
        fireAlarmDao = database.fireAlarmsDao();
        allFireAlarms = fireAlarmDao.getAllFireAlarms();
        fireAlarmLastEntry = fireAlarmDao.getLatest();
    }

    public void insert(FireAlarm fireAlarm) {
        new InsertFireAlarmAsyncTask(fireAlarmDao).execute(fireAlarm);
    }

    public void update(FireAlarm fireAlarm) {
        new UpdateFireAlarmAsyncTask(fireAlarmDao).execute(fireAlarm);
    }

    public void delete(FireAlarm fireAlarm) {
        new DeleteFireAlarmAsyncTask(fireAlarmDao).execute(fireAlarm);
    }

    FireAlarm getLatest() {
        return fireAlarmDao.latest();
    }

    void deleteAllFireAlarms() {
        new DeleteAllFireAlarmAsyncTask(fireAlarmDao).execute();
    }

    LiveData<List<FireAlarm>> getLastEntry() {return fireAlarmLastEntry;}

    LiveData<List<FireAlarm>> getAllFireAlarms() {return allFireAlarms;}

    private static class InsertFireAlarmAsyncTask extends AsyncTask<FireAlarm, Void, Void> {
        private FireAlarmDao fireAlarmDao;

        private InsertFireAlarmAsyncTask(FireAlarmDao fireAlarmDao) {
            this.fireAlarmDao = fireAlarmDao;
        }

        @Override
        protected Void doInBackground(FireAlarm... fireAlarms) {
            fireAlarmDao.insert(fireAlarms[0]);
            return null;
        }
    }

    private static class UpdateFireAlarmAsyncTask extends AsyncTask<FireAlarm, Void, Void> {
        private FireAlarmDao fireAlarmDao;

        private UpdateFireAlarmAsyncTask(FireAlarmDao fireAlarmDao) {
            this.fireAlarmDao = fireAlarmDao;
        }

        @Override
        protected Void doInBackground(FireAlarm... fireAlarms) {
            fireAlarmDao.update(fireAlarms[0]);
            return null;
        }
    }

    private static class DeleteFireAlarmAsyncTask extends AsyncTask<FireAlarm, Void, Void> {
        private FireAlarmDao fireAlarmDao;

        private DeleteFireAlarmAsyncTask(FireAlarmDao fireAlarmDao) {
            this.fireAlarmDao = fireAlarmDao;
        }

        @Override
        protected Void doInBackground(FireAlarm... fireAlarms) {
            fireAlarmDao.delete(fireAlarms[0]);
            return null;
        }
    }

    private static class DeleteAllFireAlarmAsyncTask extends AsyncTask<Void, Void, Void> {
        private FireAlarmDao fireAlarmDao;

        private DeleteAllFireAlarmAsyncTask(FireAlarmDao fireAlarmDao) {
            this.fireAlarmDao = fireAlarmDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fireAlarmDao.deleteAllFireAlarms();
            return null;
        }
    }

}
