package edu.pwr.ships.service

import edu.pwr.ships.dto.SignalDto
import edu.pwr.ships.dto.SignalType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

const val OFFER_PATTERN = "/topic/offer/"
const val ICE_PATTERN = "/topic/ice/"
const val ANSWER_PATTERN = "/topic/ice/"

@Service
class DiscoveryService(
    private val messagingTemplate: SimpMessagingTemplate,
    private val userInfoService: UserInfoService
)
{
    fun sendSignal(dto: SignalDto) {
        if (dto.type == SignalType.OFFER) {
            userInfoService.findUsersWithinRadius(dto.username, 1.0)
                .forEach {
                    messagingTemplate.convertAndSend(OFFER_PATTERN + it.username, dto.data)
                }
        }
        else if (dto.type == SignalType.ICE) {
            messagingTemplate.convertAndSend(ICE_PATTERN + dto.toUsername, dto.data)
        }
        else if (dto.type == SignalType.ANSWER) {
            messagingTemplate.convertAndSend(ANSWER_PATTERN + dto.toUsername, dto.data)
        }
    }
}