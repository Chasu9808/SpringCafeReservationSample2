package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityLoginBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainBinding
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import com.sylovestp.firebasetest.testspringrestapp.viewModel.LoginViewModel
import com.sylovestp.firebasetest.testspringrestapp.viewModelFactory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var apiService: INetworkService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


// 자동 로그인 여부 확인
        val isAutoLogin = sharedPreferences.getBoolean("auto_login", false)

        if (isAutoLogin) {
            // 자동 로그인이 설정된 경우, 바로 메인 액티비티로 이동
            val intent = Intent(this, MainFragmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginJoinBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginLoginBtn.setOnClickListener {
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()

            loginViewModel.login(username, password)
        }

        loginViewModel.loginResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()

                // 로그인 성공 후 자동 로그인 체크 여부 저장
                val isAutoLoginChecked = binding.autoLoginCheckBox.isChecked  // 체크박스 상태 확인

                if (isAutoLoginChecked) {
                    // 자동 로그인이 체크된 경우
                    sharedPreferences.edit()
                        .putBoolean("auto_login", true)
                        .apply()
                } else {
                    // 자동 로그인이 체크되지 않은 경우
                    sharedPreferences.edit()
                        .putBoolean("auto_login", false)
                        .apply()
                }

                // 로그인 성공 시 다음 화면으로 이동
                startActivity(Intent(this, MainFragmentActivity::class.java))
                finish()
            } else {
                // 로그인 실패 시 메시지 표시
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }


    } //onCreate

    private val loginViewModel: LoginViewModel by viewModels {
        val loginRepository = LoginRepository(apiService, sharedPreferences)
        LoginViewModelFactory(loginRepository)
    }


}