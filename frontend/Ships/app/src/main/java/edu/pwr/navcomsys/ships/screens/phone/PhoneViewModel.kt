package edu.pwr.navcomsys.ships.screens.phone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.dto.CallAcceptMessageDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhoneViewModel(
    private val peerRepository: PeerRepository,
    private val messageListener: MessageListener
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent

//    init {
//        viewModelScope.launch {
//            messageListener.
//        }
//    }

    fun onCallClick(ip: String) {
        val message = CallAcceptMessageDto(
            fromAddress = peerRepository.getOwnIpInfo().ipAddress,
            toAddress = ip,
        )
        val json = peerRepository.convertToJson(message, MessageType.AUDIO_CONNECT_REQUEST)
        peerRepository.sendMessage(ip, json)

        viewModelScope.launch(Dispatchers.Default) {
            _navigationEvent.emit(ip)
        }
    }

    fun onSelectTab(tab: PhoneTab) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }
}