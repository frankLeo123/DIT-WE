package com.bupt.weeat.utils;

import android.content.res.Resources;

//dp变成px，单位转化
public class Utils {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
