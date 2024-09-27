package com.sylovestp.firebasetest.testspringrestapp.reservationListPaging.dto

import java.math.BigDecimal

data class ReservationListDTO(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val description: String?,
    val itemRepImageId: String?,
    val itemAdd1ImageId: String?,
    val itemAdd2ImageId: String?,
    val itemAdd3ImageId: String?,
    val itemAdd4ImageId: String?
)