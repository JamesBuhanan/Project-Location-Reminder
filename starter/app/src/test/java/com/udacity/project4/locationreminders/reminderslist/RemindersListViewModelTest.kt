package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNot
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private val list = listOf(
        ReminderDTO("title", "description", "location", 0.0, 0.0),
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
    private val fakeDataSource = FakeDataSource()

    // CLASS UNDER TEST
    private val reminderListViewModel = RemindersListViewModel(
        ApplicationProvider.getApplicationContext(),
        fakeDataSource
    )

    @Test
    fun getRemindersList() = runBlockingTest {
        // GIVEN
        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)

        // WHEN
        reminderListViewModel.loadReminders()

        // THEN
        Assert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue(), (IsNot.not(emptyList()))
        )
        Assert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue().size,
            CoreMatchers.`is`(3)
        )
    }
}