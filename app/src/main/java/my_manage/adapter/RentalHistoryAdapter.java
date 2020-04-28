package my_manage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RentalHistoryAdapter extends BaseAdapter {
    private List<ShowRoomDetails> lst;
    private TextView man;
    private TextView startDate;
    private TextView endDate;
    private Context context;

    public RentalHistoryAdapter(@NonNull Context context, @NonNull List<ShowRoomDetails> objects) {
        this.lst = objects;
        this.context=context;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int i) {
        return lst.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.rental_history_list_item, parent, false);
            man = convertView.findViewById(R.id.rental_history_item_man);
            startDate = convertView.findViewById(R.id.rental_history_item_startDate);
            endDate = convertView.findViewById(R.id.rental_history_item_endDate);
        }
        ShowRoomDetails room = lst.get(position);
        if (room.getPersonDetails() != null) {
            man.setText(room.getPersonDetails().getName());

        }
        if (room.getRentalRecord() != null) {
            startDate.setText(date2String(room.getRentalRecord().getStartDate()));
            endDate.setText(date2String(room.getRentalEndDate()));
        }
        return convertView;
    }

    private String date2String(Calendar startDate) {
        if (startDate == null) return "";
        return startDate.get(Calendar.YEAR) + "-" + (startDate.get(Calendar.MONTH) + 1) + "-" + startDate.get(Calendar.DAY_OF_MONTH);
    }
}
