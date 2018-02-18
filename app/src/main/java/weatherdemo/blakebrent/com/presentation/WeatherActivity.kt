package weatherdemo.blakebrent.com.presentation

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_today.btn_sync_location
import kotlinx.android.synthetic.main.activity_today.iv_weather
import kotlinx.android.synthetic.main.activity_today.tv_day
import kotlinx.android.synthetic.main.activity_today.tv_forecast_description
import kotlinx.android.synthetic.main.activity_today.tv_forecast_max
import kotlinx.android.synthetic.main.activity_today.tv_forecast_min
import kotlinx.android.synthetic.main.activity_today.tv_incorrect_location
import kotlinx.android.synthetic.main.activity_today.tv_location
import weatherdemo.blakebrent.com.R
import weatherdemo.blakebrent.com.WeatherApplication
import weatherdemo.blakebrent.com.data.WeatherResponse
import weatherdemo.blakebrent.com.domain.controllers.checkLocationPermission
import weatherdemo.blakebrent.com.domain.controllers.getLocation
import weatherdemo.blakebrent.com.domain.controllers.requestLocationUpdates
import weatherdemo.blakebrent.com.domain.controllers.requestPermission
import weatherdemo.blakebrent.com.utils.LOCATION_PERMISSION_REQUEST_CODE
import weatherdemo.blakebrent.com.utils.getDateFormatted
import weatherdemo.blakebrent.com.utils.permission

class WeatherActivity : AppCompatActivity(), LocationListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today)
        getWeatherData()

        btn_sync_location.setOnClickListener {
            requestLocationUpdates(this, baseContext)
        }
    }

    private fun getWeatherData() {
        getWeather(getLocation(this, baseContext))
    }

    private fun getWeather(location: String) {
        WeatherApplication().getWeather(location)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                setWeather(it)
            }, {
                //TODO Log Non-Fatal exception (EG: Crashlytics)
            })

        if (checkLocationPermission(this, baseContext)) {
            btn_sync_location.visibility = View.VISIBLE
            tv_incorrect_location.visibility = View.VISIBLE
        } else {
            btn_sync_location.visibility = View.GONE
            tv_incorrect_location.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getWeatherData()
                } else {
                    requestRetryPermission()
                }
                return
            }
        }
    }

    private fun requestRetryPermission() {
        runOnUiThread({
            Snackbar.make(this.window.decorView, getString(R.string.permission_required_desc),
                Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry), {
                requestPermission(this, permission)
            })
        })
    }

    private fun setWeather(weatherResponse: WeatherResponse) {
        runOnUiThread({
            tv_day.text = getDateFormatted()
            tv_forecast_min.text = "Min\n${weatherResponse.main.temp_min} °C"
            tv_forecast_max.text = "Max\n${weatherResponse.main.temp_max} °C"
            tv_forecast_description.text = weatherResponse.weather[0].description
            tv_location.text = weatherResponse.name
            Glide.with(this)
                .load("${WeatherApplication.endpoint_images}/${weatherResponse.weather[0].icon}" +
                    ".png")
                .into(iv_weather)
        })
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        /* Override not implemented as it is an un-required abstract function from an api
            level class */
    }

    override fun onProviderEnabled(provider: String?) {
        /* Override not implemented as it is an un-required abstract function from an api
           level class */
    }

    override fun onProviderDisabled(provider: String?) {
        /* Override not implemented as it is an un-required abstract function from an api
           level class */
    }

    override fun onLocationChanged(location: Location?) {
        getWeatherData()
    }
}


