package edu.pwr.navcomsys.ships.screens.dashboard

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun DashboardScreen(
    navigation: DashboardNavigation
) {
    DashboardScreenContent(navigation)
}

@Composable
private fun DashboardScreenContent(
    navigation: DashboardNavigation
) {
    val isRecording = remember { mutableStateOf(false) }
    val recorder: MutableState<MediaRecorder?> = remember { mutableStateOf(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.space40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_globe),
                contentDescription = null,
            )
        }
        Dimensions.space20.HeightSpacer()
        ShipButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Mapa statków",
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            type = ShipButtonType.Primary
        ) {
            navigation.openMap()
        }
        Dimensions.space20.HeightSpacer()
        ShipButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Statki w pobliżu",
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            type = ShipButtonType.Secondary
        ) {
            navigation.openShipList()
        }
//        Dimensions.space20.HeightSpacer()
//        val context = LocalContext.current
//        val text = if (isRecording.value) "Stop" else "Nagrywaj"
//        ShipButton(
//            modifier = Modifier.fillMaxWidth(),
//            text = text,
//            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
//            onClick = { onRecordPress(context, recorder, isRecording) }
//        )
    }
}

fun startRecording(
    context: Context,
    recorder: MutableState<MediaRecorder?>,
    isRecording: MutableState<Boolean>,
) {
//    val folderPath: String = context.filesDir.path + "audiodata/cache"
//    val fileName = "${folderPath}/Macaw-${
//        SimpleDateFormat("ddMMyyyy-HHmmss", Locale.getDefault()).format(
//            Calendar.getInstance().time
//        )
//    }.wav"
    val time = SimpleDateFormat("ddMMyyyy-HHmmss", Locale.getDefault()).format(
        Calendar.getInstance().time
    )

    val path = context.filesDir.path + "/cache/audio"
    Log.d("yes", "path: $path")
    val dir = File(path)
    if (!dir.exists()) dir.mkdirs()
    val fileName = "audio-$time.wav"
    val fullFileName = path + fileName

    val file: File = File(dir, fileName)
    file.createNewFile()

    recorder.value = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        setOutputFile(fullFileName)
    }

    try {
        recorder.value?.prepare()
        recorder.value?.start()
    } catch (e: IOException) {
        isRecording.value = false
        Log.d("Call", "record audio file failed. io error: ${e.message}")
    } catch (e: Exception) {
        isRecording.value = false
        Log.e("Call", "record audio file failed. error: ${e.message}")
    }
}

private fun stopRecording(
    recorder: MutableState<MediaRecorder?>,
    isRecording: MutableState<Boolean>,
) {
    recorder.value?.apply {
        stop()
        reset()
        release()
    }
    recorder.value = null
    isRecording.value = false
}

fun onRecordPress(
    context: Context,
    recorder: MutableState<MediaRecorder?>,
    isRecording: MutableState<Boolean>
) {
    Log.d("Call", "onRecordPress called")
    val newState = !isRecording.value
    Log.d("Call", "newState: $newState")
    isRecording.value = newState
    if (!newState) {
        stopRecording(recorder, isRecording)
    } else {
        startRecording(context, recorder, isRecording)
    }
}