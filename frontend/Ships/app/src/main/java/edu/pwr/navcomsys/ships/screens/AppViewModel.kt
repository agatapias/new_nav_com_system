package edu.pwr.navcomsys.ships.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppViewModel(
    private val messageListener: MessageListener
) : ViewModel() {

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent

    init {
        viewModelScope.launch {
            messageListener.audioConnectRequestMessages.collectLatest {
                it?.fromAddress?.let { ip ->
                    _navigationEvent.emit(ip)
                }
            }
        }
    }
}