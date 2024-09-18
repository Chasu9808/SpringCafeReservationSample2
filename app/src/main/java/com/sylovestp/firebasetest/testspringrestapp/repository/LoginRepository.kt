package com.sylovestp.firebasetest.testspringrestapp.repository

import android.content.SharedPreferences
import android.util.Log
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginRequest
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class LoginRepository(private val apiService: INetworkService, private val sharedPreferences: SharedPreferences) {

    suspend fun login(username: String, password: String): Boolean {
        val loginRequest = LoginRequest(username, password)
        val response = apiService.login(loginRequest)

        return if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                Log.d("lsy Response Body", body.toString()) // 응답 전체를 확인
            val accessToken = response.body()?.accessToken
            val refreshToken = response.body()?.refreshToken
            val username = response.body()?.username

            val email = response.body()?.email
            val profileImageId = response.body()?.profileImageId
            val name = response.body()?.name
            val phone = response.body()?.phone
            val address = response.body()?.address
            val social = response.body()?.social
            val id = response.body()?.id
//            val profileImageServer = response.body()?.profileImageServer
            Log.d("lsy name ","${name}" )
            Log.d("lsy address ","${address}" )
            Log.d("lsy name", String(name?.toByteArray(Charsets.UTF_8) ?: byteArrayOf(), Charsets.UTF_8))
            Log.d("lsy address", String(address?.toByteArray(Charsets.UTF_8) ?: byteArrayOf(), Charsets.UTF_8))


            // JWT 토큰을 SharedPreferences에 저장
            sharedPreferences.edit().putString("jwt_token", accessToken).apply()
            sharedPreferences.edit().putString("refreshToken", refreshToken).apply()
            sharedPreferences.edit().putString("username", username).apply()

            sharedPreferences.edit().putString("email", email).apply()
            sharedPreferences.edit().putString("profileImageId", profileImageId).apply()
            sharedPreferences.edit().putString("name", name).apply()
            sharedPreferences.edit().putString("phone", phone).apply()
            sharedPreferences.edit().putString("address", address).apply()
            sharedPreferences.edit().putString("social", social).apply()
            if (id != null) {
                sharedPreferences.edit().putLong("id", id).apply()
            }
//            sharedPreferences.edit().putString("profileImageServer", profileImageServer).apply()

            true
        } else {
            false
        }
    }
}