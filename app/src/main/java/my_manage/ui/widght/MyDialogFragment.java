package my_manage.ui.widght;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import org.apache.log4j.varia.FallbackErrorHandler;

import butterknife.Unbinder;

/**
 * 自定义的DialogFragment，定义了Unbinder并可自动销毁Unbinder
 * 底部靠齐，可全屏
 * @author inview
 * @Date 2020/12/7 16:00
 * @Description :
 */
public class MyDialogFragment extends DialogFragment {
    protected Unbinder bind;
    private   boolean  isFullWindow = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //全屏或指定大小
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        if (isFullWindow) {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } else {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        //底部靠齐
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);
    }

    /**
     * 全屏
     */
    protected void setFullWindow() {
        isFullWindow = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (bind != null)
            bind.unbind();
    }
}
