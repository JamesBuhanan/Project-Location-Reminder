package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    //    val selectedPOI = MutableLiveData<PointOfInterest>()
    private val _latitude = MutableLiveData<Double>()
    private val _longitude = MutableLiveData<Double>()
    private val _reminderSelectedLocationStr = MutableLiveData<String>()

    val latitude: LiveData<Double>
        get() = _latitude
    val longitude: LiveData<Double>
        get() = _longitude
    val reminderSelectedLocationStr: LiveData<String>
        get() = _reminderSelectedLocationStr

    fun setLatLng(latLng: LatLng) {
        _latitude.value = latLng.latitude
        _longitude.value = latLng.longitude

        // set a string made from concatenated lat + lng
        _reminderSelectedLocationStr.value = "${latLng.latitude.toInt()}, ${latLng.longitude.toInt()}"
    }


    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        _reminderSelectedLocationStr.value = null
        _latitude.value = null
        _longitude.value = null
    }


    fun validateAndSaveReminder(reminderData: ReminderDataItem): Boolean {
        val isValid = validateEnteredData(reminderData)
        if (isValid) {
            saveReminder(reminderData)
        }
        return isValid
    }

    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}