package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.LoginRequest
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto.ItemListDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface INetworkService {

    // 이미지 분류
    @Multipart
    @POST("/api/ai/predict")
    fun predictImage(
        @Part image: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<PredictionResult>

    // 회원 수정
    @Multipart
    @PUT("/api/users/{id}/update")
    fun updateUser(
        @Path("id") id: Long,                         // 사용자 ID 경로 변수
        @Part("user") user: RequestBody,          // JSON 데이터
        @Part profileImage: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<ResponseBody>

    // 회원 탈퇴
    @DELETE("/api/users/{id}")
    fun deleteUser(
        @Path("id") id: Long,                         // 사용자 ID 경로 변수
    ): Call<ResponseBody>

    // 회원가입시
    @Multipart
    @POST("/public/users")
//    fun registerUser(@Body userDTO: UserDTO): Call<Void>
    fun registerUser(
        @Part("user") user: RequestBody,          // JSON 데이터
        @Part profileImage: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<ResponseBody>

    // 로그인시 이용.
    @POST("/generateToken")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // 버전1
    @GET("/api/users/page")
    fun getItems(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<PageResponse<UserItem>>

    //버전2
    // 전체 조회, 사용자 정보
    @GET("/api/users/page")
    suspend fun getItems2(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<UserItem>>

    //전체 조회, 예약 현황
    @GET("/api/reservation-items/page")
    suspend fun getItemList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<ItemListDTO>>

    // 나의 정보 조회, 예약 현황
    @GET("/api/reservation-items/myPage")
    suspend fun getItemListMypage(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("username") username: String // username 파라미터 추가
    ): Response<PageResponse<ItemListDTO>>

    //아이디 중복 검사
    @POST("/public/users/check-username")
    suspend fun checkUsername(@Body request: Map<String?, String?>?): Response<MutableMap<String?, Boolean?>?>
}