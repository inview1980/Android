package my_manage.adapter_expandable_List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RoomDetailsExtendableListViewAdapter extends BaseExpandableListAdapter {
    private List<ShowRoomDetails> room;
    private Context               mContext;
    private ViewHolder            vh = null;
    private String                title;
    private TextView text1;

    public RoomDetailsExtendableListViewAdapter(Context mContext, List<ShowRoomDetails> room, String title) {
        this.mContext = mContext;
        this.room = room;
        this.title = title;
    }

    // 获取分组的个数
    @Override
    public int getGroupCount() {
        return room.size();
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return room.get(groupPosition);
    }

    //获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 1;
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 获取显示指定分组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded    该组是展开状态还是伸缩状态
     * @param convertView   重用已有的视图对象
     * @param parent        返回的视图对象始终依附于的视图组
     */
    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rental_room_list_item, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.houseNumber.setText(room.get(i).getRoomNumber());
        vh.area.setText(String.valueOf(room.get(i).getRoomArea()));
        vh.monthlyRent.setText(String.valueOf(room.get(i).getRentalMoney()));
        vh.propertyPrice.setText(String.valueOf(room.get(i).getPropertyPrice()));
        if (room.get(i).getRentalRecord() != null) {//租房记录不为空
            vh.isContainRealty.setText(room.get(i).getRentalRecord().getIsContainRealty() ? "是" : "否");
            vh.realtyMoney.setText(String.valueOf(room.get(i).getRentalRecord().getRealtyMoney()));
        } else {
            //未出租,改外框颜色
            vh.tableLayout.setBackground(mContext.getDrawable(R.drawable.blue_borde_2));
        }
        if (room.get(i).getPersonDetails() != null)//用户信息不为空
            vh.manName.setText(room.get(i).getPersonDetails().getName());
        if (room.get(i).getRentalEndDate() != null) {
            Calendar date = room.get(i).getRentalEndDate();
            vh.endDate.setText(date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH));
            if (date.before(Calendar.getInstance())) {
                vh.tableLayout.setBackground(mContext.getDrawable(R.drawable.red_borde_3));
            }
        }
        if (title == null) {//显示“全部房间”时
            vh.communityName_txt.setVisibility(View.VISIBLE);
            vh.communityName_txt.setText(room.get(i).getCommunityName());
        }
        return convertView;
    }

    /**
     * 取得显示给定分组给定子位置的数据用的视图
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild   子元素是否处于组中的最后一个
     * @param convertView   重用已有的视图(View)对象
     * @param parent        返回的视图(View)对象始终依附于的视图组
     * @return
     * @see ExpandableListAdapter#getChildView(int, int, boolean, View,
     * ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
             text1=convertView.findViewById(android.R.id.text1);
        }
        text1.setText(room.get(groupPosition).getRoomNumber());
        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ViewHolder {
        @BindView(R.id.rental_house_communityName_txt)   TextView    communityName_txt;
        @BindView(R.id.rental_house_houseNumber_txt)     TextView    houseNumber;
        @BindView(R.id.rental_house_area_txt)            TextView    area;
        @BindView(R.id.rental_house_man_txt)             TextView    manName;
        @BindView(R.id.rental_house_isContainRealty_txt) TextView    isContainRealty;
        @BindView(R.id.rental_house_monthlyRent_txt)     TextView    monthlyRent;
        @BindView(R.id.rental_house_realtyMoney_txt)     TextView    realtyMoney;
        @BindView(R.id.rental_house_endDate_txt)         TextView    endDate;
        @BindView(R.id.rental_house_propertyPrice_txt)   TextView    propertyPrice;
        @BindView(R.id.rental_house_tableLayoutId)       TableLayout tableLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
