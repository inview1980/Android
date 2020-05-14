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
import java.util.Optional;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
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


    public static <T extends Activity & IShowList> void onBorrowedClick(T activity, List<ShowRoomDetails> data, int position, View v) {
        switch (v.getId()) {
            case R.id.rent_room_expandable_listview_borrowed_details:
                //查看详情
                showDetails(activity, data, position);
                break;
            case R.id.rent_room_expandable_listview_borrowed_changed_rent:
                //调整租金
                changedRent(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_continue_rent:
                continueRent(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_leaseback:
                //退租
                notRent(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_changed_deposit:
                //调整押金
                changeDeposit(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_history:
                //查看出租记录
                rentalHistory(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_pay_propertycosts:
                continueProperty(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_renew_contract:
                continueContract(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_borrowed_man_info:
                //显示租户的详情
                PersonExtendableListViewAdapterListener.showPersonDetails(activity, data.get(position).getRentalRecord().getManID());
                break;
            case R.id.rent_room_expandable_listview_borrowed_del_room:
                //删除房源
                delRoom(activity, data.get(position));
                break;
            default:
                break;
        }
    }

    private static <T extends Activity & IShowList> void continueContract(T activity, ShowRoomDetails showRoomDetails) {
        Intent intent = new Intent(activity, ContinueContractActivity.class);
        intent.putExtra(showRoomDetails.getClass().getSimpleName(), JSON.toJSONString(showRoomDetails));
        activity.startActivity(intent);
    }

    private static <T extends Activity & IShowList> void continueProperty(T activity, ShowRoomDetails showRoomDetails) {
        Intent intent = new Intent(activity, ContinuePropertyActivity.class);
        intent.putExtra(showRoomDetails.getClass().getSimpleName(), JSON.toJSONString(showRoomDetails));
        activity.startActivity(intent);
    }

    /**
     * 续租
     */
    private static <T extends Activity & IShowList> void continueRent(T activity, ShowRoomDetails showRoomDetails) {
        Intent intent = new Intent(activity, ContinueRentActivity.class);
        intent.putExtra(showRoomDetails.getClass().getSimpleName(), JSON.toJSONString(showRoomDetails));
        activity.startActivity(intent);
    }

    private static <T extends Activity & IShowList> void notRent(T activity, ShowRoomDetails showRoomDetails) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("退租").setMessage("请确定租金已付完、押金已退、水电费交清");
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            RoomDetails room = RentDB.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
            if (room != null) {
                room.setRecordId(0);
//                        room.setManId(0);
                if (RentDB.update(room) > 0) {
                    Toast.makeText(activity, "退租成功！", Toast.LENGTH_SHORT).show();
                    activity.showList();
                }
            }
        }).show();
    }

    public static <T extends Activity & IShowList> void onNullClick(T activity, List<ShowRoomDetails> data, int position, View v) {
        switch (v.getId()) {
            case R.id.rent_room_expandable_listview_null_details:
                showDetails(activity, data, position);
                break;
            case R.id.rent_room_expandable_listview_null_history:
                rentalHistory(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_null_rent:
                rentRoom(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_null_leaseback:
                leaseBack(activity, data.get(position));
                break;
            case R.id.rent_room_expandable_listview_null_del_room:
                delRoom(activity,data.get(position));
                break;
            default:
                break;
        }
    }

    /**
     * 撤销退租
     */
    private static <T extends Activity & IShowList> void leaseBack(T activity, ShowRoomDetails showRoomDetails) {
        List<RentalRecord> recordList = DbHelper.getInstance().getRecords();
        Optional<RentalRecord> rentalRecord = recordList.stream().filter(rr -> rr.getRoomNumber().equals(showRoomDetails.getRoomDetails().getRoomNumber()))
                .max((t1, t2) -> Integer.compare(t1.getPrimary_id(), t2.getPrimary_id()));

        if (rentalRecord.isPresent()) {
            RoomDetails room = RentDB.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
            if (room != null) {
                room.setRecordId(rentalRecord.get().getPrimary_id());
//                room.setManId(rentalRecord.get().getManID());
                if (RentDB.update(room) > 0) {
                    Toast.makeText(activity, "撤销退租成功！", Toast.LENGTH_SHORT).show();
                    activity.showList();
                }
            }
        }
    }

    /**
     * 出租窗口
     */
    private static void rentRoom(Activity activity, ShowRoomDetails room) {
        Intent                intent  = new Intent(activity, RoomDetailsByToolbarActivity.class);
        Bundle                bundle  = new Bundle();
        List<ShowRoomDetails> details = new ArrayList<ShowRoomDetails>() {{add(room);}};
        bundle.putString("ShowRoomDetails", JSONArray.toJSONString(details));
        bundle.putInt("ShowRoomType",ShowRoomType.Rent.getIndex());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 调整押金
     */
    private static <T extends Activity & IShowList> void changeDeposit(T activity, ShowRoomDetails sr) {
        if (sr == null) return;
        DialogPlusBuilder db = showDialog(activity, sr.getRentalRecord().getDeposit() + "", "押金");
        db.setOnClickListener((dialog1, view) -> {
            try {
                if (view.getId() == R.id.rental_changeRent_okBtn) {
                    EditText text = db.getHolder().getInflatedView().findViewById(R.id.rental_changeRent_newNum);
                    if (StrUtils.isNotBlank(text.getText().toString())) {
                        sr.getRentalRecord().setDeposit(Integer.parseInt(text.getText().toString()));
                        if (RentDB.update(sr.getRentalRecord()) > 0) {
                            //成功，刷新
                            activity.showList();
                            Toast.makeText(activity, "调整押金成功", Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }).create().show();
    }

    /**
     * 删除房源
     */
    private static <T extends Activity & IShowList> void delRoom(T activity, ShowRoomDetails showRoomDetails) {
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
    private static <T extends Activity & IShowList> void rentalHistory(T activity, ShowRoomDetails showRoomDetails) {
        Intent intent = new Intent(activity, RoomHistoryActivity.class);
        if (showRoomDetails != null) {
            intent.putExtra("roomNumber", showRoomDetails.getRoomDetails().getRoomNumber());
            intent.putExtra("communityName", showRoomDetails.getRoomDetails().getCommunityName());
            intent.putExtra("area", "" + showRoomDetails.getRoomDetails().getRoomArea());
        }
        activity.startActivity(intent);
    }

    /**
     * 调整租金
     */
    private static <T extends Activity & IShowList> void changedRent(T activity, ShowRoomDetails sr) {
        if (sr == null) return;
        DialogPlusBuilder db = showDialog(activity, sr.getRentalRecord().getMonthlyRent() + "", "租金");
        db.setOnClickListener((dialog1, view) -> {
            try {
                if (view.getId() == R.id.rental_changeRent_okBtn) {
                    EditText text = db.getHolder().getInflatedView().findViewById(R.id.rental_changeRent_newNum);
                    if (StrUtils.isNotBlank(text.getText().toString())) {
                        sr.getRentalRecord().setMonthlyRent(Double.parseDouble(text.getText().toString()));
                        if (sr.getRentalRecord().getPrimary_id() != 0 && RentDB.update(sr.getRentalRecord()) > 0) {
                            //成功，刷新
                            activity.showList();
                            Toast.makeText(activity, "调整租金成功", Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
e.printStackTrace();
            }
        }).create().show();
    }

    private static <T extends Activity & IShowList> DialogPlusBuilder showDialog(T activity, String showTxt, String title) {
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
                //输入框失去焦点则关闭
                newNum.setOnFocusChangeListener((v, b) -> PageUtils.closeInput(activity, b));
                return view;
            }
        };
        DialogPlusBuilder dialog = DialogPlus.newDialog(activity)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder);
        return dialog;
    }

    /**
     * 查看详情
     */
    private static void showDetails(Activity activity, List<ShowRoomDetails> data, int position) {
//        Intent intent = new Intent(activity, RoomDetailsActivity.class);
        Intent intent = new Intent(activity, RoomDetailsByToolbarActivity.class);
        Bundle bundle = new Bundle();
        if (position != -1) {
            bundle.putString("ShowRoomDetails", JSONArray.toJSONString(data));
        }
        bundle.putInt("currentItem", position);
        bundle.putInt("ShowRoomType",ShowRoomType.Details.getIndex());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

}
