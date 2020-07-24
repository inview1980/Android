package my_manage.ui.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import my_manage.iface.IShowList;
import my_manage.ui.password_box.R;
import my_manage.ui.rent_manage.RentalMainActivity;
import my_manage.tool.database.DbHelper;
import my_manage.ui.rent_manage.page.RentalForHouseActivity;

public class RentalMainActivityListener {

    public void showDeletedRoom(Activity activity) {
        Intent intent = new Intent(activity, RentalForHouseActivity.class);
        intent.putExtra("title", "显示已删除房源");
        activity.startActivity(intent);
    }

    public void showRoomDetails(RentalMainActivity activity, String communityName) {
        Intent intent = new Intent(activity, RentalForHouseActivity.class);
        intent.putExtra("title", communityName);
        activity.startActivity(intent);
    }

    /**
     * 删除并重建数据库
     */
    public <T extends Activity & IShowList> void rebuildingDB(T activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage("删除并重建数据库吗？")
                .setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
                    DbHelper.getInstance().rebuilding();
                    activity.showList();
                })
                .show();
    }

}
