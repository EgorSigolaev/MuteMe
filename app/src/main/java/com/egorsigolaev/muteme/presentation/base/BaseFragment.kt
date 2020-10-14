package com.egorsigolaev.muteme.presentation.base

import android.app.Activity
import android.app.ProgressDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment(layout: Int) : Fragment(layout) {

    private var insetMarginBottom: Int? = null
    private var insetMarginTop: Int? = null
    private val loadingDialog: ProgressDialog? = null

    fun showToast(message: Any) {
        when (message) {
            is String -> Toast.makeText(
                requireContext(),
                getStringMessage(message = message),
                Toast.LENGTH_SHORT
            ).show()
            is Int -> Toast.makeText(
                requireContext(),
                getStringMessage(message = message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun hideKeyboard(){
        val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getStringMessage(message: Any): String{
        return when (message) {
            is String -> {
                message
            }
            is Int -> {
                requireContext().getString(message)
            }
            else -> {
                throw RuntimeException("not all types checked")
            }
        }
    }

    fun startLoading(message: Any) {
        val dialog = getLoadingDialog()
        dialog.setMessage(getStringMessage(message = message))
        dialog.show()
    }

    fun stopLoading(){
        val dialog = getLoadingDialog()
        if(dialog.isShowing){
            dialog.cancel()
        }
    }

    private fun getLoadingDialog(): ProgressDialog {
        loadingDialog?.let {
            return it
        } ?: run {
            val dialog = ProgressDialog(context)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            return dialog
        }
    }

}