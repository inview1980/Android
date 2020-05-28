package my_manage.rent_manage;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.ExcelUtils;
import my_manage.tool.database.DbHelper;
import my_manage.rent_manage.listener.PersonListener;
import my_manage.rent_manage.listener.RentalMainActivityListener;
import my_manage.rent_manage.listener.RoomListener;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.MenuUtils;
import my_manage.tool.PageUtils;
import my_manage.widght.ParallaxSwipeBackActivity;

public final class RentalMainActivity extends ParallaxSwipeBackActivity implements IShowList {
    public static                   List<ShowRoomDetails>      showRoomForMainList;
    @BindView(R.id.toolbar)         Toolbar                    toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView          listView;
    @BindView(R.id.main_viewId)     RelativeLayout             mainViewId;
    private                         RentalMainActivityListener listener = new RentalMainActivityListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

        listView.setOnItemClickListener((adV, v, position, l) -> listener.showRoomDetails(this,
                showRoomForMainList.get(position).getRoomDetails().getCommunityName()));
        initToolBar();
        initListView();
    }

    private void initListView() {
        SwipeMenuCreator creator = menu -> {
            // create "增加房源" item
            PageUtils.getSwipeMenuItem(this,menu, "增加", Color.rgb(0xC9, 0xC9, 0xCE), R.drawable.ic_playlist_add_black_24dp);
            // create "删除小区" item
            PageUtils.getSwipeMenuItem(this,menu, "删除", Color.rgb(0xF9, 0x3F, 0x25), R.drawable.ic_delete_black_24dp);
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener((position, menu, index) -> {
            ShowRoomDetails item = showRoomForMainList.get(position);
            switch (index) {
                case 0:
                    // 增加房源
                    RoomListener.addRoomDetails(RentalMainActivity.this, item);
                    break;
                case 1:
                    // delete
                    RoomListener.delCommunity(RentalMainActivity.this, item);
                    break;
            }
            return false;
        });
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //显示菜单中的图标
        MenuUtils.setIconVisibe(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    private void initToolBar() {
        toolbar.setTitle("房屋出租管理");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rental_main_activity_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.showRoomDetails) {
            //显示所有租户信息
            PersonListener.showPersonDetails(this);
        } else if (id == R.id.addRoom) {
            //增加房源
            RoomListener.addRoomDetails(this, null);
        } else if (id == R.id.showDeletedRoomDetails) {
            //显示已删除房源
            listener.showDeletedRoom(this);
        } else if (id == R.id.saveDB) {
            //将数据库转出为xlsx
            ExcelUtils.getInstance().saveDB(this);
        } else if (id == R.id.rebuildingDB) {
            //删除并重建数据库
            listener.rebuildingDB(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showList() {
        showRoomForMainList = DbHelper.getInstance().getShowRoomDesList();
        listView.setAdapter(new CommonAdapter<ShowRoomDetails>(this,
                //视图
                R.layout.rental_room_total_item,
                showRoomForMainList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, ShowRoomDetails item, int position) {
                helper.setText(R.id.rental_main_CommunityName,item.getRoomDetails().getCommunityName())
                      .setText(R.id.rental_main_item_areaTotal,String.valueOf(item.getRoomAreas()))
                        .setText(R.id.rental_main_item_roomTotal,String.valueOf(item.getRoomCount()));
            }
        });
    }

}
