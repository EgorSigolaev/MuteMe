package com.egorsigolaev.presentation.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment(layout: Int) : Fragment(layout) {

    fun showToast(message: Any) {
        when (message) {
            is String -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            is Int -> Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
        }
    }

}