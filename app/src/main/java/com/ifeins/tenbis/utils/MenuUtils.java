package com.ifeins.tenbis.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;

/**
 * @author ifeins
 */

public class MenuUtils {

    public static void tintMenuItemDrawable(@NonNull Context context, @NonNull Menu menu,
                                            @IdRes int menuItemResId, @ColorRes int colorResId) {
        Drawable drawable = DrawableCompat.wrap(menu.findItem(menuItemResId).getIcon());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorResId));
        menu.findItem(menuItemResId).setIcon(drawable);
    }
}
