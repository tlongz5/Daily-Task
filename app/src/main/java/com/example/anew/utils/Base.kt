package com.example.anew.utils

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.graphics.scale
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

//support func
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this, now, DateUtils.MINUTE_IN_MILLIS).toString()
}

fun Long.toTimeUI():String{
    return SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH).format(this)
}

fun Long.toCalendarDay(): CalendarDay{
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return CalendarDay.from(calendar)
}

//tranfer Long to String Date
fun Long.toTime(): String{
    val date = Date(this)
    val formatTime= SimpleDateFormat("HH:mm", Locale.getDefault())
    return when{
        DateUtils.isToday(this) -> formatTime.format(date)
        DateUtils.isToday(this + DateUtils.DAY_IN_MILLIS) -> "Yesterday, ${formatTime.format(date)}"
        isThisYear(this) -> SimpleDateFormat("dd/MM",Locale.getDefault()).format(date)
        else -> {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    }
}

//tranfer Long to Day and Month
fun Long.toDayAndMonth(): String{
    val date = Date(this)
    val tranformDate = SimpleDateFormat("dd/MM", Locale.getDefault())
    return tranformDate.format(date)
}

fun Long.toHourAndMinute(): String{
    val date = Date(this)
    val tranformDate = SimpleDateFormat("HH:mm aa", Locale.getDefault())
    return tranformDate.format(date)
}

//format to hour and minute type AM, PM
fun tranferToHourAndMinute(hour: Int, minute: Int): String{
    val bonus = if(minute<10) "0" else ""
    val bonusEnd = if(hour<12) " AM" else " PM"
    val time = if(hour>12) hour-12 else hour
    return "$time:$bonus$minute$bonusEnd"
}


fun mergeDateAndTime(date: Long, hour: Int, minute: Int): Long? {
    val dateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    dateCalendar.timeInMillis = date
    val resCalendar = Calendar.getInstance()
    resCalendar.set(
        dateCalendar.get(Calendar.YEAR),
        dateCalendar.get(Calendar.MONTH),
        dateCalendar.get(Calendar.DAY_OF_MONTH),
        hour,
        minute,
        0
    )
    return resCalendar.timeInMillis
}

fun Long.toFullTime(): String{
    val date = Date(this)
    return SimpleDateFormat("HH:mm a dd/MM/yyyy", Locale.US).format(date)
}

fun String.toLongDate(): Long {
    val date = SimpleDateFormat("hh:mm a dd/MM/yyyy", Locale.US).parse(this)
    return date.time
}

fun isThisYear(time: Long): Boolean{
    val now= Calendar.getInstance()
    val date = Calendar.getInstance().apply {
        timeInMillis = time
    }
    return now.get(Calendar.YEAR) == date.get(Calendar.YEAR)
}

fun getCurrentTime():String{
    val date = Date()
    return SimpleDateFormat("HH:mm a", Locale.US).format(date)
}

fun getCurrentDate():String{
    val date = Date()
    return SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
}


fun downloadImgToLocal(context: Context, imgUrl: String){
    return try{
        val request = DownloadManager.Request(Uri.parse(imgUrl))
            .setTitle("Loading Image")
            .setDescription("Downloading Image")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"${System.currentTimeMillis()}.png")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(context,"Downloading Image ...", Toast.LENGTH_SHORT).show()

    }catch (e:Exception){
        Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
    }
}

//NOTEEEEEEEEEE
suspend fun swapBitmapToUrl(context: Context,drawable: Any): String{
    var bitmap = (drawable as BitmapDrawable).bitmap
    bitmap = bitmap.scale(150, 150, false)

    //bitmap convert to Uri
    val file = File(context.cacheDir,"temp_image.png")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    return file.absolutePath
}
suspend fun convertUriToCloudinaryUrl( imgUri: String): String = suspendCancellableCoroutine { continuation ->
    MediaManager.get().upload(imgUri)
        .unsigned("tl_default")
        .callback(object : UploadCallback{
            override fun onStart(requestId: String?) {

            }

            override fun onProgress(
                requestId: String?,
                bytes: Long,
                totalBytes: Long
            ) {

            }

            override fun onSuccess(
                requestId: String,
                resultData: Map<*, *>?
            ) {
                // Get the URL from the result data
                val url = resultData!!["secure_url"] as String
                if(continuation.isActive) continuation.resume(url)
            }

            override fun onError(
                requestId: String?,
                error: ErrorInfo?
            ) {
            }

            override fun onReschedule(
                requestId: String?,
                error: ErrorInfo?
            ) {
            }
        }).dispatch()
}

suspend fun convertUriToCloudinaryUrl( imgUri: Uri): String = suspendCancellableCoroutine { continuation ->
    MediaManager.get().upload(imgUri)
        .unsigned("tl_default")
        .callback(object : UploadCallback{
            override fun onStart(requestId: String?) {

            }

            override fun onProgress(
                requestId: String?,
                bytes: Long,
                totalBytes: Long
            ) {

            }

            override fun onSuccess(
                requestId: String,
                resultData: Map<*, *>?
            ) {
                // Get the URL from the result data
                val url = resultData!!["secure_url"] as String
                if(continuation.isActive) continuation.resume(url)
            }

            override fun onError(
                requestId: String?,
                error: ErrorInfo?
            ) {
            }

            override fun onReschedule(
                requestId: String?,
                error: ErrorInfo?
            ) {
            }
        }).dispatch()
}