package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ItemListViewBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto.ItemListDTO


class ItemListAdapter : PagingDataAdapter<ItemListDTO, ItemListAdapter.ItemListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val binding = ItemListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ItemListViewHolder(private val binding: ItemListViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemListDTO) {
            binding.item2ReservationId.text = item.reservationId.toString()
            binding.item2ReservationItemId.text = item.reservationItemId.toString()
            binding.item2ReservationDate.text = item.reservationDate.toString()
            binding.itemReservationTime.text = item.reservationTime
            binding.itemUsername.text = item.username
            binding.itemPhone.text = item.phone
            binding.itemAddress.text = item.address
            binding.itemName.text = item.name
            binding.itemPrice.text = item.price.toString()
            binding.itemDescription.text = item.description

            val imageUrl = "http://10.100.201.87:8080/items/${item.itemRepImageId}/itemImageObjectId"
//            val imageUrl = "http://192.168.219.200:8080/api/users/${user?.id}/profileImage"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.user_basic)
                .into(binding.item2RepImage)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemListDTO>() {
            override fun areItemsTheSame(oldItem: ItemListDTO, newItem: ItemListDTO): Boolean {
                return oldItem.reservationItemId == newItem.reservationItemId
            }

            override fun areContentsTheSame(oldItem: ItemListDTO, newItem: ItemListDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}