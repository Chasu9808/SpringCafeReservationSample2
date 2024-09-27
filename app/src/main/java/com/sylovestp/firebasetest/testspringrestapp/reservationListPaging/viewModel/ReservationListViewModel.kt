package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.ReservationListDTO
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.pagingSource.ReservationListPagingSource
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class ReservationListViewModel(private val apiService: INetworkService) : ViewModel() {

    val reservationListPagingData : LiveData<PagingData<ReservationListDTO>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { ReservationListPagingSource(apiService) }
    ).liveData
}