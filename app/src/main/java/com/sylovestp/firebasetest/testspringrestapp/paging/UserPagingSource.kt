package com.sylovestp.firebasetest.testspringrestapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class UserPagingSource(
    private val apiService: INetworkService
    // Int는 페이지 번호의 타입
    // UserItem은 페이징 소스에서 반환할 데이터 타입
) : PagingSource<Int, UserItem>() {

    //oad 함수는 PagingSource에서 데이터를 비동기로 로드하는 핵심 메서드
    // 데이터를 가져오는 동안 코루틴이 중단될 수 있으므로 suspend 키워드가 사용됩니다
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
        return try {
            // 처음 시작할 페이지를 0으로 설정
            val currentPage = params.key ?: 0
            // 페이지 크기를 10으로 설정
            val response = apiService.getItems2(currentPage, 10)

            if (response.isSuccessful) {
                val data = response.body()?.content ?: emptyList()
                val nextPage = if (data.isNotEmpty()) currentPage + 1 else null

                LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
//    페이지가 새로 고침될 때 어떤 페이지에서 다시 시작해야 할지를 결정하는 메서드.
//    **state.anchorPosition**은 현재의 위치를 기준으로 가장 가까운 페이지를 찾기.
    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
