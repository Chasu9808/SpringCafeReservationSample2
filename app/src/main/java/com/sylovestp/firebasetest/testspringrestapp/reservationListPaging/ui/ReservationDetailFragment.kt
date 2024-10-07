package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationDetailBinding
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.ReservationDTO
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.TimeSlotAvailableDTO
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ReservationDetailFragment : Fragment() {
    private lateinit var apiService: INetworkService
    private lateinit var binding: FragmentReservationDetailBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter

    // 전역 변수 선언
    private var imageUrl: String? = null
    private var imageUrl2: String? = null
    private var imageUrl3: String? = null
    private var imageUrl4: String? = null
    private var imageUrl5: String? = null
    private var itemId: Long? = null
    private var userName2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReservationDetailBinding.inflate(inflater, container, false)

        //뷰페이저 설정
        viewPager = binding.viewPager
        // 어댑터 설정.
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // 현재 날짜를 가져오기 위한 Calendar 인스턴스
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 날짜 선택 (현재 날짜로 초기화)
        binding.datePicker.init(currentYear, currentMonth, currentDay) { _, year, monthOfYear, dayOfMonth ->
            // 날짜가 선택될 때 동작
//            val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
            val selectedDate = "$year-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
            // 필요한 경우 이 날짜를 사용
            val timeSlotAvailableDTO = TimeSlotAvailableDTO(itemId.toString(), selectedDate)

            // API 호출
            fetchAvailableTimeSlots(timeSlotAvailableDTO)
        }
        // 과거 날짜 선택 방지: 오늘 날짜 이후로만 선택 가능
        binding.datePicker.minDate = calendar.timeInMillis

        // 시간대 배열을 0:00 ~ 24:00까지 설정
        val timeRanges = Array(24) { i -> "${i}:00 ~ ${i + 1}:00" }

        // Spinner의 어댑터 설정
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeRanges)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeUnitSpinner.adapter = spinnerAdapter

        // Spinner에서 선택된 시간대에 따라 UI 업데이트
        binding.timeUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // 선택된 시간대에 따라 TextView 업데이트
                binding.timeRange.text = "예약 가능 시간: ${timeRanges[position]}"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }



        // 사용자 수 선택
        binding.userCountPicker.apply {
            minValue = 1
            maxValue = 10
            wrapSelectorWheel = true
            setOnValueChangedListener { _, _, newVal ->
                // 사용자 수가 변경될 때 동작
                val selectedUserCount = newVal
                // 필요한 경우 이 값을 사용
            }
        }

        // SharedPreferences 객체를 가져옴
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // SharedPreferences에서 "jwt_token"이라는 키로 저장된 토큰을 가져옴
        val userName = sharedPreferences.getString("name", null)
        userName2 = sharedPreferences.getString("username", null)

        binding.reservationUserName.text = userName

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bundle로부터 데이터 받기
        val itemName = arguments?.getString("itemName")
        val itemPrice = arguments?.getInt("itemPrice")
        val itemDescription = arguments?.getString("itemDescription")
        itemId = arguments?.getLong("id")
        // arguments로 전달된 값을 초기화
        imageUrl = arguments?.getString("imageUrl")
        imageUrl2 = arguments?.getString("imageUrl2")
        imageUrl3 = arguments?.getString("imageUrl3")
        imageUrl4 = arguments?.getString("imageUrl4")
        imageUrl5 = arguments?.getString("imageUrl5")

        // 받은 데이터를 UI에 설정
        binding.itemName.text = itemName
        binding.itemPrice.text = itemPrice?.toString()
        binding.itemDescription.text = itemDescription

        // 버튼 클릭 리스너 안에서 데이터를 수집하고 전송하는 코드
        binding.reservateBtn.setOnClickListener {
            // 선택한 날짜와 시간대, 사용자 수, 그리고 사용자 이름 가져오기
            val selectedDate = "${binding.datePicker.year}-${String.format("%02d", binding.datePicker.month + 1)}-${String.format("%02d", binding.datePicker.dayOfMonth)}"
            val timeString = binding.timeRange.text.toString() // "Reservation available time: 13:00 ~ 14:00"
            val timePart = timeString.split(" ")[3] // "13:00"
            val selectedHour = timePart.split(":")[0] // "13"

            val selectedHourInt = selectedHour.toInt()
            val userCount = binding.userCountPicker.value // 사용자 수
            val userName = binding.reservationUserName.text.toString() // 사용자 이름

            // arguments에서 전달받은 상품 이름과 가격 가져오기
            val itemName = arguments?.getString("itemName") ?: ""
            val itemPrice = arguments?.getInt("itemPrice")?.toString() ?: "0"

            // ReservationDTO 객체 생성
            val reservationDto = userName2?.let { it1 ->
                ReservationDTO(
                    reservationName = it1,         // 예약자 이름
                    reservationDate = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE).toString(), // 예약 날짜
                    reservationCount = userCount,       // 예약 인원
                    selectedItemName = itemName,        // 선택된 상품 이름
                    selectedItemPrice = itemPrice,      // 선택된 상품 가격
                    reservationTime = selectedHour, // 예약 시간
                    payStatus = "입금대기"
                )
            }
            Log.d("lsy reservationDto",": ${reservationDto}")
            // 코루틴을 이용하여 Retrofit 호출
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val myApplication = requireActivity().applicationContext as MyApplication
                    myApplication.initialize(requireActivity())
                    apiService = myApplication.getApiService()

                    // 예약 데이터를 서버로 전송
                    val response = reservationDto?.let { it1 -> apiService.createReservation(it1) }

                    // 성공적으로 예약이 처리된 경우
                    if (response != null) {
                        Toast.makeText(context, "예약 성공: ${response.reservationId}", Toast.LENGTH_SHORT).show()
                    }

                    // 응답 데이터를 활용하여 UI 업데이트 가능
                    // 예: 예약 세부 정보 표시


                } catch (e: Exception) {
                    // 네트워크 오류 또는 API 호출 실패 처리
                    Toast.makeText(context, "예약 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("lsy reservationDto error",": ${e.message}")
                }
            }
}

    }

    override fun onPause() {
        super.onPause()

        // 프래그먼트가 pause 상태에 들어갔을 때 해당 프래그먼트를 종료하는 코드
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

    // 또는 onStop에서 프래그먼트를 종료하려면 아래 코드 사용
    override fun onStop() {
        super.onStop()

        // 프래그먼트가 stop 상태에 들어갔을 때 해당 프래그먼트를 종료하는 코드
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

    // FragmentStateAdapter 클래스
    private inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 5 // 페이지 수

        override fun createFragment(position: Int): Fragment {
            // 각 페이지에 해당하는 Fragment 반환
            val fragment = when (position) {
                0 -> DetailImage1Fragment()
                1 -> DetailImage2Fragment()
                2 -> DetailImage3Fragment()
                3 -> DetailImage4Fragment()
                4 -> DetailImage5Fragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }

            // 각 프래그먼트에 이미지 URL 전달
            val args = Bundle()
            when (position) {
                0 -> args.putString("imageUrl", imageUrl)
                1 -> args.putString("imageUrl", imageUrl2)
                2 -> args.putString("imageUrl", imageUrl3)
                3 -> args.putString("imageUrl", imageUrl4)
                4 -> args.putString("imageUrl", imageUrl5)
            }
            fragment.arguments = args
            return fragment

        }
    }

    private fun fetchAvailableTimeSlots(timeSlotAvailableDTO: TimeSlotAvailableDTO) {
        // Retrofit 인스턴스 가져오기
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()


        // 코루틴을 사용한 API 호출
        lifecycleScope.launch {
            try {
                // 네트워크 요청 (비동기)
                val availableSlots = apiService.getAvailableTimeSlots(timeSlotAvailableDTO)

                // 성공 시 처리
                Log.d("lsy MyFragment", "Available Time Slots: $availableSlots")

                // 시간대 배열을 0:00 ~ 24:00까지 설정
                val timeRanges = availableSlots.map { i -> "${i}:00 ~ ${i + 1}:00" }

                // Spinner의 어댑터 설정
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeRanges)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.timeUnitSpinner.adapter = spinnerAdapter

                // Spinner에서 선택된 시간대에 따라 UI 업데이트
                binding.timeUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        // 선택된 시간대에 따라 TextView 업데이트
                        binding.timeRange.text = "예약 가능 시간: ${timeRanges[position]}"
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // 아무 항목도 선택되지 않았을 때의 처리
                    }
                }

            } catch (e: HttpException) {
                // HTTP 오류 처리
                Log.e("lsy MyFragment", "Request failed with code: ${e.code()}")
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // 네트워크 또는 기타 오류 처리
                Log.e("lsy MyFragment", "Network failure: ${e.message}")
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

}