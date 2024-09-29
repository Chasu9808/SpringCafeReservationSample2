package com.sylovestp.firebasetest.testspringrestapp.pay.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.iamport.sdk.data.sdk.IamPortResponse
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentPaymentResultBinding
import com.sylovestp.firebasetest.testspringrestapp.pay.PaymentResultData

class PaymentResultFragment : Fragment() {

    private lateinit var binding: FragmentPaymentResultBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentResultBinding.inflate(inflater, container, false)
        initStart()
        return binding?.root
    }

    private fun initStart() {
        super.onStart()
        val impResponse = PaymentResultData.result
        val resultText = if (isSuccess(impResponse)) "결제성공" else "결제실패"
        val color = if (isSuccess(impResponse)) R.color.md_green_200 else R.color.fighting

        val tv = binding.resultMessage

        tv.setTextColor(ContextCompat.getColor(requireContext(), color))
        tv.text = "$resultText\n${GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(impResponse)}"
    }

    private fun isSuccess(iamPortResponse: IamPortResponse?): Boolean {
        if (iamPortResponse == null) {
            return false
        }
        return iamPortResponse.imp_success == true || iamPortResponse.success == true || (iamPortResponse.error_code == null && iamPortResponse.code == null)
    }


}