package my_manage.rent_manage.listener;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.apache.poi.ss.formula.functions.T;

import java.util.Optional;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.page.viewholder.AddRoomViewHolder;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.StrUtils;

public final class RoomListener {
    /**
     * 小区编辑，新建
     */
    public static <T extends Activity & IShowList> void changedCommunity(T activity, String communityString) {
        ViewHolder viewHolder = new AddRoomViewHolder(activity, R.layout.add_room_dialog, communityString);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, view) -> {
                    if (R.id.rental_addRoom_OkBtn == view.getId()) {
                        //确定
                        View    v                = viewHolder.getInflatedView();
                        Spinner communityName =v.findViewById(R.id.rental_addRoom_communityName);
                        String comStr = communityName.getSelectedItem().toString();
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

    /**
     * 恢复已删除的房源
     */
    public static <T extends Activity & IShowList> void recoverDel(T activity, ShowRoomDetails showRoomDetails) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("恢复删除房源").setMessage("是否恢复已删除的房源？");
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
            if (DbHelper.getInstance().restoreDelete(showRoomDetails)) {
                Toast.makeText(activity, "恢复房源成功", Toast.LENGTH_SHORT).show();
                activity.showList();
            } else {
                Toast.makeText(activity, "恢复房源失败", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    /**
     * 新建小区、户型
     */
    public static <T extends Activity & IShowList> void addRoomDetails(T activity, ShowRoomDetails room) {
        if (room == null)
            room = new ShowRoomDetails();

        String title = Optional.ofNullable(room.getRoomDetails().getCommunityName()).orElse("");
        if (title.contains("全部"))
            title = "";
        RoomListener.changedCommunity(activity, title);
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

    private static void showMessage(Activity activity, String msg) {
        Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
