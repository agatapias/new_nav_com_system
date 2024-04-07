package edu.pwr.ships.controller

import edu.pwr.ships.dto.SignalDto
import edu.pwr.ships.service.DiscoveryService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DiscoveryController(
    private val discoveryService: DiscoveryService
) {

    @PutMapping("/send")
    fun sendSignal(@RequestBody signalDto: SignalDto) {
        discoveryService.sendSignal(signalDto)
    }
}