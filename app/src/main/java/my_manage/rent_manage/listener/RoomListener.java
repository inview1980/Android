package my_manage.rent_manage.listener;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.orhanobut.dialogplus.DialogPlus;

import java.util.Optional;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.rent_manage.page.viewholder.AddRoomViewHolder;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

public final class RoomListener {
    /**
     * 小区编辑，新建
     */
    private static <T extends Activity & IShowList> void addCommunity(T activity, String communityString) {
        AddRoomViewHolder viewHolder = new AddRoomViewHolder(activity, communityString);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, view) -> {
                    if (R.id.rental_addRoom_OkBtn == view.getId()) {
                        //确定
                         addRoom(activity, viewHolder, dialog1);
                    }
                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder)
                .create();
        dialog.show();
    }

    private static <T extends Activity & IShowList> void addRoom(T activity, AddRoomViewHolder viewHolder, DialogPlus dialog1) {
        if (StrUtils.isAnyBlank(viewHolder.getCommunityName().getSelectedItem().toString(),
                viewHolder.getHouseNumber().getText().toString())) {
            showMessageDialog(activity, "小区名或房号不能为空！");
            return;
        }
        try {
            RoomDetails roomDetails = new RoomDetails();
            roomDetails.setCommunityName(viewHolder.getCommunityName().getSelectedItem().toString());
            roomDetails.setRoomNumber(viewHolder.getHouseNumber().getText().toString());
            roomDetails.setElectricMeter(viewHolder.getMeterNumber().getText().toString());
            roomDetails.setWaterMeter(viewHolder.getWaterMeter().getText().toString());
            if (StrUtils.isNotBlank(viewHolder.getArea().getText().toString()))
                roomDetails.setRoomArea(Double.parseDouble(viewHolder.getArea().getText().toString()));
            if (StrUtils.isNotBlank(viewHolder.getPropertyPrice().getText().toString()))
                roomDetails.setPropertyPrice(Double.parseDouble(viewHolder.getPropertyPrice().getText().toString()));
            boolean isOK = DbHelper.getInstance().saveRoomDes(roomDetails);
            if (isOK) {
                showMessage(activity, "保存房源成功");
                activity.showList();
            } else showMessage(activity, "保存失败");
        } catch (Exception e) {
            showMessage(activity, "保存失败");
            dialog1.dismiss();
        }
        dialog1.dismiss();
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
        addCommunity(activity, title);
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

    public static <T extends Activity & IShowList> void delCommunity(T activity, ShowRoomDetails showRoomDetails) {
        if (showRoomDetails == null) return;

        PageUtils.Log("删除小区");
        androidx.appcompat.app.AlertDialog.Builder d2 = new androidx.appcompat.app.AlertDialog.Builder(activity);
        d2.setTitle("警告:");
        d2.setMessage("确定要删除此小区所有房源吗?");
        d2.setCancelable(true);
        d2.setPositiveButton(R.string.ok_cn, (d, w) -> {
            boolean isOK = DbHelper.getInstance().delRoomDes(showRoomDetails.getRoomDetails().getCommunityName());
            if (isOK) {
                showMessage(activity, "删除此小区成功");
                activity.showList();
            } else showMessage(activity, "删除此小区失败");
        });
        d2.show();
    }

    /**
     * 彻底删除房源
     */
    public static <T extends Activity & IShowList> void deleteRoom(T activity, ShowRoomDetails showRoomDetails) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("彻底删除房源").setMessage("是否彻底删除该房源？");
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
            if (DbHelper.getInstance().deleteRoom(showRoomDetails)) {
                Toast.makeText(activity, "彻底删除房源成功", Toast.LENGTH_SHORT).show();
                activity.showList();
            } else {
                Toast.makeText(activity, "彻底删除房源失败", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
