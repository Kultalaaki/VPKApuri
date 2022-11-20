/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.dbresponder;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ResponderRepository {
    private ResponderDao responderDao;
    private LiveData<List<Responder>> allResponders;

    public ResponderRepository(Application application) {
        ResponderDatabase database = ResponderDatabase.getInstance(application);
        responderDao = database.responderDao();
        allResponders = responderDao.getAllResponders();
    }

    public void insert(Responder responder) {
        new InsertResponderAsyncTask(responderDao).execute(responder);
    }

    public void update(Responder responder) {
        new UpdateResponderAsyncTask(responderDao).execute(responder);
    }

    public void delete(Responder responder) {
        new DeleteResponderAsyncTask(responderDao).execute(responder);
    }

    void deleteAllResponders() {
        new DeleteAllResponderAsyncTask(responderDao).execute();
    }

    LiveData<List<Responder>> getAllResponders() {
        return allResponders;
    }

    private static class InsertResponderAsyncTask extends AsyncTask<Responder, Void, Void> {
        private ResponderDao responderDao;

        private InsertResponderAsyncTask(ResponderDao responderDao) {
            this.responderDao = responderDao;
        }

        @Override
        protected Void doInBackground(Responder... responders) {
            responderDao.insert(responders[0]);
            return null;
        }
    }

    private static class UpdateResponderAsyncTask extends AsyncTask<Responder, Void, Void> {
        private ResponderDao responderDao;

        private UpdateResponderAsyncTask(ResponderDao responderDao) {
            this.responderDao = responderDao;
        }

        @Override
        protected Void doInBackground(Responder... responders) {
            responderDao.update(responders[0]);
            return null;
        }
    }

    private static class DeleteResponderAsyncTask extends AsyncTask<Responder, Void, Void> {
        private ResponderDao responderDao;

        private DeleteResponderAsyncTask(ResponderDao responderDao) {
            this.responderDao = responderDao;
        }

        @Override
        protected Void doInBackground(Responder... responders) {
            responderDao.delete(responders[0]);
            return null;
        }
    }

    private static class DeleteAllResponderAsyncTask extends AsyncTask<Void, Void, Void> {
        private ResponderDao responderDao;

        private DeleteAllResponderAsyncTask(ResponderDao responderDao) {
            this.responderDao = responderDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            responderDao.deleteAllResponders();
            return null;
        }
    }
}
