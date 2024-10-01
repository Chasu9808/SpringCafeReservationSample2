package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentDetailImage1Binding
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentPage1Binding


class DetailImage1Fragment : Fragment() {

    private lateinit var binding: FragmentDetailImage1Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩을 사용해 레이아웃을 인플레이트합니다.
        binding = FragmentDetailImage1Binding.inflate(inflater, container, false)

        // 전달받은 이미지 URL을 가져옴
        val imageUrl = arguments?.getString("imageUrl")
        Glide.with(this)
            .load(imageUrl)
            .into(binding.image)

        return binding.root
    }


}