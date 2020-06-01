package my_manage.adapter_expandable_List;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.listener.RentRoomExpandableListViewListener;
import my_manage.rent_manage.listener.RoomListener;
import my_manage.rent_manage.pojo.show.MenuData;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.IconView;
import my_manage.tool.database.DbHelper;
import my_manage.tool.enums.MenuTypesEnum;
import my_manage.tool.enums.RentalOnClickEnumHandle;

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
        convertView = LayoutInflater.from(activity).inflate(R.layout.rent_room_expandable_listview_borrowed_view,
                parent, false);
        GridLayout    mainLayout = convertView.findViewById(R.id.mainLayout);
        //默认未出租
        MenuTypesEnum types= MenuTypesEnum.NotRented;
        if (title != null && title.contains("删除")) {
            //删除的房源
            types = MenuTypesEnum.DeletedRooms;
        } else if (room.get(groupPosition).getRentalRecord().getPrimary_id() != 0) {
            //已出租
            types = MenuTypesEnum.IsBorrowed;
        }
        loadLayout(mainLayout,parent, groupPosition, types);
        return convertView;
    }

    private void loadLayout(GridLayout mainLayout,ViewGroup viewGroup, int position, MenuTypesEnum types) {
        val lst = DbHelper.getInstance().getMenuTypes(mainLayout.getContext(),types);
        for (final MenuData item : lst) {
            View view=LayoutInflater.from(mainLayout.getContext()).inflate(R.layout.line_item_style,viewGroup,false);
            IconView iconView=view.findViewById(R.id.icon);
            iconView.setText(Html.fromHtml(item.getIcon(), Html.FROM_HTML_MODE_COMPACT));
            iconView.setTextColor(Color.parseColor(item.getColor()));
            ((TextView) view.findViewById(R.id.txt1)).setText(item.getTitle());
            view.setId(item.getPrimary_id());
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
            lp.setMargins(0, mainLayout.getResources().getDimensionPixelSize(R.dimen.rental_main_gridview_margin_9),
                    0, mainLayout.getResources().getDimensionPixelSize(R.dimen.rental_main_gridview_margin_6));
            view.setLayoutParams(lp);
            view.setOnClickListener(view1 -> RentalOnClickEnumHandle.getType(view1.getId()).run(RoomDetailsExtendableListViewAdapter.this.activity,
                    RoomDetailsExtendableListViewAdapter.this.room, position));
            mainLayout.addView(view);
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
}
