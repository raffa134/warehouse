package com.aeki.warehouse

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WarehouseApplicationTests {

	companion object {
		private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
	}

	@Test
	fun contextLoads() {
		logger.info("Loading test context...")
	}

}
