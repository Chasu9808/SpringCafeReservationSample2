package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity

class FragmentTwo : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainFragmentActivity)?.showTabs()  // Fragment 1 이외의 프래그먼트에서는 탭 숨김
    }//
}

