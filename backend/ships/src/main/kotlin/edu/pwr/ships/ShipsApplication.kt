package edu.pwr.ships

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShipsApplication

fun main(args: Array<String>) {
	runApplication<ShipsApplication>(*args)
}
