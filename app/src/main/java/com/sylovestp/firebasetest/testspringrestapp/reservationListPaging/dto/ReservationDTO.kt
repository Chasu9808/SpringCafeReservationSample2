package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto

import java.time.LocalDate

data class ReservationDTO(
    val reservationName: String,        // 예약자 이름
    val reservationDate: String,     // 예약 날짜
    val reservationCount: Int,          // 예약 인원
    val selectedItemName: String,       // 선택된 상품 이름
    val selectedItemPrice: String,      // 선택된 상품 가격
    val reservationTime: String,        // 예약 시간 (예: 14:00)
    val payStatus: String             // 결제 상태 (예: "paid", "unpaid")
)