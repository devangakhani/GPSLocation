package com.gpslocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

/**
 * Main activity of the application to grab the gps location through Google Play Service
 */
class MainActivity : AppCompatActivity() {
    // define class level objects
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL:Long = 10 * 1000;
    private val FASTEST_INTERVAL:Long = 2000;
    /**
     * on create lifecycle methos
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * on start lifecycle method
     */
    override fun onStart() {
        startLocationUpdates()
    }

    /**
     * method to initialize all necessary objects and listeners
     */
    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setFastestInterval(FASTEST_INTERVAL)
        }

        // initialize locationo setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listner
        registerLocationListner()


    }

    /**
     * to register location listener
     */
    private fun registerLocationListner() {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }


    }

    /**
     * @param Location, updated location object to show in toast
     */
    private fun onLocationChanged(location: Location) {
        // create message for toast with updated latitude and longitude
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        // show toast message with updated location
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()

    }

    /**
     * @return, true if permission is already allowed for the app else it will return false
     */
    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions()
            return false
        }
    }

    /**
     * if application is not allowed for the ACCESS_FINE_LOCATION
     * then it will open pop-up to grant that permission
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION ) {
                registerLocationListner()
            }
        }
    }
}
