package com.enz.ac.uclive.zba29.travelerstrace.service

import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import kotlinx.coroutines.flow.first
fun formatDistance (distance: Double, measureSetting: String): String {
    val feetConversionFactor = 3.28084
    if (measureSetting == "Metric") {
        return if (distance >= 1000) {
            val kilometers = distance / 1000.0
            String.format("%.2f km", kilometers)
        } else {
            String.format("%.2f m", distance)
        }
    } else {
        val distanceInFeet = distance * feetConversionFactor
        return if (distanceInFeet >= 5280) {
            val miles = distanceInFeet / 5280.0
            "${"%.2f".format(miles)} mi"
        } else {
            "${"%.2f".format(distanceInFeet)} ft"
        }
    }
}

fun formatTime (duration: Int): String {
    val hours: Int = duration.div(60).div(60)
    val minutes: Int = duration.div(60)
    val seconds: Int = duration.rem(60)
    var baseString = ""
    if (hours > 0) baseString += hours.toString() + "h "
    if (minutes > 0) baseString += minutes.toString() + "m "
    baseString += seconds.toString() + "s "
    return baseString
}