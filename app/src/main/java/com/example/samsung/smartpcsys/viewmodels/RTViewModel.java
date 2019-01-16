package com.example.samsung.smartpcsys.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

import java.util.List;


public class RTViewModel extends ViewModel {

    private MutableLiveData<List<RoutingTable>> rtEntry = new MutableLiveData<>();

    public void setRTEntry(List<RoutingTable> value){
        rtEntry.postValue(value);
    }

    public MutableLiveData<List<RoutingTable>> getRTEntry(){
        return rtEntry;
    }

}
