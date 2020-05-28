package my_manage.rent_manage.page.viewholder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.deadline.statebutton.StateButton;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.Getter;
import my_manage.password_box.R;
import my_manage.tool.PageUtils;

@Getter
public final class AddRoomViewHolder extends ViewHolder {
    @BindView(R.id.rental_addRoom_communityName) Spinner  communityName;
    @BindView(R.id.rental_addRoom_houseNumber)   EditText houseNumber;
    @BindView(R.id.rental_addRoom_area)          EditText area;
    @BindView(R.id.rental_addRoom_meterNumber)   EditText meterNumber;
    @BindView(R.id.waterMeter)                   EditText waterMeter;
    @BindView(R.id.rental_addRoom_propertyPrice) EditText propertyPrice;
    private                                      Activity activity;
    private                                      String   communityString;

    public AddRoomViewHolder(Activity activity, String communityString) {
        super(R.layout.add_room_dialog);
        this.activity = activity;
        this.communityString = communityString;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View           view = super.getView(inflater, parent);
        ButterKnife.bind(this, view);
        PageUtils.setUnderline(this);
        //设置社区名下拉数据
        String[] names    = view.getResources().getStringArray(R.array.communityName);
        int      position = Arrays.binarySearch(names, communityString);
        position = (position == -1) ? 0 : position;
        communityName.setSelection(position);

        return view;
    }
}
