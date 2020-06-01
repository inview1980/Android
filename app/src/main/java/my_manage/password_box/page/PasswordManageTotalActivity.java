package my_manage.password_box.page;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.listener.PasswordManageActivityListener;
import my_manage.rent_manage.pojo.show.MenuData;
import my_manage.tool.MenuUtils;
import my_manage.tool.database.DbHelper;
import my_manage.tool.enums.MenuTypesEnum;
import my_manage.widght.ParallaxSwipeBackActivity;

public final class PasswordManageTotalActivity extends ParallaxSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar) Toolbar        toolbar;
    @BindView(R.id.content) RecyclerView   content;
    private                 List<MenuData> passwordTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_manage_totle_activity);
        ButterKnife.bind(this);

        //初始化Toolbar控件
        toolbar.setTitle(R.string.passwordBox);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        content.setLayoutManager(new GridLayoutManager(this,getResources().getInteger( R.integer.password_total_item_count)));
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
        PasswordManageActivityListener.onOptionsItemSelected(this, item.getItemId(),"");
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public void showList() {
        passwordTypeList = DbHelper.getInstance().getMenuTypes(getBaseContext(),MenuTypesEnum.PasswordType);
        val adapter = new CommonRecyclerAdapter<MenuData>(this, R.layout.password_manage_totle_item, passwordTypeList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, MenuData item, int position) {
                helper.setText(R.id.icon, Html.fromHtml(item.getIcon(), Html.FROM_HTML_MODE_COMPACT))
                        .setTextColor(R.id.icon, Color.parseColor(item.getColor()))
                        .setText(R.id.name, item.getTitle())
                        .setText(R.id.count, "(" + DbHelper.getInstance().getPasswordTypeCount(item.getPrimary_id()) + ")");
            }
        };
        adapter.setOnItemClickListener((viewHolder, view, i) -> {
            int size = DbHelper.getInstance().getPasswordTypeCount(passwordTypeList.get(i).getPrimary_id());
            if (size == 0) return;

            Intent intent = new Intent(PasswordManageTotalActivity.this, PasswordManageActivity.class);
            intent.putExtra("PasswordTypeId", passwordTypeList.get(i).getPrimary_id());
            startActivity(intent);
        });
        content.setAdapter(adapter);
    }
}
