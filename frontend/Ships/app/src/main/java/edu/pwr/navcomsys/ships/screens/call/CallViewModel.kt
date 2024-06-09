package edu.pwr.navcomsys.ships.screens.call

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.dto.AudioMessageDto
import edu.pwr.navcomsys.ships.data.dto.AudioMessageType
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CallViewModel(
    private val peerRepository: PeerRepository,
    private val messageListener: MessageListener,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(CallUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<Boolean?>(null)
    val navigationEvent = _navigationEvent

    private var ipAddress: String? = null
//    private val queue: Queue<ByteArray> = LinkedList()
    private val queueFlow: MutableStateFlow<ByteArray?> = MutableStateFlow(null)

    private var recorder: MediaRecorder? = null
    private var folderPath: String = context.filesDir.path + "audiodata/cache"
    private var mediaPlayer: MediaPlayer? = null


    private val fileName = "${folderPath}/Macaw-${
        SimpleDateFormat("ddMMyyyy-HHmmss", Locale.getDefault()).format(
            Calendar.getInstance().time
        )
    }.wav"

    fun init(newIpAddress: String?, type: CallStatus) {
        _uiState.update {
            it.copy(
                callStatus = type
            )
        }
        if (ipAddress == null || ipAddress != newIpAddress) {
            ipAddress = newIpAddress
        }
        listenToAudioMessages()
    }

    fun sendRecording() {
        val ip = ipAddress ?: return
        val byteArray = readFileToByteArray(fileName)
        val acceptMessage = AudioMessageDto(
            fromAddress = peerRepository.getOwnIpInfo().ipAddress,
            toAddress = ip,
            type = AudioMessageType.Message,
            message = byteArray
        )
        val json = peerRepository.convertToJson(acceptMessage, MessageType.AUDIO)
        peerRepository.sendMessage(ip, json)
    }

    fun acceptConversation() {
        // send accept signal from peer repo
        val ip = ipAddress ?: return
        val acceptMessage = AudioMessageDto(
            fromAddress = peerRepository.getOwnIpInfo().ipAddress,
            toAddress = ip,
            type = AudioMessageType.Accept,
        )
        val json = peerRepository.convertToJson(acceptMessage, MessageType.AUDIO)
        peerRepository.sendMessage(ip, json)
    }

    fun endConversation() {
        // send ending signal from peer repo
        val ip = ipAddress ?: return
        val acceptMessage = AudioMessageDto(
            fromAddress = peerRepository.getOwnIpInfo().ipAddress,
            toAddress = ip,
            type = AudioMessageType.End,
        )
        val json = peerRepository.convertToJson(acceptMessage, MessageType.AUDIO)
        peerRepository.sendMessage(ip, json)
    }

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        }

        try {
            recorder?.prepare()
            recorder?.start()
            _uiState.update {
                it.copy(isRecording = true)
            }
        } catch (e: IOException) {
            _uiState.update {
                it.copy(isRecording = false)
            }
            Log.d("Call", "record audio file failed.")
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isRecording = false)
            }
            Log.e("Call", "record audio file failed. error: ${e.message}")
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    fun onRecordPress() {
        if (_uiState.value.isRecording) {
            stopRecording()
            sendRecording()
        } else {
            startRecording()
        }
    }

    private fun listenToAudioMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageListener.audioMessages.collectLatest { data ->
                if (data == null) return@collectLatest
                // TODO: check if ip addresses match
                when (data.type) {
                    AudioMessageType.Accept -> {
                        onAccept()
                    }// change screen and play audio
                    AudioMessageType.End -> {
                        onEnd()
                    }// change screen and stop playing audio, then navigate
                    AudioMessageType.Message -> {
                        onMessage(data.message)
                    }// play incoming messages
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

    private fun onMessage(message: ByteArray?) {
        if (_uiState.value.callStatus != CallStatus.Ongoing || message == null) return
        // transform to sound and play, in queue
        // here add to queue
        // play somewhere else
        val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir("audioDir", Context.MODE_PRIVATE)
        val file = File(directory, "audio.wav")

        FileOutputStream(file).apply {
            write(message)
            flush()
            close()
        }

        val newMediaPlayer = MediaPlayer()
        val path = file.path
        newMediaPlayer.setDataSource(path)
        newMediaPlayer.prepare()
        newMediaPlayer.setOnCompletionListener {
            Log.d("Call", "listening ended")
        }
        mediaPlayer = newMediaPlayer
    }

    private fun onEnd() {
        // navigate event
        viewModelScope.launch(Dispatchers.Default) {
            _navigationEvent.emit(true)
        }
    }

    private fun readFileToByteArray(fileName: String): ByteArray? {
        val file = File(context.getExternalFilesDir(null), fileName)
        return try {
            val fileInputStream = FileInputStream(file)
            val byteArray = ByteArray(file.length().toInt())
            fileInputStream.read(byteArray)
            fileInputStream.close()
            byteArray
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Listen to conversation initiated in general area
    // Listen to accept signal here
    // If yes, then start listening to audio messages, otherwise ignore them
    // Listen to close conversation signal here
}