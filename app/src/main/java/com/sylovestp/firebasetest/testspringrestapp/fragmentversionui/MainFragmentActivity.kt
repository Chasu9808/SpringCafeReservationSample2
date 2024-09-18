package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var binding: ActivityMainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                val selectedFragment = when (tab.position) {
                    0 -> FragmentOne()
                    1 -> FragmentTwo()
                    2 -> FragmentThree()
                    3 -> FragmentFour()
                    else -> FragmentOne()
                }

                // 선택된 탭에 따라 아이콘 변경
                when (tab.position) {
                    0 -> tab.setIcon(R.drawable.home_24px_fill)
                    1 -> tab.setIcon(R.drawable.featured_seasonal_and_gifts_24px_fill)
                    2 -> tab.setIcon(R.drawable.local_mall_24px_fill)
                    3 -> tab.setIcon(R.drawable.photo_album_24px_fill)
                }

                // 프래그먼트 전환
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
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


    } //onCreate

}