package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDao


class FakeReminderDao : RemindersDao {

    var shouldReturnError = false

    val list = mutableListOf<ReminderDTO>()

    override suspend fun getReminders(): List<ReminderDTO> {
        if (shouldReturnError) {
            throw (Exception("Test exception"))
        }

        return list
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        return list.firstOrNull {
            it.id == reminderId
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        list.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        list.clear()
    }
}