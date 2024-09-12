package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.PageFragment1
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.PageFragment2
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment.PageFragment3

class ViewPagerOneFragmentAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {

    // List of fragments to be displayed in ViewPager2
    private val fragmentList = listOf(
        PageFragment1(),
        PageFragment2(),
        PageFragment3()
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}