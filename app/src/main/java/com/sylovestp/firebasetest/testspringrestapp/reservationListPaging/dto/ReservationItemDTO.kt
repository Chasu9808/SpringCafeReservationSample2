package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ReservationItemDTO(
    val reservationId: Long,
    val reservationItemId: Long,
    val reservationDate: LocalDate,
    val reservationTime: String,
    val username: String,
    val phone: String,
    val address: String,
    val name: String,
    val payStatus: String,      // 입금 상태
    val price: BigDecimal,
    val description: String,
    val itemRepImageId: String?,
    val itemAdd1ImageId: String?,
    val itemAdd2ImageId: String?,
    val itemAdd3ImageId: String?,
    val itemAdd4ImageId: String?
)