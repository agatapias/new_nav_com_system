package edu.pwr.navcomsys.ships.screens.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.dto.AudioMessageType
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallViewModel(
    private val peerRepository: PeerRepository,
    private val messageListener: MessageListener
) : ViewModel() {
    private val _uiState = MutableStateFlow(CallUiState())
    val uiState = _uiState.asStateFlow()

    private var ipAddress: String? = null

    fun init(newIpAddress: String) {
        if (ipAddress == null || ipAddress != newIpAddress) {
            ipAddress = newIpAddress
        }
    }

    fun onDeclineCall() {

    }

//    fun sendRecording() {
//        val locationDto = LocationDto(
//            username = user.username,
//            shipName = user.shipName,
//            description = user.description,
//            ipAddress = ip,
//            xCoordinate = it.latitude,
//            yCoordinate = it.longitude
//        )
//        val json = convertToJson(locationDto, MessageType.AUDIO)
//        peerRepository.sendMessage(ipAddress, )
//    }
//
//    fun acceptConversation() {
//        // send accept signal from peer repo
//        val acceptMessage = AudioMessageDto(
//
//        )
//    }

    fun endConversation() {
        // send ending signal from peer repo
    }

    private fun listenToAudioMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageListener.audioMessages.collectLatest { data ->
                if (data == null) return@collectLatest
                when (data.type) {
                    AudioMessageType.Accept -> {}// change screen and play audio
                    AudioMessageType.End -> {}// change screen and stop playing audio, then navigate
                    AudioMessageType.Message -> {}// play incoming messages
                }
            }
        }
    }

    private fun onAccept() {
        _uiState.update {
            it.copy(
                callStatus = CallStatus.Ongoing
            )
        }
    }

    private fun onEnd() {
        // navigate event
    }

    // Listen to conversation initiated in general area
    // Listen to accept signal here
    // If yes, then start listening to audio messages, otherwise ignore them
    // Listen to close conversation signal here
}