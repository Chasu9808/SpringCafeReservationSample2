package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sylovestp.firebasetest.testspringrestapp.AiPredictActivity
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentOneBinding
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.adapter.ViewPagerAdapter
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.adapter.ViewPagerOneFragmentAdapter
import com.sylovestp.firebasetest.testspringrestapp.pay.ui.MainActivity

class FragmentOne : Fragment() {
    private lateinit var binding: FragmentOneBinding
    //auto slide, 뷰페이저2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val slideInterval: Long = 2000 // 2 seconds
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩을 사용해 레이아웃을 인플레이트합니다.
        binding = FragmentOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // user_login_icon 클릭 시 LoginFragment로 이동하는 코드
        binding.userLoginIcon.setOnClickListener {
            // 프래그먼트 전환을 위한 FragmentTransaction 사용
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment()) // LoginFragment로 전환
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()
        }

        // user_join_icon 클릭 시 JoinFragment로 이동하는 코드
        binding.userJoinIcon.setOnClickListener {
            // 프래그먼트 전환을 위한 FragmentTransaction 사용
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, JoinFragment()) // JoinFragment로 전환
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()
        }

        // user_paging_icon 클릭 시 UserRecyclerViewFragment 이동하는 코드
        binding.userPagingIcon.setOnClickListener {
            // 프래그먼트 전환을 위한 FragmentTransaction 사용
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserRecyclerViewFragment()) // UserRecyclerViewFragment 전환
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()
        }

        // ai_Image_Classify_icon 클릭 시 AiPredictFragment 이동하는 코드
        binding.aiImageClassifyIcon.setOnClickListener {
            // 프래그먼트 전환을 위한 FragmentTransaction 사용
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AiPredictFragment()) // AiPredictFragment 전환
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()
        }

        // payTestIcon 클릭 시 , 결제 액티비티로 MainActivity 이동하는 코드
        binding.payTestIcon.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        // reservationListIcon 클릭 시 ReservationItemListFragment 이동하는 코드
        binding.reservationListIcon.setOnClickListener {
            // 프래그먼트 전환을 위한 FragmentTransaction 사용
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReservationItemListFragment()) // ReservationItemListFragment 전환
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()
        }


        // Set up the ViewPager2 with an adapter
        val adapter = ViewPagerOneFragmentAdapter(this)
        binding.oneFragmentViewPager1.adapter = adapter

        // Set up the auto-slide functionality
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val totalPages = adapter.itemCount
                currentPage = (currentPage + 2) % totalPages
                binding.oneFragmentViewPager1.setCurrentItem(currentPage, true) // Smooth scroll to the next page

                // Schedule the next slide
                handler.postDelayed(this, slideInterval)
            }
        }

        // Start auto-slide
        handler.postDelayed(runnable, slideInterval)

    } //

    override fun onPause() {
        super.onPause()
        // 프래그먼트가 일시 중지되면 자동 슬라이드를 멈춤
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 활성화되면 자동 슬라이드를 재개
        handler.postDelayed(runnable, slideInterval)
    }

}