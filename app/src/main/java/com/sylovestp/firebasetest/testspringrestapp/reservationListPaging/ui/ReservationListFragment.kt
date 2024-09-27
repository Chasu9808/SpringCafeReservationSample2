package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationItemListBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.FragmentReservationListBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter.ItemListAdapter
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.factory.ItemListViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.viewModel.ItemListViewModel
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.adapter.ReservationListAdapter
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.factory.ReservationListViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.viewModel.ReservationListViewModel
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication

class ReservationListFragment : Fragment() {

    private lateinit var apiService: INetworkService
    private lateinit var viewModel: ReservationListViewModel //
    private lateinit var adapter: ReservationListAdapter //
    private lateinit var binding: FragmentReservationListBinding //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding 초기화
        binding = FragmentReservationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Application 및 API Service 초기화
        val myApplication = requireActivity().applicationContext as MyApplication
        myApplication.initialize(requireActivity())
        apiService = myApplication.getApiService()

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, ReservationListViewModelFactory(apiService))
            .get(ReservationListViewModel::class.java)

        // RecyclerView 어댑터 설정
        adapter = ReservationListAdapter()
        binding.retrofitRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.retrofitRecyclerView.adapter = adapter

        // ViewModel의 PagingData 관찰
        viewModel.reservationListPagingData.observe(viewLifecycleOwner, { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        })
    }


}