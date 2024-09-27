package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.pagingSource

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iamport.sdk.domain.core.Iamport.response
import com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto.ReservationListDTO
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication

class ReservationListPagingSource (
    private val apiService: INetworkService
    // Int는 페이지 번호의 타입
    // UserItem은 페이징 소스에서 반환할 데이터 타입
) : PagingSource<Int, ReservationListDTO>() {
    // 일반 코틀린 클래스 전역으로 이용해서 사용하기
    private val sharedPreferences = MyApplication.instance.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    //load 함수는 PagingSource에서 데이터를 비동기로 로드하는 핵심 메서드
    // 데이터를 가져오는 동안 코루틴이 중단될 수 있으므로 suspend 키워드가 사용됩니다
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReservationListDTO> {
        return try {
            // 처음 시작할 페이지를 0으로 설정
            val currentPage = params.key ?: 0
            // 페이지 크기를 10으로 설정
            // interface INetworkService getItemList 추가

            // username 가져오기
            val username = sharedPreferences.getString("username", "username")

            // 나의 예약 현황 조회
            val response = apiService.getItemsList(currentPage, 10)

            if (response?.isSuccessful == true) {
                val data = response?.body()?.content ?: emptyList()
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
    override fun getRefreshKey(state: PagingState<Int, ReservationListDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
