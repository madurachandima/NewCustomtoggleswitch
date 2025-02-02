package com.madura.customtoggleswitch

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout


class CustomToggleSwitchButton (context: Context, var position: Int, var positionType: PositionType,
                                listener: Listener, layoutId: Int, var prepareDecorator: ToggleSwitchButtonDecorator,
                                var checkedDecorator: ViewDecorator?, var uncheckedDecorator: ViewDecorator?,
                                var checkedBackgroundColor: Int, var checkedBorderColor: Int,
                                var borderRadius: Float, var borderWidth: Float,
                                var uncheckedBackgroundColor: Int, var uncheckedBorderColor: Int,
                                var separatorColor: Int, var toggleMargin: Int) :
        LinearLayout(context), IRightToLeftProvider {

    interface ToggleSwitchButtonDecorator {
        fun decorate(toggleSwitchButton: CustomToggleSwitchButton, view: View, position: Int)
    }

    interface ViewDecorator {
        fun decorate(view: View, position: Int)
    }

    interface Listener {
        fun onToggleSwitchClicked(button: CustomToggleSwitchButton)
    }

    enum class PositionType {
        LEFT, CENTER, RIGHT
    }

    var toggleWidth                                 = 0
    var toggleHeight                                = LinearLayout.LayoutParams.MATCH_PARENT
    var isChecked                                   = false
    var rightToLeftProvider: IRightToLeftProvider = this

    var separator: View
    var view: View

    init {

        // Inflate Layout
        val inflater    = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutView  = inflater.inflate(R.layout.custom_toggle_switch_button, this, true) as LinearLayout
        val relativeLayoutContainer: RelativeLayout = layoutView.findViewById(R.id.relative_layout_container)

        // View
        val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)

        view  = LayoutInflater.from(context).inflate(layoutId, relativeLayoutContainer, false)
        relativeLayoutContainer.addView(view, params)

        // Bind Views
        separator = layoutView.findViewById(R.id.separator)
        val clickableWrapper: LinearLayout = findViewById(R.id.clickable_wrapper)

        // Setup View
        val layoutParams = LinearLayout.LayoutParams(toggleWidth, toggleHeight, 1.0f)

        if (toggleMargin > 0 && !isFirst()) {
            layoutParams.setMargins(toggleMargin, 0, 0, 0)
        }

        this.layoutParams = layoutParams

        this.orientation = HORIZONTAL
        this.background = getBackgroundDrawable(uncheckedBackgroundColor, uncheckedBorderColor)

        separator.setBackgroundColor(separatorColor)

        clickableWrapper.setOnClickListener {
            listener.onToggleSwitchClicked(this)
        }

        // Decorate
        prepareDecorator.decorate(this, view, position)
        uncheckedDecorator?.decorate(view, position)
    }

    // Public instance methods

    fun check() {
        this.background = getBackgroundDrawable(checkedBackgroundColor, checkedBorderColor)
        this.isChecked = true
        checkedDecorator?.decorate(view, position)
    }

    fun uncheck() {
        this.background = getBackgroundDrawable(uncheckedBackgroundColor, uncheckedBorderColor)
        this.isChecked = false
        uncheckedDecorator?.decorate(view, position)
    }

    fun setSeparatorVisibility(isSeparatorVisible : Boolean) {
        separator.visibility = if (isSeparatorVisible) View.VISIBLE else View.GONE
    }

    // Private instance methods

    private fun getBackgroundDrawable(backgroundColor : Int, borderColor : Int) : GradientDrawable {

        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(backgroundColor)

        gradientDrawable.cornerRadii = getCornerRadii()

        if (borderWidth > 0) {
            gradientDrawable.setStroke(borderWidth.toInt(), borderColor)
        }

        return gradientDrawable
    }

    private fun getCornerRadii(): FloatArray {
        if (toggleMargin > 0) {
            return floatArrayOf(borderRadius,borderRadius,
                    borderRadius, borderRadius,
                    borderRadius, borderRadius,
                    borderRadius,borderRadius)
        }
        else {
            val isRightToLeft = rightToLeftProvider.isRightToLeft()
            when(positionType) {
                PositionType.LEFT ->
                    return if (isRightToLeft) getRightCornerRadii() else getLeftCornerRadii()

                PositionType.RIGHT ->
                    return if (isRightToLeft) getLeftCornerRadii() else getRightCornerRadii()

                else -> return floatArrayOf(0f,0f,0f, 0f, 0f, 0f, 0f,0f)
            }
        }
    }

    override fun isRightToLeft(): Boolean {
        return resources.getBoolean(R.bool.is_right_to_left)
    }

    private fun getRightCornerRadii(): FloatArray {
        return floatArrayOf(0f,0f,borderRadius, borderRadius, borderRadius, borderRadius, 0f,0f)
    }

    private fun getLeftCornerRadii(): FloatArray {
        return floatArrayOf(borderRadius, borderRadius, 0f,0f,0f,0f, borderRadius, borderRadius)
    }

    private fun isFirst() : Boolean {
        if (rightToLeftProvider.isRightToLeft()) {
            return positionType == PositionType.RIGHT
        }
        else {
            return positionType == PositionType.LEFT
        }
    }
}