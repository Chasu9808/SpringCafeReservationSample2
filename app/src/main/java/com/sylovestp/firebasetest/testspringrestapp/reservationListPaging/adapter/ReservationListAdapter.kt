package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ReservationListViewBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto.ItemListDTO
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.ReservationListDTO

class ReservationListAdapter : PagingDataAdapter<ReservationListDTO, ReservationListAdapter.ReservationListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationListViewHolder {
        val binding = ReservationListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ReservationListViewHolder(private val binding: ReservationListViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReservationListDTO) {
            binding.itemName.text = item.name
            binding.itemPrice.text = item.price.toString()
            binding.itemDescription.text = item.description

//            val imageUrl = "http://10.100.201.87:8080/items/${item.itemRepImageId}/itemImageObjectId"
            val imageUrl = "http://192.168.219.200:8080/items/${item.itemRepImageId}/itemImageObjectId"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.user_basic)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.itemRepImage)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReservationListDTO>() {
            override fun areItemsTheSame(oldItem: ReservationListDTO, newItem: ReservationListDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReservationListDTO, newItem: ReservationListDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}