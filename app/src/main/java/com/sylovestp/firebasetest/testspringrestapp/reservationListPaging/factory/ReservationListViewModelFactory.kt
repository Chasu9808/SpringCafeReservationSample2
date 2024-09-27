package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.viewModel.ReservationListViewModel
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class ReservationListViewModelFactory(private val apiService: INetworkService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationListViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}