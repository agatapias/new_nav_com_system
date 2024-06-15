package edu.pwr.navcomsys.ships.screens.shiplist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.dto.CallAcceptMessageDto
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShipListViewModel(
    private val peerRepository: PeerRepository,
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShipListUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent

    init {
        viewModelScope.launch(Dispatchers.Default) {
            peerRepository.getLocations().collectLatest { locations ->
                Log.d("Map", "new ship locations in vm: $locations")
                _uiState.update { it.copy(
                    ships = locations
                ) }
            }
        }
    }

    fun onDetailsClick(ship: LocationDto) {
        _uiState.update { it.copy(
            shipDialogData = ship
        ) }
    }

    fun onCloseDialog() {
        _uiState.update { it.copy(
            shipDialogData = null
        ) }
    }

    fun onCallClick(ip: String) {
        Log.d("Call", "onCallClick called, ip: $ip")
        viewModelScope.launch(Dispatchers.Default) {
            val toUsername = _uiState.value.ships.firstOrNull { it.ipAddress == ip }?.username
            val myUsername = userInfoRepository.getUser()?.username
            val message = CallAcceptMessageDto(
                fromAddress = peerRepository.getOwnIpInfo().ipAddress,
                toAddress = ip,
                fromUsername = myUsername ?: "",
                toUsername = toUsername ?: ""
            )
            val json = peerRepository.convertToJson(message, MessageType.AUDIO_CONNECT_REQUEST)
            peerRepository.sendMessage(ip, json)
            Log.d("Call", "after send message call")

            _navigationEvent.emit(ip)
        }
    }
}