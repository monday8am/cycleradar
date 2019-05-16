package com.monday8am.cycleradar

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.monday8am.cycleradar.data.UserLocation
import com.monday8am.cycleradar.location.LocationUpdatesService
import com.monday8am.cycleradar.redux.AppState
import com.monday8am.cycleradar.redux.SetInitialContent
import kotlinx.android.synthetic.main.activity_main.*
import org.rekotlin.StoreSubscriber


class MainActivity : AppCompatActivity(), StoreSubscriber<AppState>, OnMapReadyCallback {

    private val tag = "MainActivity"

    private val requestPermissionsRequestCode = 34
    private var mService: LocationUpdatesService? = null
    private var mBound = false
    private var mMap: GoogleMap? = null

    private var startMenuItem: MenuItem? = null
    private var stopMenuItem: MenuItem? = null

    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Set saved content!
        val isUpdatingLocation = CycleRadarApp.repository?.isRequestingLocation() ?: false
        val lastLocation = CycleRadarApp.repository?.getLastLocationSaved()
        store.dispatch(SetInitialContent(isUpdating = isUpdatingLocation, lastLocation = lastLocation))

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(Intent(this, LocationUpdatesService::class.java),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        store.subscribe(this)
    }

    override fun onPause() {
        store.unsubscribe(this)
        super.onPause()
    }

    override fun newState(state: AppState) {
        runOnUiThread {
            startMenuItem?.isVisible = !state.isGettingLocation
            stopMenuItem?.isVisible = state.isGettingLocation
            updateMapCenter(store.state.meCycling?.location)
        }
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        startMenuItem = menu.findItem(R.id.action_start)
        stopMenuItem = menu.findItem(R.id.action_stop)

        startMenuItem?.isVisible = !store.state.isGettingLocation
        stopMenuItem?.isVisible = store.state.isGettingLocation

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start ->  {
                if (!checkPermissions()) {
                    requestPermissions()
                } else {
                    startRequestingLocation()
                }
                return true
            }
            R.id.action_stop -> {
                stopRequestingLocation()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        updateMapCenter(store.state.meCycling?.location)
        val madridLocation = LatLng(40.416775, -3.703790)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(madridLocation, 10.0f))
    }

    private fun startRequestingLocation() {
         mService?.requestLocationUpdates()
    }

    private fun stopRequestingLocation() {
        mService?.removeLocationUpdates()
    }

    private fun updateMapCenter(lastLocation: UserLocation?) {
        if (lastLocation != null) {
            val myLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
            mMap?.addMarker(MarkerOptions().position(myLocation).title("Anton cycling"))
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(tag, "Displaying permission rationale to provide additional context.")
            Snackbar.make(findViewById(R.id.activity_main), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(this@MainActivity,
                                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                                        requestPermissionsRequestCode) }.show()
        } else {
            Log.i(tag, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestPermissionsRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionsRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRequestingLocation()
            } else {
                Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
