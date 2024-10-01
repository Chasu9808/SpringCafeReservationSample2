package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.sylovestp.firebasetest.testspringrestapp.AddressFinder
import com.sylovestp.firebasetest.testspringrestapp.LoginActivity
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentJoinBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.UserDTO
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity
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

class JoinFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var binding: FragmentJoinBinding
    private var imageUri: Uri? = null  // Nullable URI
    private lateinit var addressFinderLauncher: ActivityResultLauncher<Bundle>

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
        binding = FragmentJoinBinding.inflate(inflater, container, false)
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

        binding.joinBtn.setOnClickListener {

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
                return@setOnClickListener
            }

            if (password2.length < 6) {
                binding.userPassword2.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            // 비밀번호 유효성 체크
            if (password.isEmpty()) {
                binding.userPassword1.error = "Password is required"
                return@setOnClickListener
            }

            if (password2.isEmpty()) {
                binding.userPassword2.error = "Please confirm your password"
                return@setOnClickListener
            }

            if (password != password2) {
                binding.userPassword2.error = "Passwords do not match"
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.userName.error = "username is required"
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                binding.userName.error = "name is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.userEmail.error = "email is required"
                return@setOnClickListener
            }

            val userDTO = UserDTO(username, name, password, email, phone, fullAddress,null,null)


            Toast.makeText(requireContext(), "$username, $password, $email, $imageUri", Toast.LENGTH_SHORT).show()

            // 이미지가 있는 경우, 없는 경우
            imageUri?.let { uri ->
                // imageUri가 null이 아닐 경우
                processImage(userDTO, uri)
            } ?: run {
                // imageUri가 null일 경우
                processImage2(userDTO)
            }
        }

        binding.loginBtn.setOnClickListener {
            val loginFragment = LoginFragment()  // 이동하려는 프래그먼트 생성
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)  // 현재 프래그먼트를 JoinFragment로 교체
                .addToBackStack(null)  // 백스택에 추가하여 뒤로가기 시 이전 프래그먼트로 돌아갈 수 있음
                .commit()  // 트랜잭션 커밋
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

    private fun uploadData(user: RequestBody, profileImage: MultipartBody.Part?) {
        val networkService = (requireActivity().applicationContext as MyApplication).networkService
        val call = networkService.registerUser(user, profileImage)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_SHORT).show()
                    val fragmentOne = FragmentOne()  // 이동하려는 프래그먼트 생성
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragmentOne)  // 현재 프래그먼트를 JoinFragment로 교체
                        .addToBackStack(null)  // 백스택에 추가하여 뒤로가기 시 이전 프래그먼트로 돌아갈 수 있음
                        .commit()  // 트랜잭션 커밋
                } else {
                    Toast.makeText(requireContext(), "Failed to create user: ${response.code()}", Toast.LENGTH_SHORT).show()
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
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@JoinFragment)
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

    override fun onResume() {
        super.onResume()
        (activity as? MainFragmentActivity)?.hideTabs()  // Fragment 1 이외의 프래그먼트에서는 탭 숨김
    }//
}
