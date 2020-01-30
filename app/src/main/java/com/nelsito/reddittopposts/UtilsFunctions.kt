package com.nelsito.reddittopposts

import org.ocpsoft.prettytime.PrettyTime
import java.util.*

fun prettyTime(timestamp: Long): String {
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(Date(timestamp*1000))
}