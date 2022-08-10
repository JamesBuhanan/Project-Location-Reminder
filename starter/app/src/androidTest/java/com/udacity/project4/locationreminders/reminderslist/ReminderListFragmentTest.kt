package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest :
    AutoCloseKoinTest() {
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel
    private val list = listOf(
        ReminderDTO(
            "title",
            "description",
            "unique location",
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
    fun setup() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel =
            RemindersListViewModel(getApplicationContext(), fakeDataSource)
        stopKoin()

        val myModule = module {
            single {
                reminderListViewModel
            }
        }
        // new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun displayRemindersList() = runBlockingTest {
        // GIVEN
        list.forEach {
            fakeDataSource.saveReminder(it)
        }
        val reminders = (fakeDataSource.getReminders() as? Result.Success)?.data
        val firstItem = reminders!![0]

        // WHEN
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(
            Matchers.allOf(
                withText(firstItem.location),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.reminderCardView),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        ).check(matches(withText(firstItem.location)))
    }

    @Test
    fun navigateToAddReminder() = runBlockingTest {
        // WHEN - Details fragment launched to display task
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun errorSnackBarShown() = runBlockingTest {
        fakeDataSource.deleteAllReminders()

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // WHEN - Details fragment launched to display task
        onView(withText("No reminders found"))
            .check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}