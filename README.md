# GPSLocation
Kotlin application to grab GPS location through Google Play Service 

#  Installation
Add below dependancy in application build.gradle
```gradle
dependencies {
    compile 'com.google.android.gms:play-services:11.2.2'
    compile 'com.google.android.gms:play-services-location:11.2.2'
}
```
Also include the following required permission in your manifest.
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

Include following veriables at class level
```kotlin
private var mLocationRequest: LocationRequest? = null
private val UPDATE_INTERVAL:Long = 10 * 1000;
private val FASTEST_INTERVAL:Long = 2000;
```
Add following method to register and intialize all necessary objects to grab the GPS location and call it in onstart method
```kotlin
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
```
Add following method to show toast for updated location
```kotlin
private fun onLocationChanged(location: Location) {
        // create message for toast with updated latitude and longitude
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        // show toast message with updated location
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()

    }
```
Below method to check the permission
```kotlin
private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions()
            return false
        }
    }
```
Request permission ACCESS_FINE_LOCATION to allow to grab the GPS location
```kotlin
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
```