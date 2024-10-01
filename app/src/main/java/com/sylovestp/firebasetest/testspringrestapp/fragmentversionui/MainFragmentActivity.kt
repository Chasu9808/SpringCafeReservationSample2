package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainFragmentBinding
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.adapter.ViewPagerAdapter
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentFour
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentOne
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentThree
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentTwo
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.LoginFragment

class MainFragmentActivity : AppCompatActivity() {
    // 안드로이드 스튜디오 코알라, 레이디버그로 업그레이드시, 컴파일러 오류 발생함.

    private lateinit var binding: ActivityMainFragmentBinding
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        tabLayout = binding.tabLayout

        // Add tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("홈").setIcon(R.drawable.home_24px_fill))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("혜택").setIcon(R.drawable.featured_seasonal_and_gifts_24px))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("스토어").setIcon(R.drawable.local_mall_24px))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("포토몰").setIcon(R.drawable.photo_album_24px))


        // LoginFragment 로드
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, LoginFragment())
//                .commitNow()
//        }

        // 첫 번째 프래그먼트를 기본으로 로드
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FragmentOne())
            .commit()

        // TabLayout의 탭 선택 리스너 설정
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab?.position) {
                    0 -> navigateToFragment(FragmentOne(), false) // Fragment A로 이동
                    1 -> navigateToFragment(FragmentTwo(), true) // Fragment B로 이동
                    2 -> navigateToFragment(FragmentThree(), true) // Fragment C로 이동
                    3 -> navigateToFragment(FragmentFour(), true) // Fragment D로 이동

                }

                // 선택된 탭에 따라 아이콘 변경
                when (tab.position) {
                    0 -> tab.setIcon(R.drawable.home_24px_fill)
                    1 -> tab.setIcon(R.drawable.featured_seasonal_and_gifts_24px_fill)
                    2 -> tab.setIcon(R.drawable.local_mall_24px_fill)
                    3 -> tab.setIcon(R.drawable.photo_album_24px_fill)
                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택 해제될 때 아이콘을 원래대로 변경
                if (tab != null) {
                    when (tab.position) {
                        0 -> tab.setIcon(R.drawable.home_24px)
                        1 -> tab.setIcon(R.drawable.featured_seasonal_and_gifts_24px)
                        2 -> tab.setIcon(R.drawable.local_mall_24px)
                        3 -> tab.setIcon(R.drawable.photo_album_24px)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭을 다시 선택했을 때 수행할 동작 (필요하면 구현)
            }
        })

        // 초기 화면으로 Fragment A 설정
        if (savedInstanceState == null) {
            navigateToFragment(FragmentOne(), false)
        }

        showTabs()
    } //onCreate

    //탭 뷰 보이게 설정.
    fun showTabs() {
        tabLayout.visibility = View.VISIBLE
    }

    fun hideTabs() {
        tabLayout.visibility = View.GONE
    }

    // 프래그먼트를 교체하는 메서드
    fun navigateToFragment(fragment: Fragment, useBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 프래그먼트 교체

        if (useBackStack) {
            transaction.addToBackStack(null) // 백스택에 추가
        }

        transaction.commit()
    }

    // 뒤로 가기 버튼 처리
    override fun onBackPressed() {
        // 현재 활성화된 프래그먼트가 Fragment A 인지 확인
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is FragmentOne) {
            // Fragment A에 정의된 뒤로 가기 처리를 호출
            fragment.handleBackPressed()
        } else {
            super.onBackPressed() // 다른 프래그먼트에서는 기본 동작 수행
        }
    }


}