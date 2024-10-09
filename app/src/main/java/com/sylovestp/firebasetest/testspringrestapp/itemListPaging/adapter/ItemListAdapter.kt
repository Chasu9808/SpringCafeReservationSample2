package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
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
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItemListAdapter(
    private val apiService: INetworkService
) : PagingDataAdapter<ItemListDTO, ItemListAdapter.ItemListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val binding = ItemListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemListViewHolder(binding,apiService)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ItemListViewHolder(private val binding: ItemListViewBinding,private val apiService: INetworkService) : RecyclerView.ViewHolder(binding.root) {

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

            binding.cancelBtn.setOnClickListener {
                // 다이얼로그를 띄워서 삭제 여부 확인
                AlertDialog.Builder(itemView.context).apply {
                    setTitle("예약취소")
                    setMessage("예약 취소 할까요?")
                    setPositiveButton("예") { dialog, _ ->
                        // 삭제 진행 (코루틴으로 처리)
                        CoroutineScope(Dispatchers.Main).launch {
                            val result = withContext(Dispatchers.IO) {
                                try {
                                    val response = apiService.deleteReservationItem(binding.item2ReservationItemId.text.toString().toLong())
                                    response.isSuccessful
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            if (result) {
                                Toast.makeText(itemView.context, "예약 취소 성공", Toast.LENGTH_SHORT).show()
                                // 성공 시 뒤로 가기
                                val fragmentActivity = itemView.context as FragmentActivity
                                fragmentActivity.supportFragmentManager.popBackStack() // 프래그먼트 백스택에서 뒤로 가기
                            } else {
                                Toast.makeText(itemView.context, "예약 취소 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton("아니오") { dialog, _ ->
                        dialog.dismiss() // 삭제 취소
                    }
                }.show()
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