package my_manage.ui.fuel.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
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
import my_manage.pojo.FuelRecord;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.fuel.listener.FuelListener;
import my_manage.password_box.R;
import my_manage.ui.widght.ParallaxSwipeBackActivity;

/**
 * @author inview
 * @Date 2020/11/23 15:55
 * @Description :
 */
public final class FuelRecordMainActivity extends ParallaxSwipeBackActivity implements IShowList {
    private                 List<FuelRecord>  fuelRecordList;
    @BindView(R.id.toolbar)         Toolbar           toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView listView;
    @BindView(R.id.main_viewId)     RelativeLayout    mainViewId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

//        listView.setOnItemClickListener((adV, v, position, l) -> listener.showRoomDetails(this,
//                fuelRecordList.get(position).getRoomDetails().getCommunityName()));
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
            FuelRecord item = fuelRecordList.get(position);
            switch (index) {
                case 0:
//                    FuelListener.modifyFuelRecordCallActivity(this, item);
                    new DialogFragmentModifyFuelRecord< >(item,this).show(getSupportFragmentManager(),"" );
                    break;
                case 1:
                    FuelListener.deleteFuelRecord(FuelRecordMainActivity.this, item);
                    break;
            }
            return false;
        });
    }

    private void initToolBar() {
        toolbar.setTitle("加油记录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void showList() {
        //判断窗口的参数中是否包含year键，否则显示全部
        int year = getIntent().getIntExtra("year", 0);
        fuelRecordList = DbHelper.getInstance().getFuelRecordList();
        if (year != 0)
            fuelRecordList = fuelRecordList.stream().filter(fr -> fr.getTime().get(Calendar.YEAR) == year).collect(Collectors.toList());

        listView.setAdapter(new CommonAdapter<FuelRecord>(this,
                //视图
                R.layout.fuel_list_item,
                fuelRecordList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, FuelRecord item, int position) {
                int oldOdometerNumber = (position < fuelRecordList.size() - 1) ? fuelRecordList.get(position + 1).getOdometerNumber() : 0;
                if (item.getRise() == 0) item.setRise(1);//如果升数=0，默认为1
                helper.setText(R.id.date, DateUtils.date2String(item.getTime()))
                        .setText(R.id.original_price, StrUtils.df4.format(item.getMarketPrice()))
                        .setText(R.id.rise, StrUtils.df4.format(item.getRise()))
                        .setText(R.id.odometerNumber, StrUtils.df4.format(item.getOdometerNumber()))
                        .setText(R.id.fuel_consumption, StrUtils.df4.format(item.getRise() / Math.abs(item.getOdometerNumber() - oldOdometerNumber) * 100))
                        .setText(R.id.totalMoney, StrUtils.df4.format(item.getMoney()))
                        .setText(R.id.save_money, StrUtils.df4.format(item.getRise() * item.getMarketPrice() - item.getMoney()))
                        .setText(R.id.station_name, item.getStationName())
                        .setText(R.id.price, StrUtils.df4.format(item.getMoney() / item.getRise()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fuel_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addFuelRecord) {
            //添加加油记录
            new DialogFragmentModifyFuelRecord<>(null,this).show(getSupportFragmentManager(),"" );
//            startActivityForResult(new Intent(this, AddFuelRecordActivity.class), 2020);
        } else if (id == R.id.deleteAllFuelRecord) {
            //删除所有加油记录
            FuelListener.deleteAllFuelRecord(this);
        } else if (id == R.id.dataAnalysis) {
            //数据分析
            new DialogFragmentDataAnalysis().show(getSupportFragmentManager(),"");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        showList();
    }
}
