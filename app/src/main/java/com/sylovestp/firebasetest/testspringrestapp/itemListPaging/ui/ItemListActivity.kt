package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityItemListBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.ItemListViewBinding
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.adapter.ItemListAdapter
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.factory.ItemListViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.itemListPaging.viewModel.ItemListViewModel
import com.sylovestp.firebasetest.testspringrestapp.paging.adapter.UserAdapter
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModel
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication

class ItemListActivity : AppCompatActivity() {
    private lateinit var apiService: INetworkService
    private lateinit var viewModel: ItemListViewModel
    private lateinit var adapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        viewModel = ViewModelProvider(this, ItemListViewModelFactory(apiService))
            .get(ItemListViewModel::class.java)

        adapter = ItemListAdapter()
        binding.retrofitRecyclerView3.layoutManager = LinearLayoutManager(this)
        binding.retrofitRecyclerView3.adapter = adapter

        viewModel.itemListPagingData.observe(this, { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        })

    } //onCreate

}