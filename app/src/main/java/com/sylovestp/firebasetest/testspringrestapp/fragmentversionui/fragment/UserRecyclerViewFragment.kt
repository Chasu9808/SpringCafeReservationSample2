package com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentUserRecyclerViewBinding
import com.sylovestp.firebasetest.testspringrestapp.paging.adapter.UserAdapter
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModel
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication


class UserRecyclerViewFragment : Fragment() {


    private lateinit var apiService: INetworkService
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var binding: FragmentUserRecyclerViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 View Binding 초기화
        binding = FragmentUserRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MyApplication에서 apiService 가져오기
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, UserViewModelFactory(apiService))
            .get(UserViewModel::class.java)

        // RecyclerView 어댑터 및 레이아웃 설정
        adapter = UserAdapter()
        binding.retrofitRecyclerView2.layoutManager = LinearLayoutManager(requireContext())
        binding.retrofitRecyclerView2.adapter = adapter

        // PagingData를 관찰하여 어댑터에 전달
        viewModel.userPagingData.observe(viewLifecycleOwner, { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        })
    }



}