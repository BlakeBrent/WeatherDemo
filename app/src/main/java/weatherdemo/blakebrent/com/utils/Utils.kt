package weatherdemo.blakebrent.com.utils

import android.Manifest
import java.util.*

const val LOCATION_PERMISSION_REQUEST_CODE = 1337
const val permission = Manifest.permission.ACCESS_FINE_LOCATION
const val DEFAULT_LOCATION = "JOHANNESBURG"

fun getMonthNameFromInt(monthInt: Int): String {
    return when (monthInt) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "Max"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "Error_Month"
    }
}

fun getDateFormatted(): String {
    val c = Calendar.getInstance(TimeZone.getDefault())
    c.get(Calendar.YEAR)

    return "${c.get(Calendar.DAY_OF_MONTH)} ${getMonthNameFromInt(c.get(Calendar.MONTH))}" +
        " ${c.get(Calendar.YEAR)}"
}
