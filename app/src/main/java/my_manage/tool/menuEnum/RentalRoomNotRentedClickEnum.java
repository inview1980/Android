package my_manage.tool.menuEnum;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONArray;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.iface.IActivityMenuForData;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.page.RentalForHouseActivity;
import my_manage.rent_manage.page.RoomDetailsActivity;
import my_manage.rent_manage.page.RoomHistoryActivity;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

@Getter
@AllArgsConstructor
public enum RentalRoomNotRentedClickEnum implements IActivityMenuForData<RentalForHouseActivity> {
    Show(1, "查看详情") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            Intent intent = new Intent(activity, RoomDetailsActivity.class);
            Bundle bundle = new Bundle();
            if (position != -1) {
                bundle.putString("ShowRoomDetails", JSONArray.toJSONString(data));
            }
            bundle.putInt("currentItem", position);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }
    },
    Renting(2, "出租") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            // TODO: 2020/4/26
        }
    },
    LeaseOff(3, "撒消退租") {
        @Override
        public void run(RentalForHouseActivity activity, List data, int position) {
// TODO: 2020/4/27  
        }
    },
    ChangeRent(4, "调整租金") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            ShowRoomDetails sr = data.get(position);
            if (sr == null) return;

            ViewHolder viewHolder = new ViewHolder(R.layout.rental_change_rentmoney) {
                @Override
                public View getView(LayoutInflater inflater, ViewGroup parent) {
                    View view = super.getView(inflater, parent);
                    TextView oldNum = view.findViewById(R.id.rental_changeRent_oldNum);
                    EditText newNum = view.findViewById(R.id.rental_changeRent_newNum);
                    oldNum.setText("" + sr.getRentalMoney());
                    newNum.setText("" + sr.getRentalMoney());
                    //输入框失去焦点则关闭
                    newNum.setOnFocusChangeListener((v, b) -> PageUtils.closeInput(activity, b));
                    return view;
                }
            };
            DialogPlus dialog = DialogPlus.newDialog(activity)
                    .setOnClickListener((dialog1, view) -> {
                        try {
                            if (view.getId() == R.id.rental_changeRent_okBtn) {
                                EditText text = viewHolder.getInflatedView().findViewById(R.id.rental_changeRent_newNum);
                                if (StrUtils.isNotBlank(text.getText().toString())) {
                                    RoomDetails roomDetails = RentDB.getInfoById(sr.getRoomNumber(), RoomDetails.class);
                                    roomDetails.setRentalMoney(Integer.parseInt(text.getText().toString()));
                                    if (RentDB.update(roomDetails) > 0) {
                                        //成功，刷新
                                        activity.showList();
                                        dialog1.dismiss();
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                    })
                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                    .setContentHolder(viewHolder)
                    .create();
            dialog.show();
        }
    },
    RentRecords(5, "查看出租记录") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
// TODO: 2020/4/26
            Intent intent = new Intent(activity, RoomHistoryActivity.class);
            intent.putExtra("roomNumber", data.get(position).getRoomNumber());
            intent.putExtra("communityName", data.get(position).getCommunityName());
            intent.putExtra("area",""+data.get(position).getRoomArea());
            activity.startActivity(intent);
        }
    },
    Del(6, "删除房源") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("删除房源").setMessage("是否要删除房源？");
            dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
                if (data != null && position >= 0 && data.size() > position) {
                    if (DbHelper.getInstance().delRoomDes(data.get(position))) {
                        Toast.makeText(activity, "删除房源成功", Toast.LENGTH_SHORT).show();
                        activity.showList();
                    } else {
                        Toast.makeText(activity, "删除房源失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();
        }
    };

    private int index;
    private String name;
}
