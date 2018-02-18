package weatherdemo.blakebrent.com

import android.app.Application
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url
import weatherdemo.blakebrent.com.data.WeatherResponse
import java.util.concurrent.TimeUnit

class WeatherApplication : Application() {
    companion object {
        const val endpoint_weather = "https://api.openweathermap.org/data/2.5/weather/"
        const val endpoint_images = "https://openweathermap.org/img/w"
        const val APPID = "58b9060fa84a098660a38b9fb7540275"
        lateinit var retrofit: Retrofit
        lateinit var weatherApi: WeatherApiInterface
        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        retrofit = setupRetrofit()
        weatherApi = retrofit.create(WeatherApiInterface::class.java)
        prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
    }

    private fun setupRetrofit(): Retrofit {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val cacheSize = 10 * 1024 * 1024
        val cache = Cache(Environment.getDataDirectory(), cacheSize.toLong())

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .baseUrl(endpoint_weather)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build()
    }

    fun getWeather(location: String): Single<WeatherResponse> {
        val queryMap: Map<String, String> = hashMapOf(
            "q" to location,
            "APPID" to APPID,
            "units" to "metric"
        )
        return weatherApi.getWeather(endpoint_weather, queryMap)
    }

    interface WeatherApiInterface {
        //Weather
        @GET
        fun getWeather(@Url url: String, @QueryMap queryMap: Map<String, String>):
            Single<WeatherResponse>
    }
}
