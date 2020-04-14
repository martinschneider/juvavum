package io.github.martinschneider.juvavum.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {

    public static int dpToPx(Resources r, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }
}
