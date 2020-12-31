package my_manage.ui.rent_manage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.deadline.statebutton.StateButton;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.RoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/8 15:17
 * @Description :
 */
public class DialogFragmentInsertRoom<T extends Activity & IShowList> extends MyDialogFragment {
    @BindView(R.id.toolbar)                      Toolbar     toolbar;
    @BindView(R.id.rental_addRoom_communityName) Spinner     rentalAddRoomCommunityName;
    @BindView(R.id.rental_addRoom_houseNumber)   EditText    rentalAddRoomHouseNumber;
    @BindView(R.id.rental_addRoom_area)          EditText    rentalAddRoomArea;
    @BindView(R.id.rental_addRoom_meterNumber)   EditText    rentalAddRoomMeterNumber;
    @BindView(R.id.waterMeter)                   EditText    waterMeter;
    @BindView(R.id.rental_addRoom_propertyPrice) EditText    rentalAddRoomPropertyPrice;
    @BindView(R.id.rental_addRoom_OkBtn)         StateButton okBtn;
    private                                      String      communityString;
    private                                      T           activity;

    public DialogFragmentInsertRoom(T activity, String communityString) {
        this.activity = activity;
        this.communityString = communityString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_room_dialog, container, false);
        bind = ButterKnife.bind(this, v);
        init();
        return v;
    }

    private void init() {
        //设置社区名下拉数据
        if (StrUtils.isBlank(this.communityString) || this.communityString.contains("全部")) {
            rentalAddRoomCommunityName.setSelection(0);
        } else {
            String[] names    = activity.getResources().getStringArray(R.array.communityName);
            int      position =Arrays.asList(names).indexOf(communityString);
            position = (position == -1) ? 0 : position;
            rentalAddRoomCommunityName.setSelection(position);
        }

        toolbar.setTitle("增加房源");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        okBtn.setEnabled(false);
    }

    @OnTextChanged({R.id.rental_addRoom_houseNumber, R.id.rental_addRoom_area, R.id.rental_addRoom_meterNumber
                           , R.id.waterMeter, R.id.rental_addRoom_propertyPrice})
    void onTextChanged() {
        okBtn.setEnabled(true);
    }

    @OnItemSelected(R.id.rental_addRoom_communityName)
    void onItemSelected() {
        okBtn.setEnabled(true);
    }

    @OnClick(R.id.rental_addRoom_OkBtn)
    void onClick() {
        if (StrUtils.isAnyBlank(rentalAddRoomCommunityName.getSelectedItem().toString(),
                rentalAddRoomHouseNumber.getText().toString())) {
            PageUtils.showMessage(activity, "小区名或房号不能为空！");
            return;
        }
        try {
            RoomDetails roomDetails = new RoomDetails();
            roomDetails.setCommunityName(rentalAddRoomCommunityName.getSelectedItem().toString());
            roomDetails.setRoomNumber(rentalAddRoomHouseNumber.getText().toString());
            roomDetails.setElectricMeter(rentalAddRoomMeterNumber.getText().toString());
            roomDetails.setWaterMeter(waterMeter.getText().toString());
            //如果面积为空，填0
            String areaStr = StrUtils.isNotBlank(rentalAddRoomArea.getText().toString()) ? rentalAddRoomArea.getText().toString() : "0";
            roomDetails.setRoomArea(Double.parseDouble(areaStr));
            //如果单价为空，填0
            String priceStr = StrUtils.isNotBlank(rentalAddRoomPropertyPrice.getText().toString()) ? rentalAddRoomPropertyPrice.getText().toString() : "0";
            roomDetails.setPropertyPrice(Double.parseDouble(priceStr));
            boolean isOK = DbHelper.getInstance().saveRoomDes(roomDetails);
            if (isOK) {
                PageUtils.showMessage(activity, "保存房源成功");
                activity.showList();
                dismiss();
            } else {
                PageUtils.showMessage(activity, "保存失败");
            }
        } catch (Exception e) {
            PageUtils.Error("保存失败");
            dismiss();
        }
    }
}
