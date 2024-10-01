package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentAiPredictBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class AiPredictFragment : Fragment() {


    private lateinit var apiService: INetworkService
    private lateinit var imageView: ImageView
    private lateinit var resultView: TextView
    private var imageUri: Uri? = null  // Nullable URI

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
        // Fragment의 View Binding 초기화
        val binding = FragmentAiPredictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        val binding = FragmentAiPredictBinding.bind(view)
        imageView = binding.userProfile2
        resultView = binding.predictResultView

        // 이미지 선택 버튼 클릭 리스너
        binding.userProfile2.setOnClickListener {
            openGallery()
        }

        // 예측 버튼 클릭 리스너
        binding.predictSendBtn.setOnClickListener {
            Toast.makeText(requireContext(), " ${imageUri}", Toast.LENGTH_SHORT).show()
            imageUri?.let { processImage(it) }
        }


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    // 이미지 처리 후 서버로 전송
    private fun processImage(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 이미지 축소 및 MultipartBody.Part 생성
                val resizedBitmap = getResizedBitmap(uri, 200, 200) // 200x200 크기로 축소
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val profileImagePart = createMultipartBodyFromBytes(imageBytes)

                // 서버로 전송
                uploadData(profileImagePart)

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

    private suspend fun getResizedBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@AiPredictFragment)
                .asBitmap()
                .load(uri)
                .override(width, height)  // 지정된 크기로 축소
                .submit()

            futureTarget.get()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadData(profileImage: MultipartBody.Part?) {
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()

        val call = apiService.predictImage(profileImage)
        call.enqueue(object : Callback<PredictionResult> {
            override fun onResponse(call: Call<PredictionResult>, response: Response<PredictionResult>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "서버 전송 성공", Toast.LENGTH_SHORT).show()
                    val predictedClassLabel = "${response.body()?.predictedClassLabel}"
                    val confidence = response.body()?.confidence
                    val classConfidences1 = response.body()?.classConfidences?.get("망치")
                    val classConfidences2 = response.body()?.classConfidences?.get("공업용가위")

                    val result = """
                        결과 : $predictedClassLabel
                        정확도 : ${confidence?.let { formatToPercentage(it) }}
                        다른 클래스들의 정확도
                        망치 : ${classConfidences1?.let { formatToPercentage(it) }}
                        공업용가위 : ${classConfidences2?.let { formatToPercentage(it) }}
                    """.trimIndent()

                    resultView.text = result
                } else {
                    Toast.makeText(requireContext(), "Failed to create user: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createMultipartBodyFromBytes(imageBytes: ByteArray): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestFile)
    }

    fun formatToPercentage(value: Double): String {
        val percentageValue = value * 100
        return String.format("%.2f", percentageValue) + "%"
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainFragmentActivity)?.hideTabs()  // Fragment 1 이외의 프래그먼트에서는 탭 숨김
    }//


}