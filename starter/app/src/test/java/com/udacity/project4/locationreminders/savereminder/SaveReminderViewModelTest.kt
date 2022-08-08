package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val list = listOf(
        ReminderDataItem(
            title = "title",
            description = "description",
            latitude = (-360..360).random().toDouble(),
            longitude = (-360..360).random().toDouble(),
            location = "location"
        )
    )
    private val firstReminder = list[0]

    private val fakeDataSource = FakeDataSource()
    private val saveReminderViewModel =
        SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testLatLong() {
        //WHEN
        saveReminderViewModel.setLatLng(LatLng(1.0, 1.0))
        //THEN
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), equalTo(1.0))
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), equalTo(1.0))
    }

    @Test
    fun check_loading() = runBlockingTest {
        //WHEN
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(firstReminder)
        //THEN
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            equalTo(true)
        )
    }

    @Test
    fun shouldReturnError() {
        //GIVEN
        fakeDataSource.shouldSucceed = false
        firstReminder.title = null

        //WHEN
        saveReminderViewModel.validateAndSaveReminder(firstReminder)

        //THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            equalTo(R.string.err_enter_title)
        )
    }
}