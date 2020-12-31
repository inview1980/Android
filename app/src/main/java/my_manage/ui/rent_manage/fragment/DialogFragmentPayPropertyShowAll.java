package my_manage.ui.rent_manage.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.PayProperty;
import my_manage.pojo.RoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.FlowRadioGroup;
import my_manage.ui.widght.MyDialogFragment;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/22 9:15
 * @Description :
 */
public final class DialogFragmentPayPropertyShowAll extends MyDialogFragment implements IShowList {
    @BindView(R.id.toolbar)         Toolbar           toolbar;
    @BindView(R.id.community)       Spinner           community;
    @BindView(R.id.radioGroup)      FlowRadioGroup    radioGroup;
    @BindView(R.id.main_ListViewId) SwipeMenuListView listView;
    @BindView(R.id.statesBar)       TextView          statesBar;
    private                         List<String>      communityLst;
    private                         List<PayProperty> payPropertyList;
    private RoomDetails activityRoom;

    public DialogFragmentPayPropertyShowAll() {
        this.communityLst = DbHelper.getInstance().getCommunityNamesByNoDel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pay_property_show_all, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("物业缴费详情");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        initValue();

        return v;
    }

    private void initValue() {
        community.setAdapter(new SpinnerStringAdapter(getContext(), communityLst));

        initListView();
    }

    @OnItemSelected(R.id.community)
    void onItemSelected(android.widget.AdapterView<?> adapterView, android.view.View view, int position, long i) {
        radioGroup.removeAllViews();
        val roomLst = DbHelper.getInstance().getRoomDetailsAll().stream()
                .filter(rd -> rd.getCommunityName().equals(communityLst.get(position)))
                .collect(Collectors.toList());
        for (final RoomDetails roomDetails : roomLst) {
            addRadioButton(roomDetails, true);
        }
        View view1 = radioGroup.getChildAt(0);
        if (view1 != null) {
            ((RadioButton) view1).setChecked(true);
            ((RadioButton) view1).callOnClick();
        }
    }

    private void addRadioButton(RoomDetails roomDetails, boolean enabled) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(roomDetails.getRoomNumber());
        radioButton.setTag(roomDetails);
        radioButton.setWidth(getResources().getDimensionPixelSize(R.dimen.pwdItemWidth));
        radioButton.setOnClickListener(this::onRadioClick);
        radioButton.setEnabled(enabled);
        radioGroup.addView(radioButton);
    }

    private void onRadioClick(View view) {
        activityRoom = (RoomDetails) view.getTag();
        payPropertyList = DbHelper.getInstance().getPayPropertyByRoomNumber(activityRoom.getRoomNumber());
        listView.setAdapter(new CommonAdapter<PayProperty>(getContext(), R.layout.pay_property_list_item, payPropertyList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, PayProperty item, int position) {
                Calendar endCal = Calendar.getInstance();
                endCal.setTimeInMillis(item.getStartDate().getTimeInMillis());
                endCal.add(Calendar.MONTH, item.getPayMonth());
                endCal.add(Calendar.DAY_OF_YEAR, -1);
                helper.setText(R.id.payDate, DateUtils.date2String(item.getPayDate()))
                        .setText(R.id.startDate, DateUtils.date2String(item.getStartDate()))
                        .setText(R.id.endDate, DateUtils.date2String(endCal))
                        .setText(R.id.totalMoney, StrUtils.df4.format(item.getTotalMoney()))
                        .setText(R.id.remark, item.getRemarks());
            }
        });
        statesBar.setVisibility(View.VISIBLE);
        statesBar.setText("合计:" + StrUtils.df4.format(payPropertyList.stream().mapToDouble(PayProperty::getTotalMoney).sum()));
    }


    /**
     * 初始化列表
     */
    private void initListView() {
        SwipeMenuCreator creator = menu -> {
            // create "修改缴纳物业费记录" item
            PageUtils.getSwipeMenuItem(getContext(), menu, "修改", Color.rgb(0xC9, 0xC9, 0xCE), R.drawable.ic_playlist_add_black_24dp);
            // create "删除缴纳物业费记录" item
            PageUtils.getSwipeMenuItem(getContext(), menu, "删除", Color.rgb(0xF9, 0x3F, 0x25), R.drawable.ic_delete_black_24dp);
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener((position, menu, index) -> {
            PayProperty item = payPropertyList.get(position);
            switch (index) {
                case 0:
                    // 修改缴纳物业费记录
                    new DialogFragmentPayPropertyAdd(this,item).show(getChildFragmentManager(),"");
                    break;
                case 1:
                    // 删除缴纳物业费记录
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("删除").setMessage("请确定删除此项缴费记录？");
                    dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
                        if(DbBase.deleteWhere(PayProperty.class,"primary_id",new String[]{item.getPrimary_id()+""})>0){
                            PageUtils.showMessage(getContext(),"删除记录成功!");
                            showList();
                        }
                    }).show();
                    break;
            }
            return false;
        });
    }

    @Override
    public void showList() {
        View view=new View(getContext());
        view.setTag(this.activityRoom);
        onRadioClick(view);
    }
}
