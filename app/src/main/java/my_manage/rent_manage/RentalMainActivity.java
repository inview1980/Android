package my_manage.rent_manage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.adapter.RentalMainAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.listener.PersonListener;
import my_manage.rent_manage.listener.RentalMainActivityListener;
import my_manage.rent_manage.listener.RoomListener;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.EnumUtils;
import my_manage.tool.MenuUtils;
import my_manage.tool.NotificationChannels;
import my_manage.tool.menuEnum.RentalMainItemLongClickEnum;

public final class RentalMainActivity extends AppCompatActivity implements IShowList {
    public static                   List<ShowRoomDetails>      showRoomForMainList;
    @BindView(R.id.toolbar)         Toolbar                    toolbar;
    @BindView(R.id.main_ListViewId) ListView                   listView;
    @BindView(R.id.main_add_btn)    ImageButton                mainAddBtn;
    @BindView(R.id.main_viewId)     RelativeLayout             mainViewId;
    private                         RentalMainActivityListener listener = new RentalMainActivityListener();
    private                         String                     filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);
        init();
        initToolBar();

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("path", this.filePath);
        startService(intent);
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
            //输出数据至excel文件
//            listener.saveDB(this);
        } else if (id == R.id.readDB) {
            //读取数据至数据库
//            listener.loadDB(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        showList();
        mainAddBtn.setVisibility(View.GONE);
        //listView单击事件
        listView.setOnItemClickListener((adV, v, position, l) -> listener.showRoomDetails(this,
                showRoomForMainList.get(position).getRoomDetails().getCommunityName()));
        //ListView长按事件
        listView.setOnItemLongClickListener((AdapterView<?> adapterView, View view, int position, long l) ->
                EnumUtils.menuInit(this, RentalMainItemLongClickEnum.Add, view, showRoomForMainList, position));
    }

    public void showList() {
        showRoomForMainList = DbHelper.getInstance().getShowRoomDesList();
        RentalMainAdapter adapter = new RentalMainAdapter(this, showRoomForMainList);
        listView.setAdapter(adapter);
    }

}
