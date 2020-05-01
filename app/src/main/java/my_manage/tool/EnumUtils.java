package my_manage.tool;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import my_manage.iface.IActivityMenu;
import my_manage.iface.IActivityMenuForData;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.page.viewholder.AddRoomViewHolder;
import my_manage.rent_manage.pojo.RoomDetails;

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

    /**
     * 小区编辑，新建
     */
    public static <T extends Activity & IShowList> void communityChange(T activity, String communityString) {
        ViewHolder viewHolder = new AddRoomViewHolder(activity, R.layout.add_room_dialog, communityString);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, view) -> {
                    if (R.id.rental_addRoom_OkBtn == view.getId()) {
                        //确定
                        View v = viewHolder.getInflatedView();
                        AutoCompleteTextView completeTextView=v.findViewById(R.id.rental_addRoom_communityName);
                        completeTextView.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1,
                                DbHelper.getInstance().getCommunityNames()));
                        String comStr = completeTextView.getText().toString();
                        String houseStr = ((EditText) v.findViewById(R.id.rental_addRoom_houseNumber)).getText().toString();
                        String areaStr = ((EditText) v.findViewById(R.id.rental_addRoom_area)).getText().toString();
                        String meterStr = ((EditText) v.findViewById(R.id.rental_addRoom_meterNumber)).getText().toString();
                        String proStr = ((EditText) v.findViewById(R.id.rental_addRoom_propertyPrice)).getText().toString();
                        if (StrUtils.isAnyBlank(comStr, houseStr)) {
                            showMessageDialog(activity, "小区名或房号不能为空！");
                            return;
                        } else {
                            try {
                                RoomDetails roomDetails = new RoomDetails();
                                roomDetails.setCommunityName(comStr);
                                roomDetails.setRoomNumber(houseStr);
                                roomDetails.setElectricMeter(meterStr);
                                if (StrUtils.isNotBlank(areaStr))
                                    roomDetails.setRoomArea(Double.parseDouble(areaStr));
                                if (StrUtils.isNotBlank(proStr))
                                    roomDetails.setPropertyPrice(Double.parseDouble(proStr));
                                boolean isOK = DbHelper.getInstance().saveRoomDes(roomDetails);
                                if (isOK) {
                                    showMessage(activity, "保存房源成功");
                                    activity.showList();
                                } else showMessage(activity, "保存失败");
                            } catch (Exception e) {
                                showMessage(activity, "保存失败");
                                dialog1.dismiss();
                            }
                        }
                        dialog1.dismiss();
                    }
                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder)
                .create();
        dialog.show();
    }

    private static void showMessage(Activity activity, String msg) {
        Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void showMessageDialog(Activity activity, String s) {
        androidx.appcompat.app.AlertDialog.Builder d2 = new androidx.appcompat.app.AlertDialog.Builder(activity);
        d2.setTitle("警告:");
        d2.setMessage(s);
        d2.setCancelable(true);
        d2.setPositiveButton(R.string.ok_cn, (d, w) -> {
        });
        d2.show();
    }
}
