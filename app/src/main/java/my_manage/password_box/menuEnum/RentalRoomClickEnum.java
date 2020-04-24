package my_manage.password_box.menuEnum;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.password_box.R;
import my_manage.password_box.iface.IActivityMenuForData;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.page.NewRoom;
import my_manage.rent_manage.page.RentalForHouse;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

@Getter
@AllArgsConstructor
public enum RentalRoomClickEnum implements IActivityMenuForData<RentalForHouse> {
    ChangeRent(1, "调整租金") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {
            ShowRoomDetails sr = (ShowRoomDetails) data.get(position);
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
                    newNum.setOnFocusChangeListener((v,b)-> PageUtils.closeInput(activity,b));
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
    Rent(2, "出租") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {

        }
    },
    RentLost(3, "退租") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {

        }
    },
    Edit(4, "修改") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {
            Intent intent = new Intent(activity, NewRoom.class);
            if (position != -1) {
                ShowRoomDetails sr = (ShowRoomDetails) data.get(position);
                intent.putExtra("ShowRoomDetails", JSON.toJSONString(sr));
            }
            activity.startActivity(intent);
        }
    },
    Del(5, "删除房源") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {

        }
    };

    private int index;
    private String name;
}
