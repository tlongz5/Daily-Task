package com.example.anew.support

import android.net.Uri
import android.text.format.DateUtils
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this, now, DateUtils.MINUTE_IN_MILLIS).toString()
}

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

fun isThisYear(time: Long): Boolean{
    val now= Calendar.getInstance()
    val date = Calendar.getInstance().apply {
        timeInMillis = time
    }
    return now.get(Calendar.YEAR) == date.get(Calendar.YEAR)
}

suspend fun convertUriToCloudinaryUrl(imgUri: Uri): String = suspendCancellableCoroutine { continuation ->
    MediaManager.get().upload(imgUri)
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
                continuation.resume(url)
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
        })
}
