package com.nfx.speexandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtils
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()
        val echo_byte_array = IOUtils.toByteArray(this.resources.openRawResource(R.raw.echo))
        var echo_short_array = ShortArray(echo_byte_array.size / 2)
        ByteBuffer.wrap(echo_byte_array).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(echo_short_array)

        val mixed_array = IOUtils.toByteArray(this.resources.openRawResource(R.raw.mixed))

        var mixed_short_array = ShortArray(mixed_array.size / 2)
        ByteBuffer.wrap(mixed_array).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(mixed_short_array)

        val NN = 1024
        val TAIL = 4096
        open(44100, NN, TAIL)

        val frameNum = mixed_short_array.size / NN
        var result = mutableListOf<Short>()
        for (frame in 0 until frameNum) {
            val input_frame = mixed_short_array.copyOfRange(frame * NN, (frame + 1) * NN)
            val echo_frame = echo_short_array.copyOfRange(frame * NN, (frame + 1) * NN)
            val list = process(input_frame, echo_frame).toList()
            result.addAll(list)
        }

        writeAudioDataToFile(result.toShortArray())
    }


    //convert short to byte
    private fun short2byte(sData: ShortArray): ByteArray {
        val shortArrsize = sData.size
        val bytes = ByteArray(shortArrsize * 2)
        for (i in 0 until shortArrsize) {
            bytes[i * 2] = (sData[i] and 0x00FF).toByte()
            bytes[i * 2 + 1] = (sData[i].toInt() shr 8).toByte()
            sData[i] = 0
        }
        return bytes

    }

    private fun writeAudioDataToFile(sData: ShortArray) {
        // Write the output audio in byte

        val filePath = File(this.filesDir, "voice.pcm").path

        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(filePath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }


        try {
            // // writes the data to file from buffer
            // // stores the voice buffer
            val bData = short2byte(sData)
            os!!.write(bData, 0, sData.size * 2)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        try {
            os!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun open(sampleRate: Int, bufSize: Int, totalSize: Int)

    external fun process(input_frame: ShortArray, echo_frame: ShortArray): ShortArray

    external fun capture(input_frame: ShortArray): ShortArray

    external fun playback(echo_frame: ShortArray): ShortArray

    external fun close()

    @Throws(IOException::class)
    fun convertStreamToByteArray(inputStream: InputStream): ByteArray {
        val baos = ByteArrayOutputStream()
        val buff = ByteArray(10240)
        var i = Integer.MAX_VALUE
        i = inputStream.read(buff, 0, buff.size)
        while (i > 0) {
            baos.write(buff, 0, i)
            i = inputStream.read(buff, 0, buff.size)
        }

        return baos.toByteArray() // be sure to close InputStream in calling function
    }

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
