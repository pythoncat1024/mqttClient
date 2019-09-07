package org.victor.mqttcat.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apkfuns.logutils.LogUtils;

import org.victor.mqttcat.model.DataRepository;

public class MeViewModel extends AndroidViewModel {

    private LiveData<String> mClientID;
    private LiveData<Boolean> hasClientId;

    public MeViewModel(@NonNull Application application) {
        super(application);
        MutableLiveData<String> liveData = new MutableLiveData<>();
        liveData.setValue(DataRepository.getClientId(application));
        mClientID = liveData;
        MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
        booleanMutableLiveData.setValue(DataRepository.hasLocalStore(application));
        hasClientId = booleanMutableLiveData;
    }

    public LiveData<Boolean> hasClientId() {
        return hasClientId;
    }

    public LiveData<String> getClientID() {
        return mClientID;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtils.e("clear..");
    }
}
