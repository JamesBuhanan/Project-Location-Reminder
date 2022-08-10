package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.FakeReminderDao
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val list = listOf(
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

    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]
    private val newReminder = list[3]

    private lateinit var fakeRemindersDao: FakeReminderDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        fakeRemindersDao = FakeReminderDao()
        remindersLocalRepository = RemindersLocalRepository(
            fakeRemindersDao, Dispatchers.Main
        )
    }

    @Test
    fun savesToLocalCache() = runBlockingTest{
        assertThat(fakeRemindersDao.list).doesNotContain(newReminder)
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).doesNotContain(
            newReminder
        )

        //WHEN
        remindersLocalRepository.saveReminder(newReminder)

        //THEN
        assertThat(fakeRemindersDao.list).contains(newReminder)

        val result = remindersLocalRepository.getReminders() as? Result.Success
        assertThat(result?.data).contains(newReminder)
    }
    @Test
    fun getReminderByIdThatExistsInLocalCache() = runBlockingTest {

        assertThat((remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message).isEqualTo(
            "Reminder not found!")

        remindersLocalRepository.saveReminder(reminder1)

        //WHEN
        val loadedReminder = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Success)?.data

        //THEN
        assertThat<ReminderDTO>(loadedReminder as ReminderDTO, CoreMatchers.notNullValue())
        assertThat(loadedReminder.id, CoreMatchers.`is`(reminder1.id))
        assertThat(loadedReminder.title, CoreMatchers.`is`(reminder1.title))
        assertThat(loadedReminder.description, CoreMatchers.`is`(reminder1.description))
        assertThat(loadedReminder.location, CoreMatchers.`is`(reminder1.location))
        assertThat(loadedReminder.latitude, CoreMatchers.`is`(reminder1.latitude))
        assertThat(loadedReminder.longitude, CoreMatchers.`is`(reminder1.longitude))
    }
    @Test
    fun getReminderByIdThatDoesNotExistInLocalCache() = runBlockingTest {

        val message = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message
        assertThat<String>(message, CoreMatchers.notNullValue())
        assertThat(message).isEqualTo("Reminder not found!")

    }

    @Test
    fun deleteAllReminders_EmptyListFetchedFromLocalCache() = runBlockingTest {
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()

        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        //WHEN
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isNotEmpty()

        remindersLocalRepository.deleteAllReminders()

        //THEN
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()
    }
}