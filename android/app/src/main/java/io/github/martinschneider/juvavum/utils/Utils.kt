package io.github.martinschneider.juvavum.utils

import android.content.res.Resources
import android.util.TypedValue

object Utils {
    @JvmStatic
    fun dpToPx(r: Resources, dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
        ).toInt()
    }
}