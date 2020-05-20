package my_manage.password_box.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import butterknife.OnItemClick;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.listener.PasswordManageActivityListener;
import my_manage.password_box.pojo.UserItem;
import my_manage.tool.MenuUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;
import my_manage.widght.ParallaxSwipeBackActivity;


public final class PasswordManageActivity extends ParallaxSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar)         Toolbar           toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView listView;
    @BindView(R.id.main_viewId)     RelativeLayout    mainViewId;
    private List<UserItem> itemList;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

        //初始化Toolbar控件
        toolbar.setTitle("密码箱");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        initListView();
    }

    private void initListView() {
        SwipeMenuCreator creator = menu -> {
            // create "删除项目" item
            PageUtils.getSwipeMenuItem(this, menu, "删除", Color.rgb(0xF9, 0x3F, 0x25), R.drawable.ic_delete_black_24dp);
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {// delete
                PasswordManageActivityListener.deleteItem(PasswordManageActivity.this,itemList.get( position));
            }
            return false;
        });
    }

    /**
     * listView单击事件
     */
    @OnItemClick(R.id.main_ListViewId)
    void OnItemClickListener(AdapterView<?> adv, View v, int position, long l) {
        Intent intent = new Intent(this, PasswordManageViewPagerHome.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currentItem", position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //显示菜单中的图标
        MenuUtils.setIconVisibe(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pwd_manage_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            //增加信息
            PasswordManageActivityListener.callPasswordManageItemDetails(this, -1);
        } else if (id == R.id.modify) {
            //调用更改密码窗口
            startActivity(new Intent(this, my_manage.password_box.page.dialog.changePasswordDialog.class));
        } else if (id == R.id.rebuildingDB) {
            //重建资料和密码成功
            PasswordManageActivityListener.resetDatabaseAndPassword(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }


    /**
     * 重新载入列表
     */
    @Override
    public void showList() {
        itemList=DbHelper.getInstance().getItemsByAfter(this);
        listView.setAdapter(new CommonAdapter<UserItem>(this, R.layout.password_manage_list_item, itemList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, UserItem item, int position) {
                helper.setText(R.id.pwd_ItemId, item.getItemName())
                        .setText(R.id.pwd_AddressId, item.getAddress())
                        .setText(R.id.pwd_UserNameId, item.getUserName())
                        .setText(R.id.pwd_PasswordId, item.getPassword());
            }
        });
    }
}
