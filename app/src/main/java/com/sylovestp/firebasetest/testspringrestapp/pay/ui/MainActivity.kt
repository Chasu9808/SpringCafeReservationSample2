package com.sylovestp.firebasetest.testspringrestapp.pay.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.sylovestp.firebasetest.testspringrestapp.pay.ViewModel
import com.iamport.sdk.BuildConfig
import com.iamport.sdk.domain.core.Iamport
import com.iamport.sdk.domain.utils.EventObserver
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityLoginBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMain2Binding

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: View

    private val viewModel: ViewModel by viewModels()
    private val paymentFragment = PaymentFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Iamport.init(this)

        // SDK 웹 디버깅을 위해 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
        // Get data from the intent
        val reservationId = intent.getStringExtra("reservationId")
        val reservationDate = intent.getStringExtra("reservationDate")
        val reservationTime = intent.getStringExtra("reservationTime")
        val username = intent.getStringExtra("username")
        val phone = intent.getStringExtra("phone")
        val address = intent.getStringExtra("address")
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")

        Log.d("lsy ReservationDetails", "Reservation ID: $reservationId")
        Log.d("lsy ReservationDetails", "Reservation Date: $reservationDate")
        Log.d("lsy ReservationDetails", "Reservation Time: $reservationTime")
        Log.d("lsy ReservationDetails", "Username: $username")
        Log.d("lsy ReservationDetails", "Phone: $phone")
        Log.d("lsy ReservationDetails", "Address: $address")
        Log.d("lsy ReservationDetails", "Name: $name")
        Log.d("lsy ReservationDetails", "Price: $price")

        val bundle = Bundle()
        bundle.putString("reservationId", reservationId)
        bundle.putString("reservationDate", reservationDate)
        bundle.putString("reservationTime", reservationTime)
        bundle.putString("username", username)
        bundle.putString("phone", phone)
        bundle.putString("address", address)
        bundle.putString("name", name)
        bundle.putString("price", price)

        paymentFragment.arguments = bundle

        // use fragment
//        replaceFragment(paymentFragment)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, paymentFragment).commit()

        mainLayout = findViewById(R.id.container)

    }//onCreate

    override fun onStart() {
        super.onStart()

        viewModel.resultCallback.observe(this, EventObserver {
            replaceFragment(PaymentResultFragment())
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("SAMPLE", "MainActivity onDestroy")
    }

    fun replaceFragment(moveToFragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, moveToFragment).addToBackStack(null).commit()
    }

    fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
//        Iamport.catchUserLeave() // TODO SDK 백그라운드 작업 중지를 위해서 onUserLeaveHint 에서 필수 호출!
    }


}