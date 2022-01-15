package com.bigosvaap.util.http

import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class HttpErrorInfo(
    val httpStatus: HttpStatus,
    val path: String,
    val message: String
){
    val timestamp = ZonedDateTime.now()

    val status: Int
        get() = httpStatus.value()

    val error: String
        get() = httpStatus.reasonPhrase
}