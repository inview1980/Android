package my_manage.tool;

import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.widget.TextView;

public final class FontUtils {
    private static Typeface typeface;

    private FontUtils() {
    }

    public static FontUtils getInstance() {
        return new FontUtils();
    }

    public SpannableString getSpannableString(TextView view, String str) {
        if (typeface == null)
            typeface = Typeface.createFromAsset(view.getContext().getAssets(), "icons/iconfont.ttf");
        view.setTypeface(typeface);
        return new SpannableString(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
    }
}
