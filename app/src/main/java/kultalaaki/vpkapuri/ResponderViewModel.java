package kultalaaki.vpkapuri;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

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

    public void deleteAll() {
        repository.deleteAllResponders();
    }

    public LiveData<List<Responder>> getAllResponders() {
        return allResponders;
    }
}
