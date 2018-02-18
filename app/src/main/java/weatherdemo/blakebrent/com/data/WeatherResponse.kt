package weatherdemo.blakebrent.com.data

data class WeatherResponse(
    val cod: String,
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    val pressure: Float,
    val humidity: Float,
    val temp_min: Float,
    val temp_max: Float
)
