package com.udacity.project4.locationreminders

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource : ReminderDataSource {
    private val list = mutableListOf<ReminderDTO>()

    @VisibleForTesting
    var shouldSucceed: Boolean = true

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return when (shouldSucceed) {
            true -> Result.Success(list)
            else -> Result.Error("No reminders found", 500)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        list.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return when (shouldSucceed) {
            true -> Result.Success(list.first { it.id == id })
            else -> Result.Error("It failed!", 500)
        }
    }

    override suspend fun deleteAllReminders() {
        list.clear()
    }
}