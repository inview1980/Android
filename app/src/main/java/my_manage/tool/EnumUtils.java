package my_manage.tool;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import java.util.List;

import my_manage.password_box.iface.IActivityMenu;
import my_manage.password_box.iface.IActivityMenuForData;

public final class EnumUtils {
    /**
     * 菜单的初始化
     *
     * @param <T>      枚举的一个值
     * @param activity
     * @param t        枚举，继承IActivityMenu接口
     * @param view
     * @param position 当前选中的控件（如ListView中当前Item）的编号
     */
    public static <T extends Enum<T> & IActivityMenu> boolean menuInit(Activity activity, T t, View view,  int position) {
        //初始化菜单
        PopupMenu longClickMenu = new PopupMenu(activity, view);
        T[] enums = t.getDeclaringClass().getEnumConstants();
        for (T e : enums) {
            longClickMenu.getMenu().add(0, e.getIndex(), Menu.NONE, e.getName());
        }
        //menu的item点击事件
        longClickMenu.setOnMenuItemClickListener(item -> {
            getEnum(t, item.getItemId()).run(activity,position);
            return true;
        });
        longClickMenu.show();
        return true;
    }
    /**
     * 菜单的初始化
     *
     * @param <T>      枚举的一个值
     * @param activity
     * @param t        枚举，继承IActivityMenu接口
     * @param view
     * @param position 当前选中的控件（如ListView中当前Item）的编号
     */
    public static <T extends Enum<T> & IActivityMenuForData> boolean menuInit(Activity activity, T t, View view, List data, int position) {
        //初始化菜单
        PopupMenu longClickMenu = new PopupMenu(activity, view);
        T[] enums = t.getDeclaringClass().getEnumConstants();
        for (T e : enums) {
            longClickMenu.getMenu().add(0, e.getIndex(), Menu.NONE, e.getName());
        }
        //menu的item点击事件
        longClickMenu.setOnMenuItemClickListener(item -> {
            getEnumForData(t, item.getItemId()).run(activity,data,position);
            return true;
        });
        longClickMenu.show();
        return true;
    }
    /**
     * 通过数字序号获取Enum中指定的项
     * @param t Enum的其中一个实例
     * @param index
     * @param <T>
     */
    private static <T extends Enum<T> & IActivityMenu> T getEnum(T t, int index) {
        T[] enums = t.getDeclaringClass().getEnumConstants();
        for (T e : enums) {
            if (e.getIndex() == index) {
                return e;
            }
        }
        return enums[0];
    }
    /**
     * 通过数字序号获取Enum中指定的项
     * @param t Enum的其中一个实例
     * @param index
     * @param <T>
     */
    private static <T extends Enum<T> & IActivityMenuForData> T getEnumForData(T t, int index) {
        T[] enums = t.getDeclaringClass().getEnumConstants();
        for (T e : enums) {
            if (e.getIndex() == index) {
                return e;
            }
        }
        return enums[0];
    }
}
