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
import com.sylovestp.firebasetest.testspringrestapp.fragmentversionui.MainFragmentActivity
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter.ItemListAdapter
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.factory.ItemListViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.viewModel.ItemListViewModel
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.adapter.ReservationListAdapter
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.ReservationListDTO
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
//        adapter = ReservationListAdapter()
        //수정된 코드
        // 어댑터 설정 및 클릭 리스너에서 데이터 처리
        adapter = ReservationListAdapter { item ->
            // 클릭된 아이템 데이터를 프래그먼트 A에서 처리
            navigateToFragmentB(item)
        }
        binding.retrofitRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.retrofitRecyclerView.adapter = adapter

        // 데이터 로드 및 어댑터에 전달
        loadData()

    }

    private fun loadData() {
        // ViewModel의 PagingData 관찰
        viewModel.reservationListPagingData.observe(viewLifecycleOwner, { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        })
    }

    // 프래그먼트 B로 데이터 전달 및 화면 이동
    private fun navigateToFragmentB(item: ReservationListDTO) {
        val bundle = Bundle().apply {
            putLong("id",item.id)
            putString("itemName", item.name)
            putInt("itemPrice", item.price.toInt())
            putString("itemDescription", item.description)
            putString("imageUrl", "http://192.168.219.200:8080/items/${item.itemRepImageId}/itemImageObjectId")
            putString("imageUrl2", "http://192.168.219.200:8080/items/${item.itemAdd1ImageId}/itemImageObjectId")
            putString("imageUrl3", "http://192.168.219.200:8080/items/${item.itemAdd2ImageId}/itemImageObjectId")
            putString("imageUrl4", "http://192.168.219.200:8080/items/${item.itemAdd3ImageId}/itemImageObjectId")
            putString("imageUrl5", "http://192.168.219.200:8080/items/${item.itemAdd4ImageId}/itemImageObjectId")
        }

        val fragmentB = ReservationDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentB)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainFragmentActivity)?.hideTabs()  // Fragment 1 이외의 프래그먼트에서는 탭 숨김
    }//


}