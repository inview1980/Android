package my_manage.adapter_expandable_List;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.listener.PersonExtendableListViewAdapterListener;
import my_manage.rent_manage.pojo.PersonDetails;

public final class PersonExtendableListViewAdapter<T extends Activity & IShowList> extends BaseExpandableListAdapter {
    private List<PersonDetails>                     showRoomDetailsList;
    private T                                       activity;
    private TextView                                txt;
    private PersonExtendableListViewAdapterListener listener;

    public PersonExtendableListViewAdapter(T activity, List<PersonDetails> showRoomDetailsList) {
        this.activity = activity;
        this.showRoomDetailsList = showRoomDetailsList;
    }


    @Override
    public int getGroupCount() {
        return showRoomDetailsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return showRoomDetailsList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            txt = convertView.findViewById(android.R.id.text1);
        }
        txt.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        txt.setBackgroundColor(activity.getColor(android.R.color.white));
        txt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        txt.setText(showRoomDetailsList.get(groupPosition).getCompany() + "-" + showRoomDetailsList.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.rental_person_expandable_listview_item, parent, false);
            listener = new PersonExtendableListViewAdapterListener();
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.rentalPersonExpandableDetails.setOnClickListener(v -> listener.onClick(activity, showRoomDetailsList, groupPosition, v));
        vh.rentalPersonExpandableDelete.setOnClickListener(v -> listener.onClick(activity, showRoomDetailsList, groupPosition, v));
        vh.addPerson.setOnClickListener(v -> listener.onClick(activity, showRoomDetailsList, groupPosition, v));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ViewHolder {
        @BindView(R.id.rental_person_expandable_details)   TextView rentalPersonExpandableDetails;
        @BindView(R.id.rental_person_expandable_delete)    TextView rentalPersonExpandableDelete;
        @BindView(R.id.rental_person_expandable_addperson) TextView addPerson;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
