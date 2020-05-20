package my_manage.rent_manage.page.viewholder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.orhanobut.dialogplus.ViewHolder;

import java.util.Arrays;

import my_manage.password_box.R;
import my_manage.tool.PageUtils;

public final class AddRoomViewHolder extends ViewHolder implements View.OnFocusChangeListener {
    private Activity activity;
    private Spinner  communityName;
    private String   communityString;
    private EditText meterNumber;
    private EditText propertyPrice;
    private EditText area;
    private EditText houseNumber;

    public AddRoomViewHolder(Activity activity, int viewResourceId, String communityString) {
        super(viewResourceId);
        this.activity = activity;
        this.communityString = communityString;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView(inflater, parent);
        init(view);

        //设置社区名下拉数据
        String[] names    = view.getResources().getStringArray(R.array.communityName);
        int      position = Arrays.binarySearch(names, communityString);
        position = (position == -1) ? 0 : position;
        communityName.setSelection(position);

        return view;
    }

    private void init(View view) {
        communityName = view.findViewById(R.id.rental_addRoom_communityName);
        houseNumber = view.findViewById(R.id.rental_addRoom_houseNumber);
        area = view.findViewById(R.id.rental_addRoom_area);
        meterNumber = view.findViewById(R.id.rental_addRoom_meterNumber);
        propertyPrice = view.findViewById(R.id.rental_addRoom_propertyPrice);

        houseNumber.setOnFocusChangeListener(this);
        area.setOnFocusChangeListener(this);
        meterNumber.setOnFocusChangeListener(this);
        propertyPrice.setOnFocusChangeListener(this);

        PageUtils.setUnderline(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        PageUtils.closeInput(activity, b);
    }

}
