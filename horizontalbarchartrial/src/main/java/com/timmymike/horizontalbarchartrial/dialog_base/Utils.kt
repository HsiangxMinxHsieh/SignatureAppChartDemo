package com.timmymike.radarcharttrial.dialog_base

import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import com.timmymike.horizontalbarchartrial.BuildConfig
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


fun UUID.short():String {return this.toString().substring(4, 8)}
fun String.getUUID(): UUID {return UUID.fromString("0000$this-0000-1000-8000-00805f9b34fb")}
fun String.toParcelUUID(): ParcelUuid {return ParcelUuid.fromString("0000$this-0000-1000-8000-00805f9b34fb")}

fun Date.todaylbl(): Int {
    val sdf: Format = SimpleDateFormat(DATE_FORMAT)
    return sdf.format(this).toInt()
}

fun Date.tomorrowlbl(): Int {
    val calendar = GregorianCalendar()
    calendar.time = this
    calendar.add(Calendar.DATE, 1)
    val sdf: Format = SimpleDateFormat(DATE_FORMAT)
    return sdf.format(calendar.time).toInt()
}

fun Date.thisHrlbl(): Int {
    val sdf: Format = SimpleDateFormat("hh")
    return sdf.format(this).toInt()
}

fun Date.thisMinOfThisHrlbl(): Int {
    val sdf: Format = SimpleDateFormat("mm")
    return sdf.format(this).toInt()
}



fun ByteArray.hex4EasyRead():String{
    val sb = StringBuilder()
    for (b in this) sb.append(String.format("%02X ", b))
    return sb.toString()
}

fun ByteArray.toHexString():String{
    val sb = StringBuilder()
    for (b in this) sb.append(String.format("%02X:", b))
    return sb.toString()
}

fun logi(tag: String, log:Any) {

    if (BuildConfig.DEBUG_MODE) Log.i(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        }else{
            "TIME"
        }
//        appendLog("$current : $tag : $log ")
    }
}


fun loge(tag: String, log:Any) {

    if (BuildConfig.DEBUG_MODE) Log.e(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        }else{
            "TIME"
        }
//        appendLog("$current : $tag : $log ")
    }
}

//fun appendLog(text: String) {
////    val directory = App.instance.ctx().externalCacheDir
//    val logFile = File(directory, "loge.file")
////    Log.e("AAAA", " path is $logFile")
//    if (!logFile.exists()) {
//        try {
//            logFile.createNewFile()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//    }
//    try {
//        //BufferedWriter for performance, true to set append to file flag
//        val buf = BufferedWriter(FileWriter(logFile, true))
//        buf.append(text)
//        buf.newLine()
//        buf.close()
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//
//}

private fun delay(h: Handler, sec:Float, lambda: () -> Unit){
    h.postDelayed({lambda()}, (sec * 1000).toLong())
}

fun parseScanRecord(scanRecord: ByteArray): Map<Int,ByteArray> {
    val dict = mutableMapOf<Int, ByteArray>()
    val rawData: ByteArray?
    var index = 0
    while (index < scanRecord.size) {
        val length = scanRecord[index++].toInt()
        //if no record
        if (length == 0) break
        //type
        val type = scanRecord[index].toInt()
        //if not valid type
//        print("UTILS", "[MANUFACTURE] type is $type")
//        print("UTILS", "[MANUFACTURE] length is $length")
        if (type == 0) break
        dict[type] = Arrays.copyOfRange(scanRecord, index + 1, index + length)
        //next
        index += length
    }

    return dict
}

