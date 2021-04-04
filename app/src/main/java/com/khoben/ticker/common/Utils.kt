package com.khoben.ticker.common

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import java.io.File

object Utils {

    fun Int.dp(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun View.hideKeyboard() {
        val inputMethodManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * https://developer.squareup.com/blog/showing-the-android-keyboard-reliably/
     * @receiver View
     */
    fun View.focusAndShowKeyboard() {
        /**
         * This is to be called when the window already has focus.
         */
        fun View.showTheKeyboardNow() {
            if (isFocused) {
                post {
                    // We still post the call, just in case we are being notified of the windows focus
                    // but InputMethodManager didn't get properly setup yet.
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        requestFocus()
        if (hasWindowFocus()) {
            // No need to wait for the window to get focus.
            showTheKeyboardNow()
        } else {
            // We need to wait until the window gets focus.
            viewTreeObserver.addOnWindowFocusChangeListener(
                object : ViewTreeObserver.OnWindowFocusChangeListener {
                    override fun onWindowFocusChanged(hasFocus: Boolean) {
                        // This notification will arrive just before the InputMethodManager gets set up.
                        if (hasFocus) {
                            this@focusAndShowKeyboard.showTheKeyboardNow()
                            // It’s very important to remove this listener once we are done.
                            viewTreeObserver.removeOnWindowFocusChangeListener(this)
                        }
                    }
                })
        }
    }

    fun checkIfExists(path: String) = File(path).exists()
    private fun getTypedValue(context: Context, @AttrRes attr: Int): TypedValue {
        return TypedValue().also { typedValue ->
            context.theme.resolveAttribute(
                attr,
                typedValue,
                true
            )
        }
    }
    /**
     * Get color from attribute resource
     *
     * @param context Application context
     * @param attr Attribute resource ID
     * @return Color
     */
    fun getColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = getTypedValue(context, attr)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }
}