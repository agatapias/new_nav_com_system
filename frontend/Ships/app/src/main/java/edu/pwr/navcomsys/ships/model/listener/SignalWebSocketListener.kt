package edu.pwr.navcomsys.ships.model.listener

import edu.pwr.navcomsys.ships.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SignalWebSocketListener(
    client: OkHttpClient
) {
    private val request = Request.Builder()
        .url("ws://${BuildConfig.APP_NETWORK}/projectDeleted")
        .build()

    private val webSocket = client.newWebSocket(request, object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            // Handle message based on its type
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // WebSocket connection is closed

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // Handle connection failure
        }
    })

    fun closeWebSocket() {
        webSocket.close(1000, null)
    }
}