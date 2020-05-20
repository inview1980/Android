package my_manage.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageViewPagerHome;

public final class PageUtils {
    public static String Tag="MyManage";


    public static void showMessage(Context context,  String msg) {
        Log.i( Tag,msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 调用密码详情页面
     * @param activity PasswordManageActivity类
     */
    public static void callPasswordManageItemDetails(PasswordManageActivity activity, int item) {
        Intent intent = new Intent(activity, PasswordManageViewPagerHome.class);
        Bundle bundle=new Bundle();
        bundle.putInt("currentItem", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 输入框失去焦点则关闭
     */
    public static void closeInput(Activity activity,boolean b){
        if (!b) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm!=null)//软键盘如果打开就关闭
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
    public static void setEnable(Object obj,boolean b) {
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


    public static void getSwipeMenuItem(Context context,SwipeMenu menu, String title, int color, int iconId) {
        // create  item
        SwipeMenuItem item = new SwipeMenuItem(context);
        item.setBackground(new ColorDrawable(color));
        item.setWidth(dp2px(context,70));
        item.setTitle(title);
        item.setTitleSize(18);
        item.setTitleColor(Color.WHITE);
        // set a icon
        item.setIcon(iconId);
        // add to menu
        menu.addMenuItem(item);
    }
    private static int dp2px(Context context,int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
