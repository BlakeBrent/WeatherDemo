package weatherdemo.blakebrent.com.domain.controllers

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import weatherdemo.blakebrent.com.R
import weatherdemo.blakebrent.com.WeatherApplication
import weatherdemo.blakebrent.com.presentation.WeatherActivity
import weatherdemo.blakebrent.com.utils.DEFAULT_LOCATION
import weatherdemo.blakebrent.com.utils.LOCATION_PERMISSION_REQUEST_CODE
import weatherdemo.blakebrent.com.utils.permission
import java.util.*

class LocationController(var context: Context) {
    fun getCurrentAddress(): Address? {
        val location: Location? = getMostAccurateLocation(context)
        return if (location == null) null else getAddress(context, location)
    }
}

fun getLocation(activity: WeatherActivity, context: Context): String {
    updateLocation(activity, context)
    val location = WeatherApplication.prefs.getString(context.getString(R.string.preference_location), DEFAULT_LOCATION)

    if (location.isNullOrEmpty()) {
        return DEFAULT_LOCATION
    }

    return location
}

private fun getAddress(context: Context, location: Location): Address? {
    return Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location
        .longitude, 1)[0]
}

private fun getMostAccurateLocation(context: Context): Location? {
    val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)
    var bestLocation: Location? = null
    for (provider in providers) {
        val l = locationManager.getLastKnownLocation(provider) ?: continue
        if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
            bestLocation = l
        }
    }
    return bestLocation
}

private fun updateLocation(activity: WeatherActivity, context: Context): String {
    if (!checkLocationPermission(activity, context)) {
        return DEFAULT_LOCATION
    }

    val address = LocationController(context)
        .getCurrentAddress()
    val addressLine = address?.subLocality

    val location: String = when (addressLine == null) {
        true -> DEFAULT_LOCATION
        false -> addressLine.toString()
    }

    WeatherApplication.prefs.edit().putString(context.getString(R.string.preference_location), location).apply()
    return location
}

fun checkLocationPermission(activity: WeatherActivity, context: Context): Boolean {
    when (ContextCompat.checkSelfPermission(context, permission)) {
        PackageManager.PERMISSION_GRANTED -> return true
        PackageManager.PERMISSION_DENIED -> requestPermission(activity, permission)
    }
    return false
}

fun requestPermission(activity: WeatherActivity, permission: String) {
    ActivityCompat.requestPermissions(activity,
        arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
}

fun requestLocationUpdates(activity: WeatherActivity, context: Context) {
    val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, activity, null)
    locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, activity, null)
    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, activity, null)
}


