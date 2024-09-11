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

class MainFragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                // 프래그먼트 전환
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택되지 않았을 때 수행할 동작 (필요하면 구현)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭을 다시 선택했을 때 수행할 동작 (필요하면 구현)
            }
        })

        // 탭 아이콘 및 텍스트 설정
        setupTabIcons()
    }

    // 탭에 아이콘과 텍스트 설정하는 함수
    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.home_24px)?.text = "홈"
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.featured_seasonal_and_gifts_24px)?.text = "혜택"
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.local_mall_24px)?.text = "스토어"
        binding.tabLayout.getTabAt(3)?.setIcon(R.drawable.photo_album_24px)?.text = "포토몰"
    }
}