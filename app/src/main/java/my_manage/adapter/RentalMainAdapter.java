package my_manage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomForMain;

@AllArgsConstructor(suppressConstructorProperties = true)
public final class RentalMainAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShowRoomForMain> mData;

    @Override
    public int getCount() {
        return mData.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View convertView= Optional.ofNullable(view).orElse(
                LayoutInflater.from(mContext).inflate(R.layout.rental_main_listview_style, viewGroup, false)
        );
        TextView title = convertView.findViewById(R.id.rental_main_CommunityName);
        TextView areas = convertView.findViewById(R.id.rental_main_item_areaTotal);
        TextView nums = convertView.findViewById(R.id.rental_main_item_roomTotal);
        title.setText(mData.get(position).getCommunityName());
        areas.setText(String.valueOf(mData.get(position).getRoomAreas()));
        nums.setText(String.valueOf(mData.get(position).getRoomCount()));
        return convertView;
    }
}
