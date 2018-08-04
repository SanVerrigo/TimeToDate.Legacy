package com.verrigo.timetodate;

import android.content.res.Resources;
import android.widget.ImageView;

/**
 * Created by Verrigo on 28.07.2018.
 */

public class UiUtils {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
