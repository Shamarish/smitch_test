package com.example.nsd

import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nsd.databinding.AdapterServiceBinding

class ServiceAdapter(private val serviceData: ArrayList<NsdServiceInfo?>) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(private var binding: AdapterServiceBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(service: NsdServiceInfo?){
            binding.data = service
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding: AdapterServiceBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.adapter_service, parent, false)
        return ServiceViewHolder(binding)
    }

    override fun getItemCount() = serviceData.size

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(serviceData[position])
    }

}