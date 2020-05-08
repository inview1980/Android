package my_manage.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import my_manage.rent_manage.RentalMainActivity;
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

    public void loadDB(RentalMainActivity rentalMainActivity) {
        // TODO: 2020/5/7
    }

    public void saveDB(RentalMainActivity activity) {
        // TODO: 2020/5/7
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "请选择一个要保存的文件夹"),
                    11);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
