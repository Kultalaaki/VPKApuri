package kultalaaki.vpkapuri;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FireAlarmViewModel extends AndroidViewModel {
    private FireAlarmRepository repository;
    private LiveData<List<FireAlarm>> allFireAlarms;

    public FireAlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new FireAlarmRepository(application);
        allFireAlarms = repository.getAllFireAlarms();
    }

    public void insert(FireAlarm fireAlarm) {repository.insert(fireAlarm);}

    public void update(FireAlarm fireAlarm) {repository.update(fireAlarm);}

    public void delete(FireAlarm fireAlarm) {repository.delete(fireAlarm);}

    public void deleteAll(FireAlarm fireAlarm) {repository.deleteAllFireAlarms();}

    public FireAlarm lastEntry() {
        return repository.getLatest();
    }

    public LiveData<List<FireAlarm>> getAllFireAlarms() {return allFireAlarms;}
}
