package edu.pwr.navcomsys.ships.screens.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.database.User
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userRepository: UserInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getUser()
            Log.d("user", user.username)
            _uiState.update {
                it.copy(user = user)
            }
        }
    }

    fun onEditDataClick() {
        _uiState.update {
            it.copy(isEditingMode = true)
        }
    }

    fun onCancelEditClick() {
        _uiState.update {
            it.copy(isEditingMode = false)
        }
    }

    fun onSaveClick(
        username: String,
        shipName: String,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val user = User(
                username = username,
                shipName = shipName,
                description = description
            )
            kotlin.runCatching {
                userRepository.saveUser(user)
            }.onSuccess {
                userRepository.getUser()
                _uiState.update {
                    it.copy(
                        isEditingMode = false,
                        user = user
                    )
                }
            }.onFailure {
                Log.e("Account", "Error", it)
            }
        }
    }
}