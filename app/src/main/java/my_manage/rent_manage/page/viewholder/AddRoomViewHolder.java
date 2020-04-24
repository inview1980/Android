package my_manage.rent_manage.page.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;
import java.util.stream.Collectors;

import my_manage.password_box.R;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.tool.PageUtils;

public final class AddRoomViewHolder extends ViewHolder implements View.OnFocusChangeListener {
    private Activity activity;
    private AutoCompleteTextView communityName;
    private String communityString;
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
        List<String> roomDesList = RentDB.getQueryAll(RoomDetails.class).stream()
                .map(RoomDetails::getCommunityName).distinct()
                .collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, roomDesList);
        communityName.setAdapter(adapter);
        communityName.setText(communityString);

        //设置用户名下拉数据，其中第一项为新建
//        List<PersonDetails> manList = RentDB.getQueryAll(PersonDetails.class);
//        manList.add(new PersonDetails("新增租户"));
//        ManAdapter manAdapter = new ManAdapter(activity, android.R.layout.simple_list_item_1, manList);
//        man.setAdapter(manAdapter);
//        man.setSelection(0);

        return view;
    }

    private void init(View view) {
        communityName = view.findViewById(R.id.rental_addRoom_communityName);
        houseNumber = view.findViewById(R.id.rental_addRoom_houseNumber);
        area = view.findViewById(R.id.rental_addRoom_area);
        meterNumber = view.findViewById(R.id.rental_addRoom_meterNumber);
        propertyPrice = view.findViewById(R.id.rental_addRoom_propertyPrice);

        communityName.setOnFocusChangeListener(this);
        houseNumber.setOnFocusChangeListener(this);
        area.setOnFocusChangeListener(this);
        meterNumber.setOnFocusChangeListener(this);
        propertyPrice.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        PageUtils.closeInput(activity,b);
    }

}
