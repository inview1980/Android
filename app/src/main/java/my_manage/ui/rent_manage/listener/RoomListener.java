package my_manage.ui.rent_manage.listener;

import android.app.Activity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;

public final class RoomListener {
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
