package com.aeki.warehouse.controller

import com.aeki.warehouse.model.ErrorDTO
import com.aeki.warehouse.service.exception.DuplicateItemException
import com.aeki.warehouse.service.exception.InsufficientStockException
import com.aeki.warehouse.service.exception.ItemNotFoundException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        const val GENERIC_ERROR_MSG =
                "Failed to process the request due to internal errors"
        const val GENERIC_INPUT_ERROR_MSG =
                "Failed to process the request due to errors in the payload"
    }

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleProductNotFound(
            ex: ItemNotFoundException
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, ex.message)
        logger.error("Error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DuplicateItemException::class)
    fun handleDuplicateArticle(
            ex: DuplicateItemException
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, ex.message)
        logger.error("Error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(
            ex: InsufficientStockException
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, ex.message)
        logger.error("Error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
            MissingKotlinParameterException::class,
            UnrecognizedPropertyException::class,
            HttpMessageConversionException::class,
            DataIntegrityViolationException::class
    )
    fun handleMissingParameter(
            ex: Exception
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, GENERIC_INPUT_ERROR_MSG)
        logger.error("Error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    override fun handleMethodArgumentNotValid(
            ex: MethodArgumentNotValidException,
            headers: HttpHeaders,
            status: HttpStatus,
            request: WebRequest
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, GENERIC_INPUT_ERROR_MSG)
        logger.error("$error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpMessageNotReadable(
            ex: HttpMessageNotReadableException,
            headers: HttpHeaders,
            status: HttpStatus,
            request: WebRequest
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, GENERIC_INPUT_ERROR_MSG)
        logger.error("$error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleOtherExceptions(
            ex: Exception
    ): ResponseEntity<Any> {
        val error = ErrorDTO(ErrorDTO.Severity.error, GENERIC_ERROR_MSG)
        logger.error("$error with the following exception: $ex", ex)
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

}