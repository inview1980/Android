package my_manage.adapter_expandable_List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.listener.RentRoomExpandableListViewListener;
import my_manage.rent_manage.listener.RoomListener;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

import static my_manage.rent_manage.listener.RentRoomExpandableListViewListener.*;

public final class RoomDetailsExtendableListViewAdapter<T extends Activity & IShowList> extends BaseExpandableListAdapter {
    private List<ShowRoomDetails> room;
    private T                     activity;
    private ViewHolderFor1Floor   vh = null;
    private String                title;

    public RoomDetailsExtendableListViewAdapter(T activity, List<ShowRoomDetails> room, String title) {
        this.activity = activity;
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
     * @param i           组位置
     * @param isExpanded  该组是展开状态还是伸缩状态
     * @param convertView 重用已有的视图对象
     * @param parent      返回的视图对象始终依附于的视图组
     */
    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.rental_room_list_item, parent, false);
            vh = new ViewHolderFor1Floor(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolderFor1Floor) convertView.getTag();
        }
        vh.houseNumber.setText(room.get(i).getRoomDetails().getRoomNumber());
        vh.area.setText(String.valueOf(room.get(i).getRoomDetails().getRoomArea()));
        if (room.get(i).getRentalRecord().getPrimary_id() != 0) {//租房记录不为空
            vh.monthlyRent.setText(String.valueOf(room.get(i).getRentalRecord().getMonthlyRent()));
            //未填写出租的开始日期
            if (room.get(i).getRentalEndDate() != null) {
                Calendar date = room.get(i).getRentalEndDate();
                vh.endDate.setText(date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH));
                if (date.before(Calendar.getInstance())) {
                    //租期已超过,改背景颜色
                    vh.tableLayout.setBackgroundColor(activity.getColor(R.color.red1));
                } else {
                    vh.tableLayout.setBackgroundColor(activity.getColor(android.R.color.white));
                }
            } else {
                //未填写房租的开始时间
                vh.tableLayout.setBackgroundColor(activity.getColor(R.color.grayBlue));
            }
        } else {
            //未出租,改背景颜色
            vh.tableLayout.setBackgroundColor(activity.getColor(R.color.banana));
        }

        if (room.get(i).getPersonDetails() != null)//用户信息不为空
            vh.manName.setText(room.get(i).getPersonDetails().getName());

        if (title == null) {//显示“全部房间”时
            vh.communityName_txt.setVisibility(View.VISIBLE);
            vh.communityName_txt.setText(room.get(i).getRoomDetails().getCommunityName());
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
        if (title != null && title.contains("删除")) {
            //恢复删除的房源
            convertView = LayoutInflater.from(activity).inflate(R.layout.rental_delroom_item,
                    parent, false);
            convertView.findViewById(R.id.recover).setOnClickListener(v ->
                    RoomListener.recoverDel(this.activity, room.get(groupPosition)));
            convertView.findViewById(R.id.delete).setOnClickListener(v ->
                    RoomListener.deleteRoom(this.activity, room.get(groupPosition)));

            return convertView;
        }
        if (room.get(groupPosition).getRentalRecord().getPrimary_id() != 0) {
            //已出租
            convertView = LayoutInflater.from(activity).inflate(R.layout.rent_room_expandable_listview_borrowed_view,
                    parent, false);

            //自动载入各按键
            ViewHolderForBorrowed borrowed = new ViewHolderForBorrowed(convertView);
            //注册各按键的点击事件
            myOnClick(RentRoomExpandableListViewListener::onBorrowedClick, borrowed, groupPosition);
        } else {
            //未出租
            convertView = LayoutInflater.from(activity).inflate(R.layout.rent_room_expandable_listview_null_view,
                    parent, false);
            //自动载入各按键
            ViewHolderForNull aNull = new ViewHolderForNull(convertView);
            //注册各按键的点击事件
            myOnClick(RentRoomExpandableListViewListener::onNullClick, aNull, groupPosition);
        }
        return convertView;
    }

    private void myOnClick(myOnClick myOnClick, Object obj, int position) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == TextView.class) {
                try {
                    ((TextView) field.get(obj)).setOnClickListener(v ->
                            myOnClick.onClick(this.activity, room, position, v));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ViewHolderFor1Floor {
        @BindView(R.id.rental_house_communityName_txt) TextView    communityName_txt;
        @BindView(R.id.rental_house_houseNumber_txt)   TextView    houseNumber;
        @BindView(R.id.rental_person_list_item_tel)    TextView    area;
        @BindView(R.id.rental_person_list_item_name)   TextView    manName;
        @BindView(R.id.rental_house_monthlyRent_txt)   TextView    monthlyRent;
        @BindView(R.id.rental_house_endDate_txt)       TextView    endDate;
        @BindView(R.id.rental_house_tableLayoutId)     TableLayout tableLayout;

        ViewHolderFor1Floor(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //已租出
    class ViewHolderForBorrowed {
        @BindView(R.id.details)           TextView Details;
        @BindView(R.id.continue_rent)     TextView Continue;
        @BindView(R.id.leaseback)         TextView Leaseback;
        @BindView(R.id.changed_rent)      TextView ChangedRent;
        @BindView(R.id.changed_deposit)   TextView ChangedDeposit;
        @BindView(R.id.history)           TextView History;
        @BindView(R.id.pay_propertycosts) TextView PayPropertycosts;
        @BindView(R.id.renew_contract)    TextView RenewContract;
        @BindView(R.id.man_info)          TextView ManInfo;
        @BindView(R.id.del_room)          TextView DelRoom;

        ViewHolderForBorrowed(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderForNull {
        @BindView(R.id.rent_room_expandable_listview_null_details)   TextView Details;
        @BindView(R.id.rent_room_expandable_listview_null_rent)      TextView Continue;
        @BindView(R.id.rent_room_expandable_listview_null_leaseback) TextView Leaseback;
        @BindView(R.id.rent_room_expandable_listview_null_history)   TextView History;
        @BindView(R.id.rent_room_expandable_listview_null_del_room)  TextView del;

        ViewHolderForNull(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
