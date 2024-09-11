package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import com.sylovestp.firebasetest.testspringrestapp.JoinActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentLoginBinding
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import com.sylovestp.firebasetest.testspringrestapp.viewModel.LoginViewModel
import com.sylovestp.firebasetest.testspringrestapp.viewModelFactory.LoginViewModelFactory

class LoginFragment : Fragment() {
    private lateinit var apiService: INetworkService
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        val loginRepository = LoginRepository(apiService, sharedPreferences)
        LoginViewModelFactory(loginRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FragmentLoginBinding을 인플레이트합니다.
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // API 서비스 및 SharedPreferences 초기화
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireContext())
        apiService = myApplication.getApiService()

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // WindowInsets 설정
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 회원가입 버튼 클릭 리스너
        binding.loginJoinBtn.setOnClickListener {
            val intent = Intent(requireActivity(), JoinActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // 로그인 버튼 클릭 리스너
        binding.loginLoginBtn.setOnClickListener {
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()

            loginViewModel.login(username, password)
        }

        // 로그인 결과 처리
        loginViewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show()
                // 로그인 성공 시 다음 화면으로 이동
                startActivity(Intent(requireContext(), MainFragmentActivity::class.java))
                requireActivity().finish()
            } else {
                // 로그인 실패 시 메시지 표시
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
