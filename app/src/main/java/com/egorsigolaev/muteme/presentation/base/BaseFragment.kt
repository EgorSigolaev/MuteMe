package com.egorsigolaev.muteme.presentation.base

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.egorsigolaev.muteme.MuteMeApp.Companion.TAG

open class BaseFragment(layout: Int) : Fragment(layout) {

    private var insetMarginBottom: Int? = null
    private var insetMarginTop: Int? = null

    fun showToast(message: Any) {
        when (message) {
            is String -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            is Int -> Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
        }
    }

//    fun applyInsets(views: Array<View>){
//        for(view in views){
//            val params = view.layoutParams as ViewGroup.MarginLayoutParams
//            view.setOnApplyWindowInsetsListener { v, insets ->
//
//                if(insets.systemWindowInsetTop == 0 && insets.systemWindowInsetBottom == 0){
//                    insets
//                }
//
//                //Log.d(TAG, "applyInsets: viewId = ${view.id}")
//                Log.d(TAG, "applyInsets: insetMarginBottom = $insetMarginBottom")
//                Log.d(TAG, "applyInsets: insetMarginTop = $insetMarginTop")
//
//                //Top
//                insetMarginTop?.let {
//                    params.topMargin = insets.systemWindowInsetTop + view.marginTop
//                    view.layoutParams = params
//                } ?: run{
//                    insetMarginTop = insets.systemWindowInsetTop
//                    params.topMargin = insets.systemWindowInsetTop + view.marginTop
//                    view.layoutParams = params
//                }
//
//                // Bottom
//                insetMarginBottom?.let {
//                    params.bottomMargin = insets.systemWindowInsetBottom + view.marginBottom
//                    view.layoutParams = params
//                } ?: run{
//                    insetMarginBottom = insets.systemWindowInsetBottom
//                    params.bottomMargin = insets.systemWindowInsetBottom + view.marginBottom
//                    view.layoutParams = params
//                }
//
//                insets
//            }
//        }
//
//    }

    fun applyInsets(view: View){
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        view.rootView.setOnApplyWindowInsetsListener { v, insets ->

            Log.d(TAG, "applyInsets: viewId = ${view.id}")
            Log.d(TAG, "applyInsets: insetMarginBottom = $insetMarginBottom")
            Log.d(TAG, "applyInsets: insetMarginTop = $insetMarginTop")
            Log.d(TAG, "applyInsets: systemWindowInsetTop = ${insets.systemWindowInsetTop}")
            Log.d(TAG, "applyInsets: systemWindowInsetBottom = ${insets.systemWindowInsetBottom}")

            //Top
            insetMarginTop?.let {
                params.topMargin = insets.systemWindowInsetTop + view.marginTop
                view.layoutParams = params
            } ?: run{
                insetMarginTop = insets.systemWindowInsetTop
                params.topMargin = insets.systemWindowInsetTop + view.marginTop
                view.layoutParams = params
            }

            // Bottom
            insetMarginBottom?.let {
                params.bottomMargin = insets.systemWindowInsetBottom + view.marginBottom
                view.layoutParams = params
            } ?: run{
                insetMarginBottom = insets.systemWindowInsetBottom
                params.bottomMargin = insets.systemWindowInsetBottom + view.marginBottom
                view.layoutParams = params
            }

            insets.consumeSystemWindowInsets()
        }
    }

    fun initMarginBottomWithInsets(view: View){
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        ViewCompat.setOnApplyWindowInsetsListener(view.rootView) { _, insets ->
            insetMarginBottom?.let {
                params.bottomMargin = view.marginBottom
                view.layoutParams = params
            } ?: run{
                insetMarginBottom = insets.systemWindowInsetBottom
                params.bottomMargin = insets.systemWindowInsetBottom + view.marginBottom
                view.layoutParams = params
            }

            insets
        }
    }

    fun initMarginTopWithInsets(view: View){
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        ViewCompat.setOnApplyWindowInsetsListener(view.rootView) { _, insets ->
            insetMarginTop?.let {
                params.topMargin = view.marginTop
                view.layoutParams = params
            } ?: run{
                insetMarginTop = insets.systemWindowInsetTop
                params.topMargin = insets.systemWindowInsetTop + view.marginTop
                view.layoutParams = params
            }

            insets
        }
    }

//    private val onApplyWindowInsetsListener: View.OnApplyWindowInsetsListener = View.OnApplyWindowInsetsListener { v, insets ->
//        // Bottom margins
//        initMarginBottomWithInsets(view = v)
//        // Top margins
//        initMarginTopWithInsets(view = v)
//        insets
//    }

}