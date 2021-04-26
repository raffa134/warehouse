package com.aeki.warehouse.service.exception

class DuplicateItemException(override val message: String) : RuntimeException(message)