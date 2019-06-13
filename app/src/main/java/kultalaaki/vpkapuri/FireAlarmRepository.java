package kultalaaki.vpkapuri;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FireAlarmRepository {
    private FireAlarmDao fireAlarmDao;
    private LiveData<List<FireAlarm>> allFireAlarms;

    public FireAlarmRepository(Application application) {
        FireAlarmDatabase database = FireAlarmDatabase.getInstance(application);
        fireAlarmDao = database.fireAlarmsDao();
        allFireAlarms = fireAlarmDao.getAllFireAlarms();
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

    public void deleteAllFireAlarms() {
        new DeleteAllFireAlarmAsyncTask(fireAlarmDao).execute();
    }

    public LiveData<List<FireAlarm>> getAllFireAlarms() {return allFireAlarms;}

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
