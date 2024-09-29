package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationItemListBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter.ItemListAdapter
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.factory.ItemListViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.viewModel.ItemListViewModel
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication


class ReservationItemListFragment : Fragment() {


    private lateinit var apiService: INetworkService
    private lateinit var viewModel: ItemListViewModel
    private lateinit var adapter: ItemListAdapter
    private lateinit var binding: FragmentReservationItemListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding 초기화
        binding = FragmentReservationItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Application 및 API Service 초기화
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, ItemListViewModelFactory(apiService))
            .get(ItemListViewModel::class.java)

        // RecyclerView 어댑터 설정
        adapter = ItemListAdapter()
        binding.retrofitRecyclerView3.layoutManager = LinearLayoutManager(requireContext())
        binding.retrofitRecyclerView3.adapter = adapter

        // ViewModel의 PagingData 관찰
        viewModel.itemListPagingData.observe(viewLifecycleOwner, { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        })
    }

    // 또는 onStop에서 프래그먼트를 종료하려면 아래 코드 사용
    override fun onStop() {
        super.onStop()

        // 프래그먼트가 stop 상태에 들어갔을 때 해당 프래그먼트를 종료하는 코드
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

}