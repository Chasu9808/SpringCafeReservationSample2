package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentFour
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentOne
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentThree
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.FragmentTwo

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4 // 프래그먼트의 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentOne()
            1 -> FragmentTwo()
            2 -> FragmentThree()
            3 -> FragmentFour()
            else -> FragmentOne()
        }
    }
}
