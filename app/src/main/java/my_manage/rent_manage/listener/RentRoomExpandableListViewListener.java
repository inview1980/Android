package my_manage.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.rent_manage.page.ContinueContractActivity;
import my_manage.rent_manage.page.ContinuePropertyActivity;
import my_manage.rent_manage.page.ContinueRentActivity;
import my_manage.rent_manage.page.RoomDetailsByToolbarActivity;
import my_manage.rent_manage.page.RoomHistoryActivity;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.enums.ShowRoomType;

public class RentRoomExpandableListViewListener {
    @FunctionalInterface
    public interface ISetValue {
        void setValue(double value);
    }

    /**
     * 续租、续签合同、续物业费
     */
    public static <T extends Activity & IShowList> void continueOperation(T activity, ShowRoomDetails showRoomDetails, Class tClass) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(showRoomDetails.getClass().getSimpleName(), JSON.toJSONString(showRoomDetails));
        activity.startActivity(intent);
    }

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
     * 调出调整押金、月租金的对话框，
     * @param setValueFunc 自定义接口函数式接口ISetValue
     * @param showTxt 原押金、月租金的金额
     * @param title 只为：押金、月租金
     */
    public static <T extends Activity & IShowList> void showDialog(T activity, ISetValue setValueFunc,
                                                                    ShowRoomDetails sr, String showTxt, String title) {
        if (sr == null) return;
        ViewHolder viewHolder = new ViewHolder(R.layout.rental_change_dialog) {
            @Override
            public View getView(LayoutInflater inflater, ViewGroup parent) {
                View     view   = super.getView(inflater, parent);
                TextView oldNum = view.findViewById(R.id.rental_changeRent_oldNum);
                EditText newNum = view.findViewById(R.id.rental_changeRent_newNum);
                ((TextView) view.findViewById(R.id.rental_change_dialog_title)).setText("调整" + title);
                ((TextView) view.findViewById(R.id.rental_change_dialog_lable1)).setText("原" + title + "为:");
                ((TextView) view.findViewById(R.id.rental_change_dialog_lable2)).setText("调整" + title + "为:");
                oldNum.setText(showTxt);
                newNum.setText(showTxt);
                return view;
            }
        };
        DialogPlusBuilder db = DialogPlus.newDialog(activity).setExpanded(true).setContentHolder(viewHolder);
        db.setOnClickListener((dialog1, view) -> {
            try {
                if (view.getId() == R.id.rental_changeRent_okBtn) {
                    EditText text = db.getHolder().getInflatedView().findViewById(R.id.rental_changeRent_newNum);
                    if (StrUtils.isNotBlank(text.getText().toString())) {
                        setValueFunc.setValue(Double.parseDouble(text.getText().toString()));
                        if (sr.getRentalRecord().getPrimary_id() != 0 && DbBase.update(sr.getRentalRecord()) > 0) {
                            //成功，刷新
                            activity.showList();
                            Toast.makeText(activity, "调整" + title + "成功", Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).create().show();
    }

    /**
     * 查看详情
     */
    public static void showDetails(Activity activity, List<ShowRoomDetails> data, int position) {
//        Intent intent = new Intent(activity, RoomDetailsActivity.class);
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
