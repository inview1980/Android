package my_manage.tool.menuEnum;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.iface.IActivityMenuForData;
import my_manage.password_box.R;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.page.RentalForHouseActivity;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.EnumUtils;

/**
 * RentalMainActivity类处理GridView的项目单击事件处理
 */
@AllArgsConstructor
@Getter
public enum RentalMainItemLongClickEnum implements IActivityMenuForData<RentalMainActivity> {

    Add(2, "增加房源") {
        @Override
        public void run(RentalMainActivity activity, List<ShowRoomDetails> data, int position) {
//            if (data.size() < position - 1) return;

            Log.i(activity.getPackageName(), "增加房源");
            String title = "";
            if (position >= 0 && data != null && data.size() >= position) {
                title = data.get(position).getCommunityName();
                if (title.contains("全部")) title = "";
            }
            EnumUtils.communityChange(activity, title);
        }
    },
    Delete(3, "删除小区") {
        @Override
        public void run(RentalMainActivity activity, List<ShowRoomDetails> data, int position) {
            if (data.size() < position - 2) return;
            Log.i(activity.getPackageName(), "删除小区");
            androidx.appcompat.app.AlertDialog.Builder d2 = new androidx.appcompat.app.AlertDialog.Builder(activity);
            d2.setTitle("警告:");
            d2.setMessage("确定要删除此小区所有房源吗?");
            d2.setCancelable(true);
            d2.setPositiveButton(R.string.ok_cn, (d, w) -> {
                boolean isOK = DbHelper.getInstance().delRoomDes(data.get(position).getCommunityName());
                if (isOK) {
                    showMessage(activity, "删除此小区成功");
                    activity.showList();
                } else showMessage(activity, "删除此小区失败");
            });
            d2.show();
        }
    },
    ShowRooms(4, "显示房源") {
        @Override
        public void run(RentalMainActivity activity, List<ShowRoomDetails> data, int position) {
            Intent intent = new Intent(activity, RentalForHouseActivity.class);
            intent.putExtra("title", RentalMainActivity.showRoomForMainList.get(position).getCommunityName());
            activity.startActivity(intent);
            Log.i(activity.getPackageName(), "显示房源");
        }
    };


    private int index;
    private String name;


    private static void showMessage(Activity activity, String msg) {
        Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }


}
