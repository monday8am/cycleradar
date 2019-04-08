package com.monday8am.locationstream.location

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.monday8am.locationstream.Injection
import com.monday8am.locationstream.MainActivity
import com.monday8am.locationstream.R
import com.monday8am.locationstream.data.PhotoDataRepository
import com.monday8am.locationstream.data.UserLocation
import java.text.DateFormat
import java.util.*


const val packageNameString = "com.monday8am.locationupdatesforegroundservice"
const val extraStartedFromNotification = "$packageNameString.started_from_notification"

class LocationUpdatesService : Service() {
    private val tag = LocationUpdatesService::class.java.simpleName
    private val channelId = "channel_01"
    private val mBinder = LocalBinder()

    private val updateIntervalInMs: Long = 10000
    private val fastestUpdateIntervalInMs = updateIntervalInMs/2
    private val notificationId = 12345678
    private var mChangingConfiguration = false

    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mServiceHandler: Handler

    private lateinit var repo: PhotoDataRepository

    override fun onCreate() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }

        val handlerThread = HandlerThread(tag)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)
        }

        repo = Injection.providePhotoDataRepository(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(tag, "Service started")
        val startedFromNotification = intent?.getBooleanExtra(extraStartedFromNotification,false) ?: false
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        return Service.START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(tag, "in onBind()")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        Log.i(tag, "in onRebind()")
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i(tag, "Last client unbound from service")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && repo.isRequestingLocation()) {
            Log.i(tag, "Starting foreground service")
            startForeground(notificationId, getNotification())
        }
        return true
    }

    override fun onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null)
    }

    //  Removes location updates

    fun requestLocationUpdates(): Boolean {
        Log.i(tag, "Requesting location updates")
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        return try {
            mFusedLocationClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, Looper.myLooper())
            true
        } catch (unlikely: SecurityException) {
            Log.e(tag, "Lost location permission. Could not request updates. $unlikely")
            false
        }
    }

    //  Removes location updates

    fun removeLocationUpdates(): Boolean {
        Log.i(tag, "Removing location updates")
        return try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            stopSelf()
            true
        } catch (unlikely: SecurityException) {
            Log.e(tag, "Lost location permission. Could not remove updates. $unlikely")
            false
        }
    }

    private fun getNotification(): Notification {
        val intent = Intent(this, LocationUpdatesService::class.java)
        intent.putExtra(extraStartedFromNotification, true)

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val activityPendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java), 0
        )

        val builder = NotificationCompat.Builder(this)
            .addAction(R.drawable.ic_launcher_foreground, getString(R.string.launch_activity), activityPendingIntent)
            .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates), servicePendingIntent)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.reading_location_text))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId)
        }

        return builder.build()
    }

    private fun onNewLocation(location: Location) {
        repo.addPhotoFromLocation(UserLocation(longitude = location.longitude, latitude = location.latitude))
            .subscribe()
    }

    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = updateIntervalInMs
        mLocationRequest.fastestInterval = fastestUpdateIntervalInMs
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        return mLocationRequest
    }

    private fun getLocationText(location: UserLocation?): String {
        return if (location == null)
            "Unknown location"
        else
            "(" + location.latitude + ", " + location.longitude + ")"
    }

    private fun getLocationTitle(context: Context): String {
        return context.getString(
            R.string.location_updated,
            DateFormat.getDateTimeInstance().format(Date())
        )
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

}
