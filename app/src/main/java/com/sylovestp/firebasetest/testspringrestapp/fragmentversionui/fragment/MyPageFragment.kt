package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.sylovestp.firebasetest.testspringrestapp.AddressFinder
import com.sylovestp.firebasetest.testspringrestapp.LoginActivity
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentJoinBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentMyPageBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.UserDTO
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class MyPageFragment : Fragment() {

    private lateinit var apiService: INetworkService
    private lateinit var imageView: ImageView
    private lateinit var binding: FragmentMyPageBinding
    private var imageUri: Uri? = null  // Nullable URI
    private lateinit var addressFinderLauncher: ActivityResultLauncher<Bundle>
    lateinit var sharedPreferences: SharedPreferences


    private val selectImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = result.data?.data!!

            // 이미지 로드 및 코너 둥글게 적용
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions().circleCrop())
                .into(imageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding 초기화
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = binding.userProfile

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.userProfile.setOnClickListener {
            openGallery()
        }


        // 로그인 한 유저 정보 가져오기.
        // SharedPreferences 객체를 가져옴
        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // SharedPreferences에서 "jwt_token"이라는 키로 저장된 토큰을 가져옴
        val userUserName = sharedPreferences.getString("username", null)
        val userName = sharedPreferences.getString("name", null)
        val userEmail = sharedPreferences.getString("email", null)
        val userPhone = sharedPreferences.getString("phone", null)
        val userAddress = sharedPreferences.getString("address", null)

        val userProfileImageId = sharedPreferences.getLong("id", 0)
        val imageUrl = "http://10.100.201.87:8080/api/users/${userProfileImageId}/profileImage"
//        val imageUrl = "http://192.168.219.200:8080/api/users/${userProfileImageId}/profileImage"
        Glide.with(requireContext())
            .load(imageUrl)
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.user_basic)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.userProfile)

        binding.userUsername.setText(userUserName)
        binding.userName.setText(userName)
        binding.userEmail.setText(userEmail)
        binding.userPhone.setText(userPhone)
        binding.userAddress.setText(userAddress)


        // 회원 수정
        binding.updateUserBtn.setOnClickListener {
            // 다이얼로그를 생성하여 로그아웃 여부 확인
            AlertDialog.Builder(requireContext())
                .setTitle("회원수정")
                .setMessage("정말 회원수정 하시겠습니까?")
                .setPositiveButton("확인") { dialog, _ ->


                    val userId = sharedPreferences.getLong("id", 0)
                    val username = binding.userUsername.text.toString().trim()
                    val name = binding.userName.text.toString().trim()
                    val password = binding.userPassword1.text.toString().trim()
                    val password2 = binding.userPassword2.text.toString().trim()
                    val email = binding.userEmail.text.toString().trim()
                    val phone = binding.userPhone.text.toString().trim()
                    val address = binding.userAddress.text.toString().trim()
                    val detailAddress = binding.userAddressDetail.text.toString().trim()
                    val fullAddress = "$address $detailAddress".trim()

                    if (password.length < 6) {
                        binding.userPassword1.error = "Password must be at least 6 characters"
                        return@setPositiveButton
                    }

                    if (password2.length < 6) {
                        binding.userPassword2.error = "Password must be at least 6 characters"
                        return@setPositiveButton
                    }

                    // 비밀번호 유효성 체크
                    if (password.isEmpty()) {
                        binding.userPassword1.error = "Password is required"
                        return@setPositiveButton
                    }

                    if (password2.isEmpty()) {
                        binding.userPassword2.error = "Please confirm your password"
                        return@setPositiveButton
                    }

                    if (password != password2) {
                        binding.userPassword2.error = "Passwords do not match"
                        return@setPositiveButton
                    }

                    if (username.isEmpty()) {
                        binding.userName.error = "username is required"
                        return@setPositiveButton
                    }

                    if (name.isEmpty()) {
                        binding.userName.error = "name is required"
                        return@setPositiveButton
                    }

                    if (email.isEmpty()) {
                        binding.userEmail.error = "email is required"
                        return@setPositiveButton
                    }

                    val userDTO = UserDTO(username, name, password, email, phone, fullAddress)


                    Toast.makeText(requireContext(), "$username, $password, $email, $imageUri", Toast.LENGTH_SHORT).show()

                    // 이미지가 있는 경우, 없는 경우
                    imageUri?.let { uri ->
                        // imageUri가 null이 아닐 경우
                        processImage(userDTO, uri)
                    } ?: run {
                        // imageUri가 null일 경우
                        processImage2(userDTO)
                    }

                    // 로그인 액티비티로 이동
                    val intent = Intent(requireContext(), MainFragmentActivity::class.java)
                    startActivity(intent)

                    // 현재 액티비티 종료
                    requireActivity().finish()

                    dialog.dismiss() // 다이얼로그 닫기
                }
                .setNegativeButton("취소") { dialog, _ ->
                    // 취소 버튼 클릭 시 다이얼로그 닫기
                    dialog.dismiss()
                }
                .show()

        }

        //회원탈퇴
        binding.deleteUserBtn.setOnClickListener {
            // 다이얼로그를 생성하여 로그아웃 여부 확인
            AlertDialog.Builder(requireContext())
                .setTitle("회원탈퇴")
                .setMessage("정말 회원탈퇴 하시겠습니까?")
                .setPositiveButton("확인") { dialog, _ ->


                    val userId = sharedPreferences.getLong("id", 0)
                    val password = binding.userPassword1.text.toString().trim()
                    val password2 = binding.userPassword2.text.toString().trim()

                    if (password.length < 6) {
                        binding.userPassword1.error = "Password must be at least 6 characters"
                        return@setPositiveButton
                    }

                    if (password2.length < 6) {
                        binding.userPassword2.error = "Password must be at least 6 characters"
                        return@setPositiveButton
                    }

                    // 비밀번호 유효성 체크
                    if (password.isEmpty()) {
                        binding.userPassword1.error = "Password is required"
                        return@setPositiveButton
                    }

                    if (password2.isEmpty()) {
                        binding.userPassword2.error = "Please confirm your password"
                        return@setPositiveButton
                    }

                    if (password != password2) {
                        binding.userPassword2.error = "Passwords do not match"
                        return@setPositiveButton
                    }

                    //로직처리
                    deleteUser()

                    dialog.dismiss() // 다이얼로그 닫기
                }
                .setNegativeButton("취소") { dialog, _ ->
                    // 취소 버튼 클릭 시 다이얼로그 닫기
                    dialog.dismiss()
                }
                .show()

        }

        // ActivityResultLauncher 등록
        addressFinderLauncher = registerForActivityResult(AddressFinder.contract) { result ->
            if (result != Bundle.EMPTY) {
                val address = result.getString(AddressFinder.ADDRESS)
                val zipCode = result.getString(AddressFinder.ZIPCODE)
                val editableText: Editable = Editable.Factory.getInstance().newEditable("[$zipCode] $address")
                binding.userAddress.text = editableText
            }
        }

        binding.findAddressBtn.setOnClickListener {
            addressFinderLauncher.launch(Bundle())
        }
    }

    //회원 정보 수정 형식으로 변경 필요.
    // INetworkService
    // registerUser , 변경.
    // updateUser , 메서드 확인
    private fun uploadData(user: RequestBody, profileImage: MultipartBody.Part?) {
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()
        //
        val userId = sharedPreferences.getLong("id", 0)
        val call = apiService.updateUser(userId,user, profileImage)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show()

                    sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                    // SharedPreferences 값 삭제
                    sharedPreferences.edit().clear().apply()

                    Toast.makeText(requireContext(), "다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                    // 로그인 액티비티로 이동
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    // 현재 액티비티 종료
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Failed to updated user: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteUserData() {
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()
        //
        val userId = sharedPreferences.getLong("id", 0)
        val call = apiService.deleteUser(userId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show()

                    sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                    // SharedPreferences 값 삭제
                    sharedPreferences.edit().clear().apply()

                    Toast.makeText(requireContext(), "다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                    // 로그인 액티비티로 이동
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    // 현재 액티비티 종료
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Failed to deleted user: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createRequestBodyFromDTO(userDTO: UserDTO): RequestBody {
        val gson = Gson()
        val json = gson.toJson(userDTO)
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun createMultipartBodyFromBytes(imageBytes: ByteArray): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData("profileImage", "image.jpg", requestFile)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun deleteUser() {
        GlobalScope.launch(Dispatchers.IO) {
            try {

                deleteUserData()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "delete user successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error delete user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processImage(userDTO: UserDTO, uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val userRequestBody = createRequestBodyFromDTO(userDTO)
                val resizedBitmap = getResizedBitmap(uri, 200, 200)
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val profileImagePart = createMultipartBodyFromBytes(imageBytes)

                uploadData(userRequestBody, profileImagePart)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Image processed successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // imageUri가 null일 때 호출되는 함수
    private fun processImage2(userDTO: UserDTO) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val userRequestBody = createRequestBodyFromDTO(userDTO)

                // 파일 없이 JSON 데이터만 전송
                uploadData(userRequestBody, null)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Data processed successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error processing data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private suspend fun getResizedBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@MyPageFragment)
                .asBitmap()
                .load(uri)
                .override(width, height)
                .submit()

            futureTarget.get()
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
