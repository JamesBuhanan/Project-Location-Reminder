package com.udacity.project4.locationreminders.reminderslist

import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var latitude: Double?,
    var longitude: Double?,
    var location: String? = "Location: $latitude, $longitude",
    val id: String = UUID.randomUUID().toString()
) : Serializable