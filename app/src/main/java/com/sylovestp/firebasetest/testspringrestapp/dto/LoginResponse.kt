package com.sylovestp.firebasetest.testspringrestapp.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val email: String,
    val profileImageId: String,
    val name: String,
    val phone: String,
    val address: String,
    val social: String,
//    val profileImageServer: String,
)