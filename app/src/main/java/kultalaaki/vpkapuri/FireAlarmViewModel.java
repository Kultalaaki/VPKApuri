package kultalaaki.vpkapuri;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FireAlarmViewModel extends AndroidViewModel {
    private FireAlarmRepository repository;
    private LiveData<List<FireAlarm>> allFireAlarms;
    private LiveData<List<FireAlarm>> lastFireAlarm;

    public FireAlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new FireAlarmRepository(application);
        allFireAlarms = repository.getAllFireAlarms();
        lastFireAlarm = repository.getLastEntry();
    }

    public void insert(FireAlarm fireAlarm) {repository.insert(fireAlarm);}

    public void update(FireAlarm fireAlarm) {repository.update(fireAlarm);}

    public void delete(FireAlarm fireAlarm) {repository.delete(fireAlarm);}

    public void deleteAll(FireAlarm fireAlarm) {repository.deleteAllFireAlarms();}

    public FireAlarm lastEntry() {
        return repository.getLatest();
    }

    public LiveData<List<FireAlarm>> getLastEntry() {return lastFireAlarm;}

    public LiveData<List<FireAlarm>> getAllFireAlarms() {return allFireAlarms;}
}
