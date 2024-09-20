package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
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
    private lateinit var apiService2: INetworkService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyHash = Utility.getKeyHash(this)
        // 아래 로그값을 , 카카오 개발자 콘솔에 , 플랫폼 -> 안드로이드 -> 패키지명과, 키 해시 등록하기. 중요함.
        Log.d("lsy hash", "keyhash : ${Utility.getKeyHash(this)}")

        val myApplication = applicationContext as MyApplication
        val myApplication2 = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()
        apiService2 = myApplication2.networkService

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

        //카카오 로그인
        binding.kakaoLogin.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("lsy", "카카오톡으로 로그인 실패", error)

//                        if (error.toString().contains("statusCode=302")){
//                            loginWithKakaoAccount()
//                        }
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                        Log.i("lsy", "카카오톡으로 로그인 성공 11 loginWithKakaoAccount")
                    } else if (token != null) {
                        Log.i("lsy", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        getUserInfo()
                        startActivity(Intent(this, MainFragmentActivity::class.java))
                        finish()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                getUserInfo()
                Log.i("lsy", "카카오톡으로 로그인 성공 12 loginWithKakaoAccount")

            }

        }


    } //onCreate

    // 카카오 로그인
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("lsy", "카카오계정으로 로그인 실패", error)
//            if (error.toString().contains("statusCode=302")){
//                loginWithKakaoAccount()
//            }

        } else if (token != null) {
            Log.i("lsy", "카카오계정으로 로그인 성공 ${token.accessToken}")
            // 성공시 이동할 화면.

            startActivity(Intent(this, MainFragmentActivity::class.java))
            finish()

        }
    }

    private fun loginWithKakaoAccount(){
        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        val loginRepository = LoginRepository(apiService, sharedPreferences)
        LoginViewModelFactory(loginRepository)
    }
    // 사용자 정보 요청 함수
    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("lsy KakaoLogin", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("lsy KakaoLogin", "사용자 정보 요청 성공")
                Log.i("lsy KakaoLogin", "닉네임: ${user.kakaoAccount?.profile?.nickname}")
                Log.i("lsy KakaoLogin", "이메일: ${user.kakaoAccount?.email}")
                Log.i("lsy KakaoLogin", "프로필 사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                // 여기서 사용자 정보를 이용해 추가적인 작업 수행
            }
        }
    }


}