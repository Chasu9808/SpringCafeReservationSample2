package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentOneBinding

class FragmentOne : Fragment() {
    private lateinit var binding: FragmentOneBinding
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
    }

}