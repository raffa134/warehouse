package com.aeki.warehouse.service.exception

class InsufficientStockException(override val message: String) : RuntimeException(message)