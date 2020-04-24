package my_manage.password_box.menuEnum;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.password_box.R;
import my_manage.password_box.iface.IActivityMenuForData;
import my_manage.tool.StrUtils;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.page.RentalForHouse;
import my_manage.rent_manage.page.viewholder.AddRoomViewHolder;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomForMain;

/**
 * RentalMainActivity类处理GridView的项目单击事件处理
 */
@AllArgsConstructor
@Getter
public enum RentalMainItemLongClickEnum implements IActivityMenuForData<RentalMainActivity> {

    Add(2, "增加房源") {
        @Override
        public void run(RentalMainActivity activity, List data, int position) {
            Log.i(activity.getPackageName(), "增加房源");
            String title = "";
            if (position >= 0 && data != null && data.size() >= position) {
                title = ((ShowRoomForMain) data.get(position)).getCommunityName();
                if (title.contains("全部")) title = "";
            }
            communityChange(activity, title);
        }
    },
    Delete(3, "删除小区") {
        @Override
        public void run(RentalMainActivity activity, List data, int position) {
            Log.i(activity.getPackageName(), "删除小区");
            androidx.appcompat.app.AlertDialog.Builder d2 = new androidx.appcompat.app.AlertDialog.Builder(activity);
            d2.setTitle("警告:");
            d2.setMessage("确定要删除此小区所有房源吗?");
            d2.setCancelable(true);
            d2.setPositiveButton(R.string.ok_cn, (d, w) -> {
                boolean isOK = DbHelper.getInstance().delRoomDes(((ShowRoomForMain)data.get(position)).getCommunityName());
                if (isOK) {
                    showMessage(activity, "删除此小区成功");
                     activity.flushView();
                } else showMessage(activity, "删除此小区失败");
            });
            d2.show();
        }
    },
    ShowRooms(4, "显示房源") {
        @Override
        public void run(RentalMainActivity activity, List data, int position) {
            Intent intent = new Intent(activity, RentalForHouse.class);
            intent.putExtra("title", RentalMainActivity.showRoomForMainList.get(position).getCommunityName());
            activity.startActivity(intent);
            Log.i(activity.getPackageName(), "显示房源");
        }
    };


    private int index;
    private String name;

    /**
     * 小区编辑，新建
     */
    private static void communityChange(RentalMainActivity activity, String communityString) {

        ViewHolder viewHolder = new AddRoomViewHolder(activity, R.layout.add_room_dialog, communityString);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, view) -> {
                    if (R.id.rental_addRoom_OkBtn == view.getId()) {
                        //确定
                        View v = viewHolder.getInflatedView();
                        String comStr = ((AutoCompleteTextView) v.findViewById(R.id.rental_addRoom_communityName)).getText().toString();
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
                                roomDetails.setMeterNumber(meterStr);
                                if (StrUtils.isNotBlank(areaStr))
                                    roomDetails.setRoomArea(Double.parseDouble(areaStr));
                                if (StrUtils.isNotBlank(proStr))
                                    roomDetails.setPropertyPrice(Double.parseDouble(proStr));
                                boolean isOK = DbHelper.getInstance().saveRoomDes(roomDetails);
                                if (isOK) {
                                    showMessage(activity, "保存房源成功");
                                     activity.flushView();
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
