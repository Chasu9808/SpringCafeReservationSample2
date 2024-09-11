package com.sylovestp.firebasetest.testspringrestapp.itemListPaging.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ItemListDTO (
    // 예약된 상품
    val reservationId: Long,
    val reservationItemId: Long,
    val reservationDate: String,
    val reservationTime: String,
    val username: String, //id 처럼 사용, 예) lsy
    val phone: String,
    val address: String,
    val name: String, // 이름 : 이상용
    val price: BigDecimal,
    val description: String,
    val itemRepImageId: String,
    val itemAdd1ImageId: String,
    val itemAdd2ImageId: String,
    val itemAdd3ImageId: String,
    val itemAdd4ImageId: String
)