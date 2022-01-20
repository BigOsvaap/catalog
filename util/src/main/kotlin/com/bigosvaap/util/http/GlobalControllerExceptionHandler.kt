package com.bigosvaap.util.http

import com.bigosvaap.api.exceptions.BadRequestException
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.api.exceptions.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalControllerExceptionHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler::class.java)
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    @ResponseBody
    fun handleBadRequestExceptions(request: ServerHttpRequest, ex: BadRequestException): HttpErrorInfo {
        return createHttpErrorInfo(BAD_REQUEST, request, ex)
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundExceptions(request: ServerHttpRequest, ex: NotFoundException): HttpErrorInfo {
        return createHttpErrorInfo(NOT_FOUND, request, ex)
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException::class)
    @ResponseBody
    fun handleInvalidInputException(request: ServerHttpRequest, ex: InvalidInputException): HttpErrorInfo {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex)
    }

    private fun createHttpErrorInfo(httpStatus: HttpStatus, request: ServerHttpRequest, ex: Exception): HttpErrorInfo {
        val path = request.path.pathWithinApplication().value()
        val message = ex.message ?: " "

        LOG.debug("Returning HTTP status: $httpStatus for path: $path, message: $message")

        return HttpErrorInfo(httpStatus, path, message)
    }


}