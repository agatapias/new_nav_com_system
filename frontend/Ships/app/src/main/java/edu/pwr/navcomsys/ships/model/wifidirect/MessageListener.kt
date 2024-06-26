package edu.pwr.navcomsys.ships.model.wifidirect

import android.util.Log
import com.google.gson.Gson
import edu.pwr.navcomsys.ships.data.database.ChatMessage
import edu.pwr.navcomsys.ships.data.dto.AudioMessageDto
import edu.pwr.navcomsys.ships.data.dto.CallAcceptMessageDto
import edu.pwr.navcomsys.ships.data.dto.ChatMessageDto
import edu.pwr.navcomsys.ships.data.dto.IPBroadcastDto
import edu.pwr.navcomsys.ships.data.dto.IPInfoDto
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.data.dto.MessageDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.ChatMessageRepository
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

private const val TAG = "MessageListener"

class MessageListener(
    private val peerRepository: PeerRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    val chatMessages: MutableStateFlow<ChatMessageDto?> = MutableStateFlow(null)
    val audioMessages: MutableStateFlow<AudioMessageDto?> = MutableStateFlow(null)
    val audioConnectRequestMessages: MutableStateFlow<CallAcceptMessageDto?> = MutableStateFlow(null)

    fun startListening() {
        Log.d(TAG, "started listening for messages")
        scope.launch {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "started listening for messages")
                val serverSocket = ServerSocket(8888)
                try {
                    while (isActive) {
                        val clientSocket = serverSocket.accept()
                        handleClient(clientSocket)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error accepting connection: ${e.localizedMessage}")
                } finally {
                    serverSocket.close()
                }
            }
        }
    }

    private fun handleClient(clientSocket: Socket) {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    clientSocket.getInputStream().bufferedReader().use { reader ->
                        val json = reader.readLine()
                        Log.d(TAG, "Read JSON: $json")
                        val message = gson.fromJson(json, MessageDto::class.java)
                        Log.d(TAG, "Read message type: $message.messageType")
                        when (message.messageType) {
                            MessageType.IP_INFO -> handleIPInfoMessage(message.encodedJson)
                            MessageType.IP_BROADCAST -> handleIPBroadcastMessage(message.encodedJson)
                            MessageType.DEVICE_INFO -> handleLocationMessage(message.encodedJson)
                            MessageType.AUDIO -> handleAudioMessage(message.encodedJson)
                            MessageType.AUDIO_CONNECT_REQUEST -> handleAudioConnectRequestMessage(message.encodedJson)
                            MessageType.CHAT -> handleChatMessage(message.encodedJson)
                            else -> doNothing()
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.d(TAG, "Error: $it") }
                } finally {
                    clientSocket.close()
                }
            }
        }
    }

    private fun handleIPInfoMessage(msg: String) {
        val ipInfo = gson.fromJson(msg, IPInfoDto::class.java)
        if (peerRepository.connectedDevices.none { it.deviceName == ipInfo.deviceName && it.ipAddress == ipInfo.ipAddress }) {
            var newDevicesList = peerRepository.connectedDevices + ipInfo
            if (peerRepository.connectedDevices.isEmpty()) {
                val ownerIpInfo = peerRepository.getOwnIpInfo()
                newDevicesList = newDevicesList + ownerIpInfo
            }
            peerRepository.updateDevices(newDevicesList)
            peerRepository.broadcastAddresses()
        }
    }

    private fun handleIPBroadcastMessage(msg: String) {
        Log.d(TAG, "IP broadcasted = $msg")
        val broadcast = gson.fromJson(msg, IPBroadcastDto::class.java)
        peerRepository.updateDevices(broadcast.devices)
    }

    private fun handleLocationMessage(msg: String) {
        val broadcast = gson.fromJson(msg, LocationDto::class.java)
        peerRepository.updateLocation(broadcast)
    }

    private suspend fun handleChatMessage(msg: String) {
        val chatMessage = gson.fromJson(msg, ChatMessageDto::class.java)
        val chatMessageEntity = ChatMessage(
            0,
            chatMessage.fromUsername,
            chatMessage.toUsername,
            chatMessage.message,
            chatMessage.createdDate,
            chatMessage.createdTime
        )
        chatMessageRepository.insertMessage(chatMessageEntity)
        chatMessages.emit(chatMessage)
    }

    private suspend fun handleAudioMessage(msg: String) {
        val message = gson.fromJson(msg, AudioMessageDto::class.java)
        audioMessages.emit(message)
    }

    private suspend fun handleAudioConnectRequestMessage(msg: String) {
        val message = gson.fromJson(msg, CallAcceptMessageDto::class.java)
        audioConnectRequestMessages.emit(message)
    }

    private fun doNothing() {}
}