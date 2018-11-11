package com.ifeins.tenbismonit.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;

/**
 * @author ifeins
 */

public class UiUtils {

    /**
     * This method assumes that the text that should be made clickable is surrounded with "<a>" and "</a>" tags.
     */
    public static void setClickableLink(@NonNull TextView view, @NonNull String text, @NonNull ClickableSpan span) {
        SpannableString ss = new SpannableString(Html.fromHtml(text));
        ss.setSpan(span, 0, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setHighlightColor(Color.TRANSPARENT);
        view.setText(ss);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
