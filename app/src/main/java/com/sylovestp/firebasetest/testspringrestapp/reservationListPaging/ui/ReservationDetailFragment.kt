package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationDetailBinding

class ReservationDetailFragment : Fragment() {
    private lateinit var binding: FragmentReservationDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReservationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bundle로부터 데이터 받기
        val itemName = arguments?.getString("itemName")
        val itemPrice = arguments?.getInt("itemPrice")
        val itemDescription = arguments?.getString("itemDescription")
        val imageUrl = arguments?.getString("imageUrl")
        val imageUrl2 = arguments?.getString("imageUrl2")
        val imageUrl3 = arguments?.getString("imageUrl3")
        val imageUrl4 = arguments?.getString("imageUrl4")
        val imageUrl5 = arguments?.getString("imageUrl5")

        // 받은 데이터를 UI에 설정
        binding.itemName.text = itemName
        binding.itemPrice.text = itemPrice?.toString()
        binding.itemDescription.text = itemDescription

        // 이미지를 Glide로 로드
        Glide.with(this)
            .load(imageUrl)
            .into(binding.itemRepImage)

        Glide.with(this)
            .load(imageUrl2)
            .into(binding.itemAddImage1)

        Glide.with(this)
            .load(imageUrl3)
            .into(binding.itemAddImage2)

        Glide.with(this)
            .load(imageUrl4)
            .into(binding.itemAddImage3)

        Glide.with(this)
            .load(imageUrl5)
            .into(binding.itemAddImage4)
    }

    override fun onPause() {
        super.onPause()

        // 프래그먼트가 pause 상태에 들어갔을 때 해당 프래그먼트를 종료하는 코드
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

    // 또는 onStop에서 프래그먼트를 종료하려면 아래 코드 사용
    override fun onStop() {
        super.onStop()

        // 프래그먼트가 stop 상태에 들어갔을 때 해당 프래그먼트를 종료하는 코드
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }


}