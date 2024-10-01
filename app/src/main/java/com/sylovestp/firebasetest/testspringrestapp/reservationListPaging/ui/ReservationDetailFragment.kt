package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationDetailBinding

class ReservationDetailFragment : Fragment() {
    private lateinit var binding: FragmentReservationDetailBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter

    // 전역 변수 선언
    private var imageUrl: String? = null
    private var imageUrl2: String? = null
    private var imageUrl3: String? = null
    private var imageUrl4: String? = null
    private var imageUrl5: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReservationDetailBinding.inflate(inflater, container, false)

        //뷰페이저 설정
        viewPager = binding.viewPager
        // 어댑터 설정.
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bundle로부터 데이터 받기
        val itemName = arguments?.getString("itemName")
        val itemPrice = arguments?.getInt("itemPrice")
        val itemDescription = arguments?.getString("itemDescription")
        // arguments로 전달된 값을 초기화
        imageUrl = arguments?.getString("imageUrl")
        imageUrl2 = arguments?.getString("imageUrl2")
        imageUrl3 = arguments?.getString("imageUrl3")
        imageUrl4 = arguments?.getString("imageUrl4")
        imageUrl5 = arguments?.getString("imageUrl5")

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

    // FragmentStateAdapter 클래스
    private inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 5 // 페이지 수

        override fun createFragment(position: Int): Fragment {
            // 각 페이지에 해당하는 Fragment 반환
            val fragment = when (position) {
                0 -> DetailImage1Fragment()
                1 -> DetailImage2Fragment()
                2 -> DetailImage3Fragment()
                3 -> DetailImage4Fragment()
                4 -> DetailImage5Fragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }

            // 각 프래그먼트에 이미지 URL 전달
            val args = Bundle()
            when (position) {
                0 -> args.putString("imageUrl", imageUrl)
                1 -> args.putString("imageUrl", imageUrl2)
                2 -> args.putString("imageUrl", imageUrl3)
                3 -> args.putString("imageUrl", imageUrl4)
                4 -> args.putString("imageUrl", imageUrl5)
            }
            fragment.arguments = args
            return fragment

        }
    }

}