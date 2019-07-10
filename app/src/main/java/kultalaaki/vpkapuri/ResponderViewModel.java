/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 10.7.2019 23:01
 */

package kultalaaki.vpkapuri;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import java.util.List;

public class ResponderViewModel extends AndroidViewModel {
    private ResponderRepository repository;
    private LiveData<List<Responder>> allResponders;

    public ResponderViewModel(@NonNull Application application) {
        super(application);
        repository = new ResponderRepository(application);
        allResponders = repository.getAllResponders();
    }

    public void insert(Responder responder) {
        repository.insert(responder);
    }

    public void update(Responder responder) {
        repository.update(responder);
    }

    public void delete(Responder responder) {
        repository.delete(responder);
    }

    void deleteAll() {
        repository.deleteAllResponders();
    }

    LiveData<List<Responder>> getAllResponders() {
        return allResponders;
    }
}
