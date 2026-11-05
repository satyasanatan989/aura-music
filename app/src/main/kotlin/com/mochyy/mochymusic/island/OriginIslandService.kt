package com.mochyy.mochymusic.island

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mochyy.mochymusic.MainActivity
import com.mochyy.mochymusic.R

/**
 * OriginIslandService
 *
 * Provides real-time interactive media capsule integration for:
 * - Vivo OriginOS "Origin Island"
 * - Dynamic Island / Live Activity style notifications on modern Android & foldables
 * - Real-time audio waveform animation metadata & quick controls
 */
object OriginIslandService {

    const val CHANNEL_ID = "origin_island_media_capsule"
    const val NOTIFICATION_ID = 4040

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Origin Island Media Capsule"
            val descriptionText = "Displays real-time animated music playback on Origin Island & status bar"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildCapsuleNotification(
        context: Context,
        title: String,
        artist: String,
        artwork: Bitmap?,
        isPlaying: Boolean,
        volumeLevel: Int = 80
    ): Notification {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.nav_mochy_filled)
            .setContentTitle(title)
            .setContentText(artist)
            .setLargeIcon(artwork)
            .setContentIntent(pendingIntent)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )

        // Custom extras for OriginOS Island & HyperOS dynamic status capsule
        builder.addExtras(android.os.Bundle().apply {
            putString("vivo.origin.island.type", "media_player")
            putBoolean("vivo.origin.island.active", isPlaying)
            putInt("vivo.origin.island.volume", volumeLevel)
            putString("origin.island.wave_state", if (isPlaying) "playing" else "paused")
        })

        return builder.build()
    }
}
