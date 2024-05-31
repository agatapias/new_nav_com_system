package edu.pwr.navcomsys.ships.model.wifidirect

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.File
import java.net.ServerSocket

private const val TAG = "WifiDirect"

class MessageServerAsyncTask(
    private val context: Context
) : AsyncTask<Void, Void, String?>() {

    override fun doInBackground(vararg params: Void): String? {
        Log.d(TAG, "doInBackground called")
        /**
         * Create a server socket.
         */
        val serverSocket = ServerSocket(8888)
        return serverSocket.use {
            Log.d(TAG, "serverSocket.use  called")
            /**
             * Wait for client connections. This call blocks until a
             * connection is accepted from a client.
             */
            val client = serverSocket.accept()

            Log.d(TAG, "client accepted, ${client.inetAddress.hostAddress}")
            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */

            val inputstream = client.getInputStream()
            val bytearr = ByteArray(4096)
            inputstream.read(bytearr)
            Log.d(TAG, "async task")
            Log.d(TAG, bytearr.decodeToString())
            serverSocket.close()
            bytearr.decodeToString()
        }
    }

    private fun File.doesNotExist(): Boolean = !exists()

    /**
     * Start activity that can handle the JPEG image
     */
    override fun onPostExecute(result: String?) {
        result?.run {
            Log.d(TAG, "onPostExecute called")
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
    }
}