package my_manage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import my_manage.rent_manage.pojo.PersonDetails;

public final class ManAdapter extends ArrayAdapter<PersonDetails> {
    private List<PersonDetails> lst;

    public ManAdapter(@NonNull Context context, int resource, @NonNull List<PersonDetails> objects) {
        super(context, resource, objects);
        this.lst = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getText(position, super.getDropDownView(position, convertView, parent), parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getText(position, super.getView(position, convertView, parent), parent);
    }

    private View getText(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(lst.get(position).getName());
        return convertView;
    }
}
