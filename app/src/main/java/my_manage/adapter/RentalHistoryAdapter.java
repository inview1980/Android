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
import my_manage.tool.DateUtils;

public final class RentalHistoryAdapter extends BaseAdapter {
    private List<ShowRoomDetails> lst;
    private TextView              man;
    private TextView              startDate;
    private Context               context;

    public RentalHistoryAdapter(@NonNull Context context, @NonNull List<ShowRoomDetails> objects) {
        this.lst = objects;
        this.context = context;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.rental_history_list_item, parent, false);
            man = convertView.findViewById(R.id.rental_history_item_man);
            startDate = convertView.findViewById(R.id.rental_history_item_startDate);
        }
        ShowRoomDetails room = lst.get(position);
        man.setText(room.getPersonDetails().getName());
        startDate.setText(DateUtils.date2String(room.getRentalRecord().getStartDate(),room.getRentalRecord().getPayMonth()));
        return convertView;
    }

}
