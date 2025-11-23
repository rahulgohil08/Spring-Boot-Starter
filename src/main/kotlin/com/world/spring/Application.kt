package com.world.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
