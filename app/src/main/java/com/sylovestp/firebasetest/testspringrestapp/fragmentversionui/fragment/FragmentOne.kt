package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
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

        // 액티비티 버전 메인 ui
        binding.activityUiVersionIcon.setOnClickListener {
            val intent = Intent(requireContext(), com.sylovestp.firebasetest.testspringrestapp.MainActivity::class.java)
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

        // SharedPreferences 객체를 가져옴
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // SharedPreferences에서 "jwt_token"이라는 키로 저장된 토큰을 가져옴
        val userName = sharedPreferences.getString("name", null)

        val userEmail = sharedPreferences.getString("email", null)

        val userProfileImageId = sharedPreferences.getLong("id", 0)
        Log.d("lsy userProfileImageId","${userProfileImageId}")


        binding.userName.text = userName
        binding.userEmail.text = userEmail

// 이미지 URL 설정
        val imageUrl = "http://192.168.219.200:8080/api/users/${userProfileImageId}/profileImage"

        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.user_basic)
            .into(binding.userProfileImage)


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