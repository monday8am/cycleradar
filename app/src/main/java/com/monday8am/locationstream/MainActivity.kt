package com.monday8am.locationstream

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.monday8am.locationstream.location.LocationUpdatesService
import com.monday8am.locationstream.ui.PhotoListAdapter
import com.monday8am.locationstream.ui.PhotoListViewModel
import com.monday8am.locationstream.ui.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.widget.Toast



class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"

    private val requestPermissionsRequestCode = 34
    private var mService: LocationUpdatesService? = null
    private var mBound = false

    private var startMenuItem: MenuItem? = null
    private var stopMenuItem: MenuItem? = null

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: PhotoListViewModel
    // Use it for testing!
    //private lateinit var listAdapter: PhotoListTestingAdapter

    private lateinit var listAdapter: PhotoListAdapter

    private val disposables: CompositeDisposable = CompositeDisposable()

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
        photoRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoListViewModel::class.java)
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

        disposables.add(viewModel.isRequestingLocation()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { value ->
                startMenuItem?.isVisible = !value
                stopMenuItem?.isVisible = value
            })

        disposables.add(viewModel.getPhotos()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { value ->
                listAdapter = PhotoListAdapter(value)
                // Use it for testing!
                // listAdapter = PhotoListTestingAdapter(value)
                photoRecyclerView.adapter = listAdapter
            })
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
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

        val isActivated = viewModel.isRequestingLocation().value ?: false
        startMenuItem?.isVisible = !isActivated
        stopMenuItem?.isVisible = isActivated

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

    private fun startRequestingLocation() {
        val success = mService?.requestLocationUpdates() ?: false
        viewModel.startStopRequestingLocation(success)
    }

    private fun stopRequestingLocation() {
        val success = mService?.removeLocationUpdates() ?: false
        viewModel.startStopRequestingLocation(!success)
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
