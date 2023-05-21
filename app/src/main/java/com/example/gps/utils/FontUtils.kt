package com.example.gps.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

class FontUtils() {
    companion object {
        fun setFont(context: Context, vararg textView: TextView) {
            textView.forEach {
                it.typeface = Typeface.createFromAsset(context.assets, "font_lcd.ttf")
            }
        }
    }
}