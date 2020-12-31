package my_manage.ui.car.page;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Triple;
import kotlin.TuplesKt;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.pojo.CarMaintenanceRecord;
import my_manage.pojo.FuelRecord;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.password_box.R;
import my_manage.ui.widght.ParallaxSwipeBackActivity;

/**
 * @author inview
 * @Date 2020/11/27 8:53
 * @Description :
 */
public final class ActivityCarMaintenanceMain extends ParallaxSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar)         Toolbar                    toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView          listView;
    @BindView(R.id.statesBar)       TextView                   statesTxt;
    private                         List<CarMaintenanceRecord> carList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

        initToolBar();
        initListView();
    }

    private void initListView() {
        SwipeMenuCreator creator = menu -> {
//             create "修改记录" item
            PageUtils.getSwipeMenuItem(this, menu, "修改", Color.rgb(0xC9, 0xC9, 0xCE), R.drawable.ic_playlist_add_black_24dp);
            // create "删除记录" item
            PageUtils.getSwipeMenuItem(this, menu, "删除", Color.rgb(0xF9, 0x3F, 0x25), R.drawable.ic_delete_black_24dp);
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener((position, menu, index) -> {
            CarMaintenanceRecord item = carList.get(position);
            switch (index) {
                case 0:
                    new DialogFragmentCarModify(this, item).show(getSupportFragmentManager(), "");
                    break;
                case 1:
                    deleteCar(item);
                    break;
            }
            return false;
        });
    }

    private void deleteCar(CarMaintenanceRecord car) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("删除");
        if (car != null)
            dialog.setMessage("确定删除此项车辆维护记录？");
        else
            dialog.setMessage("确定删除所有车辆维护记录？");

        dialog.setPositiveButton("确定", (dialog1, which) -> {
            boolean isOk = false;
            if (car != null) {
                if (DbBase.deleteWhere(CarMaintenanceRecord.class, "primary_id", new String[]{car.getPrimary_id() + ""}) > 0)
                    isOk = true;
            } else {
                if (DbBase.deleteAll(CarMaintenanceRecord.class) > 0)
                    isOk = true;
            }
            if (isOk) {
                PageUtils.showMessage(this, "删除记录成功");
                showList();
            } else {
                PageUtils.showMessage(this, "删除记录失败");
            }
        });
        dialog.show();
    }

    @Override
    public void showList() {
        carList = DbHelper.getInstance().getCarMaintenanceList();
        listView.setAdapter(new CommonAdapter<CarMaintenanceRecord>(this,
                //视图
                R.layout.car_main_list_item,
                carList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, CarMaintenanceRecord item, int position) {
                helper.setText(R.id.car_type_name, item.getMaintenanceType())
                        .setText(R.id.date, DateUtils.date2String(item.getDate()))
                        .setText(R.id.money, StrUtils.df4.format(item.getMoney()))
                        .setText(R.id.odometerNumber, StrUtils.df4.format(item.getOdometerNumber()))
                        .setText(R.id.address, item.getAddress());
                if (StrUtils.isNotBlank(item.getRemark())) {
                    helper.setVisible(R.id.remarkLayout, View.VISIBLE)
                            .setText(R.id.remark, item.getRemark());
                } else {
                    helper.setVisible(R.id.remarkLayout, View.GONE);
                }
            }
        });
        showStatesBar();

    }

    private void showStatesBar() {
        //状态栏显示：距上次保养的时间，距上次保养的里程
        Triple<Calendar, Integer, Integer> result = DbHelper.getInstance().getCarMaintenanceTimeAndOdometerNumber();

        //计算日期之间的差
        Calendar date3 = Calendar.getInstance();
        date3.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - result.getFirst().getTimeInMillis());
        String txt = "最后一次保养时间:" + DateUtils.date2String(result.getFirst()) + "，相距" + (date3.get(Calendar.YEAR) - 1970) + "年"
                + date3.get(Calendar.MONTH) + "月" + date3.get(Calendar.DAY_OF_MONTH) + "天";
        txt += "\n最后一次保养:" + result.getThird() + "，和最后一次加油:" + result.getSecond() + ",相差" + (result.getSecond() - result.getThird());
        statesTxt.setText(txt);
        statesTxt.setVisibility(View.VISIBLE);
    }

    private void initToolBar() {
        toolbar.setTitle("车辆维护记录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "新增");
        menu.add(0, 1, 1, "删除所有记录");
//        getMenuInflater().inflate(R.menu.fuel_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 0) {
            //添加加油记录
            new DialogFragmentCarModify(this, null).show(getSupportFragmentManager(), "");
            showList();
        }
        if (id == 1) {
            //删除所有加油记录
            deleteCar(null);
        }
        return super.onOptionsItemSelected(item);
    }

}
