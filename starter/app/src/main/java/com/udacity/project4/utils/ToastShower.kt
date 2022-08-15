package com.udacity.project4.utils

import android.content.Context
import android.widget.Toast

interface ToastShower {
    fun showToast(message: String)
}

class ToastShowerImpl(private val context: Context) : ToastShower {
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}