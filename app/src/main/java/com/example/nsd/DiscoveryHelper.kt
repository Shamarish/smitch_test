package com.example.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import android.util.Log
import android.widget.Toast

class DiscoveryHelper(private val context: Context?, val resolveListener: NsdManager.ResolveListener?) {

    private val nsdManager: NsdManager? = context?.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_http._tcp."
    private val serviceName = "NsdTest"
    private var registrationListener: RegistrationListener? = null
    private var discoveryListener: DiscoveryListener? = null
    private val tag: String = DiscoveryHelper::class.java.simpleName
    private val activityListener: ActivityListener? = context as ActivityListener


    fun publishService() {
        tearDown()
        initializeRegistrationListener()
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = this@DiscoveryHelper.serviceName
            serviceType = this@DiscoveryHelper.serviceType
            port = 8080
        }
        nsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    private fun initializeRegistrationListener() {
        registrationListener = object : RegistrationListener {
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(tag, "onUnregistrationFailed: ")

            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                Log.d(tag, "onServiceUnregistered: ")

            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(tag, "onRegistrationFailed: ")
                displayToast("Published Failed")

            }

            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                Log.d(tag, "onServiceRegistered: ")
                displayToast("Published")
            }

        }
    }

    private fun initializeDiscoveryListener() {
        discoveryListener = object : DiscoveryListener {

            override fun onDiscoveryStarted(regType: String) {
                Log.d(tag, "Service discovery started")
                displayToast("Scanning Started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(tag, "Service discovery success$service")
                when {
                    service.serviceType != serviceType ->
                        Log.d(tag, "Unknown Service Type: ${service.serviceType}")
                    service.serviceName.contains(serviceName) ->
                        nsdManager?.resolveService(service, object : NsdManager.ResolveListener{
                            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                                resolveListener?.onResolveFailed(serviceInfo, errorCode)
                            }

                            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                                resolveListener?.onServiceResolved(serviceInfo)
                            }

                        })
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(tag, "service lost: $service")
                activityListener?.onServiceLost(service)
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(tag, "Discovery stopped: $serviceType")

            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(tag, "Discovery failed: Error code:$errorCode")
                nsdManager?.stopServiceDiscovery(discoveryListener)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(tag, "Discovery failed: Error code:$errorCode")
                nsdManager?.stopServiceDiscovery(discoveryListener)
            }
        }
    }

    fun scanService(){
        stopDiscovery()
        initializeDiscoveryListener()
        nsdManager?.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun tearDown(){
        if(registrationListener != null){
            try {
                nsdManager?.unregisterService(registrationListener)
            }catch (e: Exception){
                Log.e(tag, "tearDown: ", e)
            }
            registrationListener = null
        }
    }

    private fun stopDiscovery(){
        if(discoveryListener != null){
            try {
                nsdManager?.stopServiceDiscovery(discoveryListener)
            }catch (e: Exception){
                Log.e(tag, "stopDiscovery: ", e)
            }
            discoveryListener = null
        }
    }

    fun clearAll(){
        stopDiscovery()
        tearDown()
    }


    private fun displayToast(message: String){
        Toast.makeText(context?.applicationContext, message, Toast.LENGTH_SHORT).show()

    }

    interface ActivityListener{
        fun onServiceLost(serviceInfo: NsdServiceInfo?)
    }


}