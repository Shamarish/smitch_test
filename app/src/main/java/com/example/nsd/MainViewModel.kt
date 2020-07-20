package com.example.nsd

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var service : MutableLiveData<NsdServiceInfo> = MutableLiveData()

    fun getServices() : LiveData<NsdServiceInfo> {
        return service
    }

    fun setService(nsdServiceInfo: NsdServiceInfo?) {
        service.value = nsdServiceInfo
    }

}