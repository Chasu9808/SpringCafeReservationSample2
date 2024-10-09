package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.iamport.sdk.domain.core.Iamport.response
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ItemListViewBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto.ItemListDTO
import com.sylovestp.firebasetest.testspringrestapp.pay.ui.MainActivity


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
            binding.reservationItemPayStatus.text = item.payStatus
            binding.itemPrice.text = item.price.toString()
            binding.itemDescription.text = item.description

            // Check if payStatus is "waiting for deposit"
            if ( binding.reservationItemPayStatus.text == "입금대기") {
                // Make button visible
                binding.payBtn.visibility = View.VISIBLE
            } else {
                // Otherwise, make button invisible or gone
                binding.payBtn.visibility = View.GONE
            }

            binding.payBtn.setOnClickListener {
                // Intent 생성, 현재 context에서 새로운 Activity로 이동
                val context = itemView.context
                val intent = Intent(context, MainActivity::class.java)

                // 데이터를 전달
                intent.putExtra("reservationId", "${binding.item2ReservationId.text}")
                intent.putExtra("reservationDate", "${binding.item2ReservationDate.text}")
                intent.putExtra("reservationTime", "${ binding.itemReservationTime.text}")
                intent.putExtra("username", "${binding.itemUsername.text}")
                intent.putExtra("phone", "${binding.itemPhone.text}")
                intent.putExtra("address", "${binding.itemAddress.text}")
                intent.putExtra("name", "${ binding.itemName.text}")
                intent.putExtra("price", "${ binding.itemPrice.text}")
                // 추가적으로 전달할 데이터가 있으면 여기에 작성

                // 새로운 Activity 시작
                context.startActivity(intent)
            }

//            val imageUrl = "http://10.100.201.87:8080/items/${item.itemRepImageId}/itemImageObjectId"
            val imageUrl = "http://192.168.219.200:8080/items/${item.itemRepImageId}/itemImageObjectId"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.user_basic)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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