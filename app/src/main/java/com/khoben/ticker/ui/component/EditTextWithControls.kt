package com.khoben.ticker.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import com.khoben.ticker.R
import com.khoben.ticker.common.Utils.dp

@SuppressLint("ClickableViewAccessibility")
class EditTextWithControls @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attributeSet, defStyleAttr) {

    enum class DRAWABLE_PLACEMENT {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    enum class STATE {
        NONE,
        BTN,   // like btn
        INPUT   // ready for input
    }

    private var currentState = STATE.NONE

    // back / search btn
    private val leftButton by lazy {
        compoundDrawables[DRAWABLE_PLACEMENT.LEFT.ordinal]
    }

    // clear text btn
    private val clearTextButton by lazy {
        compoundDrawables[DRAWABLE_PLACEMENT.RIGHT.ordinal]
    }

    init {
        configureView()
        setState(STATE.INPUT)
        initTouchListener()
        initTextChangedListener()
        initFocusChangedListener()
    }

    private fun setState(state: STATE) {
        if (state == currentState) return
        currentState = state
        when (state) {
            STATE.BTN -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_search,
                    0,
                    0,
                    0
                )
            }
            STATE.INPUT -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.arrow_back_new_24dp,
                    0,
                    R.drawable.ic_close_24dp,
                    0
                )
            }
            else -> {
            }
        }
    }

    private fun initFocusChangedListener() {
        setOnFocusChangeListener { _, hasFocus ->
            listener?.onFocusChanged(hasFocus)
        }
    }

    private fun configureView() {
        compoundDrawablePadding = 10.dp()
    }

    private fun initTouchListener() {
        setOnTouchListener(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // right icon (clear text)
                if (isClearTextButtonAvailable() && event.rawX >= right - compoundDrawablePadding - clearTextButton.bounds.width()) {
                    text?.clear()
                    return@OnTouchListener true
                    // left icon (back)
                } else if (event.rawX <= left + compoundDrawablePadding + leftButton.bounds.width()) {
                    if (currentState == STATE.INPUT) {
                        listener?.onBackClicked()
                    }
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun initTextChangedListener() {
        addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeClearTextButtonAvailableState(s.isNotEmpty())
                listener?.onTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun isClearTextButtonAvailable() =
        currentState == STATE.INPUT && clearTextButton.alpha == 255

    private fun changeClearTextButtonAvailableState(notEmpty: Boolean) {
        if (currentState != STATE.INPUT) return
        if (notEmpty) {
            clearTextButton.alpha = 255
        } else {
            clearTextButton.alpha = 0
        }
    }

    var listener: EditTextWithControlsListener? = null

    interface EditTextWithControlsListener {
        fun onBackClicked()
        fun onTextChanged(text: CharSequence)
        fun onFocusChanged(focused: Boolean)
    }

}