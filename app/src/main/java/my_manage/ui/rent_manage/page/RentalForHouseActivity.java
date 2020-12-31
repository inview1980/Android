package my_manage.ui.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.MenuUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.adapter_expandable_List.RoomDetailsExtendableListViewAdapter;
import my_manage.ui.rent_manage.fragment.DialogFragmentInsertPerson;
import my_manage.ui.rent_manage.fragment.DialogFragmentInsertRoom;
import my_manage.ui.rent_manage.fragment.DialogFragmentPayPropertyAdd;
import my_manage.ui.rent_manage.fragment.DialogFragmentPayPropertyShowAll;
import my_manage.ui.rent_manage.listener.RentalMainActivityListener;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

/**
 * 显示指定小区的所有房源列表
 */
public final class RentalForHouseActivity extends MyBaseSwipeBackActivity implements IShowList {
    @BindView(R.id.main_expandable_listview) ExpandableListView                   listView;
    @BindView(R.id.main_viewId)              RelativeLayout                       mainViewId;
    private                                  List<ShowRoomDetails>                sr;
    private                                  String                               title;
    private                                  RoomDetailsExtendableListViewAdapter rh;

    @Override
    protected void onResume() {
        showList();
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        //初始化Toolbar控件
        setTitle(title);

        if (title.contains("全部"))
            title = null;

        //控制ExpandableListView只能打开一个组
        listView.setOnGroupExpandListener(groupPosition -> {
            int count = rh.getGroupCount();
            for (int i = 0; i < count; i++) {
                if (i != groupPosition) {
                    listView.collapseGroup(i);
                }
            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //显示菜单中的图标
        MenuUtils.setIconVisibe(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rental_house_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPerson:
                //增加租户信息
//            PersonListener.addPerson(this,this);
                new DialogFragmentInsertPerson(this, this).show(getSupportFragmentManager(), "");
                break;
            case R.id.addRoom:
                //增加房源信息
                new DialogFragmentInsertRoom<>(this, title).show(getSupportFragmentManager(), "");
                break;
            case R.id.showDeletedRoomDetails:
                //显示已删除房源
                new RentalMainActivityListener().showDeletedRoom(this);
                break;
            case R.id.insertPayPropertyRecord:
                new DialogFragmentPayPropertyAdd().show(this.getSupportFragmentManager(), "");
                break;
            case  R.id.showPayPropertyRecord:
                new DialogFragmentPayPropertyShowAll().show(getSupportFragmentManager(),"");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showList() {
        if ("显示已删除房源".equals(title)) {
            sr = DbHelper.getInstance().getDeleteRoomDetails();
        } else {
            sr = DbHelper.getInstance().getRoomForHouse(title);
        }
        //按日期排序，日期小的靠前
        sr.sort((t1, t2) -> {
            long i1 = t1.getRentalEndDate() == null ? 0 : t1.getRentalEndDate().getTimeInMillis();
            long i2 = t2.getRentalEndDate() == null ? 0 : t2.getRentalEndDate().getTimeInMillis();
            return Long.compare(i1, i2);
        });
        rh = new RoomDetailsExtendableListViewAdapter<>(this, sr, title);
        listView.setAdapter(rh);
    }
}
