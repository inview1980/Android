package my_manage.rent_manage.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import my_manage.password_box.R;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.tool.ExcelUtils;
import my_manage.tool.database.DbHelper;
import my_manage.rent_manage.page.RentalForHouseActivity;

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
    public void rebuildingDB(RentalMainActivity rentalMainActivity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(rentalMainActivity);
        dialog.setMessage("删除并重建数据库吗？")
                .setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
                    DbHelper.getInstance().rebuilding(rentalMainActivity.getApplicationContext());
                    rentalMainActivity.showList();
                })
                .show();
    }

}
