package my_manage.ui.rent_manage.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.PayProperty;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/17 10:10
 * @Description :
 */
public final class DialogFragmentPayProperty extends MyDialogFragment implements IShowList {
    @BindView(R.id.toolbar)         Toolbar           toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView listView;
    @BindView(R.id.statesBar)       TextView          statesBar;
    private                         String            community;
    private                         String            roomNumber;
    private                         List<PayProperty> payLst;

    public DialogFragmentPayProperty(String community, String roomNumber) {
        this.community = community;
        this.roomNumber = roomNumber;
//        setFullWindow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_listview, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("物业缴费记录");
        toolbar.setSubtitle(community + ":" + roomNumber);
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        initListView();
        initValue();
        return v;
    }

    private void initValue() {
        payLst = DbHelper.getInstance().getPayPropertyByRoomNumber(roomNumber)
                .stream().sorted((x, y) -> Long.compare(y.getPayDate().getTimeInMillis(), x.getPayDate().getTimeInMillis()))
                .collect(Collectors.toList());
        listView.setAdapter(new CommonAdapter<PayProperty>(getContext(), R.layout.pay_property_list_item, payLst) {
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
        statesBar.setText("合计:" + StrUtils.df4.format(payLst.stream().mapToDouble(PayProperty::getTotalMoney).sum()));
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
            PayProperty item = payLst.get(position);
            switch (index) {
                case 0:
                    // 修改缴纳物业费记录
                    new DialogFragmentPayPropertyAdd(this, item).show(getChildFragmentManager(), "");
                    break;
                case 1:
                    // 删除缴纳物业费记录
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("删除").setMessage("请确定删除此项缴费记录？");
                    dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
                        if (DbBase.deleteWhere(PayProperty.class, "primary_id", new String[]{item.getPrimary_id() + ""}) > 0) {
                            PageUtils.showMessage(getContext(), "删除记录成功!");
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
        initValue();
    }
}
