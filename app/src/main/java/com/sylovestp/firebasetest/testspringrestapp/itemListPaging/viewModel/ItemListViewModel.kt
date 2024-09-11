package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto.ItemListDTO
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.pagingSource.ItemListPagingSource
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class ItemListViewModel (private val apiService: INetworkService) : ViewModel() {

    val itemListPagingData : LiveData<PagingData<ItemListDTO>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { ItemListPagingSource(apiService)}
    ).liveData
}