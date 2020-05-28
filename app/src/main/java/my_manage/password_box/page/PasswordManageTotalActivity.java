package my_manage.password_box.page;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.listener.PasswordManageActivityListener;
import my_manage.password_box.pojo.PasswordType;
import my_manage.tool.ExcelUtils;
import my_manage.tool.MenuUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.widght.ParallaxSwipeBackActivity;

public final class PasswordManageTotalActivity extends ParallaxSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar) Toolbar            toolbar;
    @BindView(R.id.content) RecyclerView       content;
    private                 List<PasswordType> passwordTypeList;

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
        passwordTypeList = DbHelper.getInstance().getPasswordTypes();
        val adapter = new CommonRecyclerAdapter<PasswordType>(this, R.layout.password_manage_totle_item, passwordTypeList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, PasswordType item, int position) {
                helper.setText(R.id.icon, Html.fromHtml(item.getUrl(), Html.FROM_HTML_MODE_COMPACT))
                        .setText(R.id.name, item.getName())
                        .setText(R.id.count, "(" + DbHelper.getInstance().getPasswordTypeCount(item.getId()) + ")");
            }
        };
        adapter.setOnItemClickListener((viewHolder, view, i) -> {
            int size = DbHelper.getInstance().getPasswordTypeCount(passwordTypeList.get(i).getId());
            if (size == 0) return;

            Intent intent = new Intent(PasswordManageTotalActivity.this, PasswordManageActivity.class);
            intent.putExtra("PasswordTypeId", passwordTypeList.get(i).getId());
            startActivity(intent);
        });
        content.setAdapter(adapter);
    }
}
