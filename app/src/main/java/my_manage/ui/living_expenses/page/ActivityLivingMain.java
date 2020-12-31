package my_manage.ui.living_expenses.page;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
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
import my_manage.pojo.LivingExpenses;
import my_manage.pojo.show.MenuData;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.tool.enums.MenuTypesEnum;
import my_manage.ui.living_expenses.fragment.DialogFragmentLivingAddOrUpdate;
import my_manage.ui.living_expenses.fragment.DialogFragmentLivingDataAnalysis;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

/**
 * @author inview
 * @Date 2020/12/28 10:55
 * @Description :
 */
public class ActivityLivingMain extends MyBaseSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar) Toolbar        toolbar;
    @BindView(R.id.content) RecyclerView   content;
    private                 List<MenuData> iconTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_main);
        ButterKnife.bind(this);

        //初始化Toolbar控件
        setTitle("生活缴费");

        content.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.password_total_item_count)));
//        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.living_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addRecord:
                new DialogFragmentLivingAddOrUpdate(this, null).show(getSupportFragmentManager(), "");
                break;
            case R.id.dataAnalysis:
                new DialogFragmentLivingDataAnalysis(null).show(getSupportFragmentManager(), "");
                break;
            case R.id.deleteAllRecord:
                dialogRemoveAll();
                break;
            // TODO: 2020/12/28  
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogRemoveAll() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("删除")
                .setMessage("确定删除所有生活缴费记录？")
                .setPositiveButton("确定", (dialog1, which) -> {
                    if (DbBase.deleteAll(LivingExpenses.class)>0) {
                        //刷新
                        PageUtils.showMessage(this, "删除记录成功");
                        finish();
                    }
                }).show();
    }

    @Override
    public void showList() {
        iconTypeList = DbHelper.getInstance().getMenuTypes(getBaseContext(), MenuTypesEnum.LivingExpenses);
        val adapter = new CommonRecyclerAdapter<MenuData>(this, R.layout.icon_style, iconTypeList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, MenuData item, int position) {
                helper.setText(R.id.icon, Html.fromHtml(item.getIcon(), Html.FROM_HTML_MODE_COMPACT))
                        .setTextColor(R.id.icon, Color.parseColor(item.getColor()))
                        .setText(R.id.name, item.getTitle())
                        .setText(R.id.count, "(" + DbHelper.getInstance().getLiveTypeCount(item.getTitle()) + ")");
            }
        };
        adapter.setOnItemClickListener((viewHolder, view, i) -> {
            int size = DbHelper.getInstance().getLiveTypeCount(iconTypeList.get(i).getTitle());
            if (size == 0) return;
            Intent intent=new Intent(this,ActivityLivingByType.class);
            intent.putExtra("typeName", iconTypeList.get(i).getTitle());
            startActivity(intent);
        });
        content.setAdapter(adapter);
    }

}
