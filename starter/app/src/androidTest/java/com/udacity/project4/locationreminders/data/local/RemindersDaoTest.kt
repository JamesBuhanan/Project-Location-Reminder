package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private val reminder = ReminderDTO(
        "title",
        "description",
        "location",
        (-360..360).random().toDouble(),
        (-360..360).random().toDouble()
    )
    private val remindersList = listOf(
        ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
    )

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminders() = runBlockingTest {
        // GIVEN
        database.reminderDao().saveReminder(reminder)
        // WHEN
        val reminders = database.reminderDao().getReminders()
        // THEN
        assertThat(reminders.size, equalTo(1))
        assertThat(reminders[0].id, equalTo(reminder.id))
        assertThat(reminders[0].title, equalTo(reminder.title))
        assertThat(reminders[0].description, equalTo(reminder.description))
        assertThat(reminders[0].location, equalTo(reminder.location))
        assertThat(reminders[0].latitude, equalTo(reminder.latitude))
        assertThat(reminders[0].longitude, equalTo(reminder.longitude))
    }

    @Test
    fun insertReminder_GetById() = runBlockingTest {
        // GIVEN
        database.reminderDao().saveReminder(reminder)
        // WHEN
        val loaded = database.reminderDao().getReminderById(reminder.id)
        // THEN
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, equalTo(reminder.id))
        assertThat(loaded.title, equalTo(reminder.title))
        assertThat(loaded.description, equalTo(reminder.description))
        assertThat(loaded.location, equalTo(reminder.location))
        assertThat(loaded.latitude, equalTo(reminder.latitude))
        assertThat(loaded.longitude, equalTo(reminder.longitude))
    }

    @Test
    fun getReminderByIdNotFound() = runBlockingTest {
        // GIVEN
        val reminderId = UUID.randomUUID().toString()
        // WHEN
        val loaded = database.reminderDao().getReminderById(reminderId)
        // THEN
        assertNull(loaded)
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        // GIVEN
        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }
        // WHEN
        database.reminderDao().deleteAllReminders()
        // THEN
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), equalTo(true))
    }
}