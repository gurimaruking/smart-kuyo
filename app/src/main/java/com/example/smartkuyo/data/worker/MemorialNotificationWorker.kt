package com.example.smartkuyo.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.smartkuyo.R
import com.example.smartkuyo.data.local.db.dao.DeceasedDao
import com.example.smartkuyo.data.mapper.toDomain
import com.example.smartkuyo.domain.model.MemorialDateCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class MemorialNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deceasedDao: DeceasedDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val allDeceased = deceasedDao.getAllDeceased().first()

        allDeceased.forEach { entity ->
            val deceased = entity.toDomain()
            val nextMemorial = MemorialDateCalculator.getNextMemorial(deceased.dateOfDeath)

            if (nextMemorial != null && nextMemorial.daysFromNow in 0..3) {
                val title = when {
                    nextMemorial.isToday -> "${deceased.fullName}様の${nextMemorial.name}です"
                    nextMemorial.daysFromNow == 1L -> "明日は${deceased.fullName}様の${nextMemorial.name}です"
                    else -> "${nextMemorial.daysFromNow}日後に${deceased.fullName}様の${nextMemorial.name}があります"
                }

                sendNotification(
                    id = deceased.id.toInt(),
                    title = title,
                    message = "${nextMemorial.date}（${nextMemorial.name}）"
                )
            }
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "法要通知",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "法要・月命日の通知"
        }
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotification(id: Int, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(id, notification)
    }

    companion object {
        private const val CHANNEL_ID = "memorial_notifications"
        private const val WORK_NAME = "memorial_notification_check"

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<MemorialNotificationWorker>(
                1, TimeUnit.DAYS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
