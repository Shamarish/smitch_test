package com.example.nsd

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nsd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), DiscoveryHelper.ActivityListener {

    private lateinit var binding: ActivityMainBinding
    private val tag: String = MainActivity::class.java.simpleName
    private lateinit var serviceDataList: ArrayList<NsdServiceInfo?>
    private lateinit var recyclerView: RecyclerView
    private var discoveryHelper: DiscoveryHelper? = null

    private val resolveListener = object : NsdManager.ResolveListener{
        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            Log.d(tag, "onResolveFailed: ")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            Log.d(tag, "onServiceResolved: ")
            runOnUiThread {
                for(serviceData in serviceDataList){
                    if(serviceData?.host?.hostAddress.equals(serviceInfo?.host?.hostAddress)){
                        return@runOnUiThread
                    }
                }
                serviceDataList.add(serviceInfo)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        serviceDataList = ArrayList()
        recyclerView = binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ServiceAdapter(serviceDataList)
        }

        discoveryHelper = DiscoveryHelper(this@MainActivity, resolveListener)

        binding.scanButton.setOnClickListener {
            Log.d(tag, "On Scan Button Clicked : ")
            discoveryHelper?.scanService()
        }

        binding.publishButton.setOnClickListener {
            Log.d(tag, "On Publish Button Clicked : ")
            discoveryHelper?.publishService()
        }

    }


     override fun onPause() {
         discoveryHelper?.clearAll()
         super.onPause()
     }

     override fun onResume() {
         super.onResume()
         Log.d(tag, "onResume: ")

     }

     override fun onDestroy() {
         discoveryHelper?.clearAll()
         super.onDestroy()
     }

    override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
        runOnUiThread{
            var serData: NsdServiceInfo? = null
            for(serviceData in serviceDataList){
                if(serviceData?.host?.hostAddress.equals(serviceInfo?.host?.hostAddress)){
                    serData = serviceData
                    break
                }
            }
            if(serData != null) {
                serviceDataList.remove(serData)
                recyclerView.adapter?.notifyDataSetChanged()

            }
        }
    }

}