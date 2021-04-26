package com.aeki.warehouse.service.exception

class ItemNotFoundException(override val message: String) : RuntimeException(message)