package my_manage.ui.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.rent_manage.page.RoomDetailsByToolbarActivity;
import my_manage.ui.rent_manage.page.RoomHistoryActivity;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.RoomDetails;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.enums.ShowRoomType;

public class RentRoomExpandableListViewListener {

    public static <T extends Activity & IShowList> void notRent(T activity, ShowRoomDetails showRoomDetails) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("退租").setMessage("请确定租金已付完、押金已退、水电费交清");
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            RoomDetails room = DbBase.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
            if (room != null) {
                room.setRecordId(0);
                if (DbBase.update(room) > 0) {
                    Toast.makeText(activity, "退租成功！", Toast.LENGTH_SHORT).show();
                    activity.showList();
                }
            }
        }).show();
    }

    /**
     * 撤销退租
     */
    public static <T extends Activity & IShowList> void leaseBack(T activity, ShowRoomDetails showRoomDetails) {
        List<RentalRecord> recordList = DbHelper.getInstance().getRecords();
        Optional<RentalRecord> rentalRecord = recordList.stream().filter(rr -> rr.getRoomNumber().equals(showRoomDetails.getRoomDetails().getRoomNumber()))
                .max((t1, t2) -> Integer.compare(t1.getPrimary_id(), t2.getPrimary_id()));

        if (rentalRecord.isPresent()) {
            RoomDetails room = DbBase.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
            if (room != null) {
                room.setRecordId(rentalRecord.get().getPrimary_id());
                if (DbBase.update(room) > 0) {
                    Toast.makeText(activity, "撤销退租成功！", Toast.LENGTH_SHORT).show();
                    activity.showList();
                }
            }
        }
    }

    /**
     * 出租窗口
     */
    public static void rentRoom(Activity activity, ShowRoomDetails room) {
        Intent                intent  = new Intent(activity, RoomDetailsByToolbarActivity.class);
        Bundle                bundle  = new Bundle();
        List<ShowRoomDetails> details = new ArrayList<ShowRoomDetails>() {{add(room);}};
        bundle.putString("ShowRoomDetails", JSONArray.toJSONString(details));
        bundle.putInt("ShowRoomType", ShowRoomType.Rent.getIndex());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 删除房源
     */
    public static <T extends Activity & IShowList> void delRoom(T activity, ShowRoomDetails showRoomDetails) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("删除房源").setMessage("是否要删除房源？");
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
            if (showRoomDetails != null) {
                if (DbHelper.getInstance().delRoomDes(showRoomDetails)) {
                    Toast.makeText(activity, "删除房源成功", Toast.LENGTH_SHORT).show();
                    activity.showList();
                } else {
                    Toast.makeText(activity, "删除房源失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    /**
     * 查看出租记录
     */
    public static <T extends Activity & IShowList> void rentalHistory(T activity, ShowRoomDetails showRoomDetails) {
        Intent intent = new Intent(activity, RoomHistoryActivity.class);
        if (showRoomDetails != null) {
            intent.putExtra("roomNumber", showRoomDetails.getRoomDetails().getRoomNumber());
            intent.putExtra("communityName", showRoomDetails.getRoomDetails().getCommunityName());
            intent.putExtra("area", "" + showRoomDetails.getRoomDetails().getRoomArea());
        }
        activity.startActivity(intent);
    }


    /**
     * 查看详情
     */
    public static void showDetails(Activity activity, List<ShowRoomDetails> data, int position) {
        Intent intent = new Intent(activity, RoomDetailsByToolbarActivity.class);
        Bundle bundle = new Bundle();
        if (position != -1) {
            bundle.putString("ShowRoomDetails", JSONArray.toJSONString(data));
        }
        bundle.putInt("currentItem", position);
        bundle.putInt("ShowRoomType", ShowRoomType.Details.getIndex());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

}
