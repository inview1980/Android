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
import my_manage.password_box.pojo.UserItem;

@AllArgsConstructor(suppressConstructorProperties = true)
public final class UserItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserItem> mData;

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        @SuppressLint("ViewHolder") View convertView= Optional.ofNullable(view).orElse(
                LayoutInflater.from(mContext).inflate(R.layout.password_manage_list_item, parent, false)
        );
        TextView item = convertView.findViewById(R.id.pwd_ItemId);
        TextView address = convertView.findViewById(R.id.pwd_AddressId);
        TextView username = convertView.findViewById(R.id.pwd_UserNameId);
        TextView password = convertView.findViewById(R.id.pwd_PasswordId);
        TextView remark = convertView.findViewById(R.id.pwd_RemarkId);
        item.setText(mData.get(position).getItemName());
        address.setText(mData.get(position).getAddress());
        username.setText(mData.get(position).getUserName());
        password.setText(mData.get(position).getPassword());
        remark.setText(mData.get(position).getRemark());
        return convertView;
    }
}
