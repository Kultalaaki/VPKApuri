/*
 * Created by Kultala Aki on 7.7.2019 12:26
 * Copyright (c) 2019. All rights reserved.
 * Last modified 4.7.2019 16:13
 */

package kultalaaki.vpkapuri;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class FireAlarmViewModel extends AndroidViewModel {
    private FireAlarmRepository repository;
    private LiveData<List<FireAlarm>> allFireAlarms;
    private LiveData<List<FireAlarm>> lastFireAlarm;

    private MutableLiveData<CharSequence> address = new MutableLiveData<>();
    private MutableLiveData<CharSequence> number = new MutableLiveData<>();
    private MutableLiveData<CharSequence> tunnus = new MutableLiveData<>();

    public FireAlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new FireAlarmRepository(application);
        allFireAlarms = repository.getAllFireAlarms();
        lastFireAlarm = repository.getLastEntry();
    }

    void setAddress(CharSequence input) {
        address.setValue(input);
    }

    LiveData<CharSequence> getAddress() {
        return address;
    }

    void setAlarmingNumber(CharSequence input) {
        number.setValue(input);
    }

    LiveData<CharSequence> getAlarmingNumber() {
        return number;
    }

    void setTunnus(CharSequence input) {tunnus.setValue(input);}

    LiveData<CharSequence> getTunnus() {return tunnus;}

    public void insert(FireAlarm fireAlarm) {repository.insert(fireAlarm);}

    public void update(FireAlarm fireAlarm) {repository.update(fireAlarm);}

    public void delete(FireAlarm fireAlarm) {repository.delete(fireAlarm);}

    public void deleteAll(FireAlarm fireAlarm) {repository.deleteAllFireAlarms();}

    public FireAlarm lastEntry() {
        return repository.getLatest();
    }

    LiveData<List<FireAlarm>> getLastEntry() {return lastFireAlarm;}

    LiveData<List<FireAlarm>> getAllFireAlarms() {return allFireAlarms;}
}
