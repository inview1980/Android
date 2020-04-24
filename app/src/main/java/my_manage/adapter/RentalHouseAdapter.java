package my_manage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RentalHouseAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShowRoomDetails> room;

    private TextView houseNumber;
    private TextView area;
    private TextView monthlyRent;
    private TextView isContainRealty;
    private TextView manName;
    private TextView realtyMonty;
    private TextView endDate;
    private TableLayout tableLayout;
    private String title;
    private TextView communityName_lable;
    private TextView communityName_txt;
    private TextView propertyPrice;

    public RentalHouseAdapter(Context mContext, List<ShowRoomDetails> room, String title) {
        this.mContext = mContext;
        this.room = room;
        this.title = title;
    }

    @Override
    public int getCount() {
        return room.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rental_house_listview_style, viewGroup, false);
            houseNumber = view.findViewById(R.id.rental_house_houseNumber_txt);
            area = view.findViewById(R.id.rental_house_area_txt);
            monthlyRent = view.findViewById(R.id.rental_house_monthlyRent_txt);
            isContainRealty = view.findViewById(R.id.rental_house_isContainRealty_txt);
            manName = view.findViewById(R.id.rental_house_man_txt);
            realtyMonty = view.findViewById(R.id.rental_house_realtyMoney_txt);
            endDate = view.findViewById(R.id.rental_house_endDate_txt);
            tableLayout = view.findViewById(R.id.rental_house_tableLayoutId);
            communityName_lable = view.findViewById(R.id.rental_house_communityName_lable);
            communityName_txt = view.findViewById(R.id.rental_house_communityName_txt);
            propertyPrice = view.findViewById(R.id.rental_house_propertyPrice_txt);
        }
        houseNumber.setText(room.get(i).getRoomNumber());
        area.setText(String.valueOf(room.get(i).getRoomArea()));
        monthlyRent.setText(String.valueOf(room.get(i).getRentalMoney()));
        propertyPrice.setText(String.valueOf(room.get(i).getPropertyPrice()));
        if (room.get(i).getRentalRecord() != null) {//租房记录不为空
            isContainRealty.setText(room.get(i).getRentalRecord().getIsContainRealty() ? "是" : "否");
            realtyMonty.setText(String.valueOf(room.get(i).getRentalRecord().getRealtyMoney()));
        }else {
            //未出租
            tableLayout.setBackgroundColor(view.getResources().getColor(android.R.color.holo_blue_light));
        }
        if (room.get(i).getPersonDetails() != null)//用户信息不为空
            manName.setText(room.get(i).getPersonDetails().getName());
        if (room.get(i).getEndDate() != null) {
            Calendar date = room.get(i).getEndDate();
            endDate.setText(date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH));
            if (date.before(Calendar.getInstance())) {
                tableLayout.setBackgroundColor(view.getResources().getColor(R.color.red));
            }
        }
        if (title == null) {//显示全部时
            communityName_txt.setVisibility(View.VISIBLE);
            communityName_lable.setVisibility(View.VISIBLE);
            communityName_txt.setText(room.get(i).getCommunityName());
        }
        return view;
    }
}
