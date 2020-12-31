package my_manage.ui.widght;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author inview
 * @Date 2020/12/28 10:48
 * @Description :
 */
class IconViewAlibabaGithub  extends AppCompatTextView {
    public IconViewAlibabaGithub(Context context) {
        super(context);
        init(context);
    }

    public IconViewAlibabaGithub(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconViewAlibabaGithub(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
//        设置字体图标
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"icons/iconfont2.ttf"));
    }
}
