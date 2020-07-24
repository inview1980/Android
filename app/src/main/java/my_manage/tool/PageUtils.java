package my_manage.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import my_manage.iface.IShowList;
import my_manage.ui.password_box.R;
import my_manage.ui.common.CheckPassword;

public final class PageUtils {
    public static  String  Tag     = "MyManage";
    private static boolean isDebug = true;

    /**
     * 当前为debug时记录信息,生产环境不记录
     */
    public static void Log(String msg) {
        if (isDebug)
            Log.i(Tag, msg);
    }

    /**
     * 当前为debug时记录信息,生产环境不记录
     */
    public static void Error(String msg) {
        if (isDebug)
            Log.e(Tag, msg);
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static void isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            isDebug = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            isDebug = false;
        }
    }

    /**
     * 弹出Toast信息，并根据debug状态，记录相关信息
     */
    public static void showMessage(Context context, String msg, boolean isShowLong) {
        if (msg == null) msg = "";
        Log(msg);
        if (context == null) return;
        if (isShowLong)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出Toast信息，并根据debug状态，记录相关信息
     */
    public static void showMessage(Context context, String msg) {
        showMessage(context, msg, false);
    }

    /**
     * 输入框失去焦点则关闭
     */
    public static void closeInput(Activity activity, boolean b) {
        if (!b) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)//软键盘如果打开就关闭
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 设置所有EditText、TextView中的文字下划线
     */
    public static void setUnderline(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (final Field field : fields) {
                field.setAccessible(true);
                Object obj = field.get(object);
                if (field.getType() == EditText.class) {
                    ((EditText) obj).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    ((EditText) obj).getPaint().setAntiAlias(true);//抗锯齿

                } else if (field.getType() == TextView.class) {
                    ((TextView) obj).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    ((TextView) obj).getPaint().setAntiAlias(true);//抗锯齿
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射，将本类中控制所有的EditText、CheckBox、TextView、Spinner控件的setEnable方法
     */
    public static void setEnable(Object obj, boolean b) {
        try {
            Field[]  fields = obj.getClass().getDeclaredFields();
            Class<?> clazz  = Class.forName("android.view.View");
            for (final Field field : fields) {
                if (field.getType() == EditText.class || field.getType() == CheckBox.class
                        || field.getType() == Spinner.class) {
                    Method method = clazz.getMethod("setEnabled", boolean.class);
                    field.setAccessible(true);
                    method.invoke(field.get(obj), b);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static void getSwipeMenuItem(Context context, SwipeMenu menu, String title, int color, int iconId) {
        // create  item
        SwipeMenuItem item = new SwipeMenuItem(context);
        item.setBackground(new ColorDrawable(color));
        item.setWidth(dp2px(context, 70));
        item.setTitle(title);
        item.setTitleSize(18);
        item.setTitleColor(Color.WHITE);
        // set a icon
        item.setIcon(iconId);
        // add to menu
        menu.addMenuItem(item);
    }

    private static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }


    /**
     * 给TabLayout控件增加OnTabSelectedListener事件，在点击标签时，可以转到相应的页
     *
     * @param tab
     * @param viewPage
     */
    public static void addOnTabSelectedListenerByTabLayout(TabLayout tab, ViewPager viewPage) {
        tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPage) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPage.setCurrentItem(tab.getPosition());
            }
        });
    }

//    /**
//     * 背景颜色动画，闪烁
//     */
//    public static void backColorFlash(View view, int colorValues) {
//        if (colorValues == view.getResources().getColor(android.R.color.white)) {
//            ValueAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", colorValues);
//            animator.end();
//            return;
//        }
//        int[]         colors   = new int[]{colorValues ^ 0x77000000, colorValues & 0x00ffffff};
//        ValueAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", colors);//对背景色颜色进行改变，操作的属性为"backgroundColor",此处必须这样写，不能全小写,后面的颜色为在对应颜色间进行渐变
//        animator.setDuration((new Random().nextInt(4) + 4) * 100);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setEvaluator(new ArgbEvaluator());//如果要颜色渐变必须要ArgbEvaluator，来实现颜色之间的平滑变化，否则会出现颜色不规则跳动
//        animator.start();
//    }

    /**
     * 如果超过时间，要求输入密码；如果数据已变更，重新载入数据
     *
     * @param activity
     * @param time
     * @param <T>
     */
    public static <T extends Activity & IShowList> void checkUser(T activity, long time) {
        long t = activity.getResources().getInteger(R.integer.LockIntervalByMinute) * 60 * 1000;
//        long t = 10 * 1000;
        if (time == 0 || (new Date().getTime() - time) < t) {
            activity.showList();
        } else {
            Intent intent = new Intent(activity, CheckPassword.class);
            intent.putExtra("isLogin", false);
            activity.startActivityForResult(intent, 8080);
        }
    }
}
