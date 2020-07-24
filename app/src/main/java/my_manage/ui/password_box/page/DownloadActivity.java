package my_manage.ui.password_box.page;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;
import my_manage.tool.http.HttpUtils;
import my_manage.ui.password_box.R;
import my_manage.ui.widght.MyBaseSwipeBackActivity;
import pojo.Info;
import web.WebResult;

public class DownloadActivity extends MyBaseSwipeBackActivity {
    @BindView(R.id.toolbar)         Toolbar           toolbar;
    @BindView(R.id.main_ListViewId) SwipeMenuListView mainListViewId;
    @BindView(R.id.main_viewId)     RelativeLayout    mainViewId;
    private                         List<Info>        infoList;
    private                         DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

        toolbar.setTitle("从服务器下载数据库");
        initList();
    }

    private void initList() {
        SwipeMenuCreator creator = menu -> {
            // create "删除项目" item
            PageUtils.getSwipeMenuItem(this, menu, "删除", Color.rgb(0xF9, 0x3F, 0x25), R.drawable.ic_delete_black_24dp);
        };
        mainListViewId.setMenuCreator(creator);
        mainListViewId.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {// delete
                deleteItem(position);
            }
            return false;
        });
    }

    @SuppressLint("CheckResult")
    @OnItemClick(R.id.main_ListViewId)
    void onItemClick(android.widget.AdapterView<?> adapterView, android.view.View view, int position, long i) {
        if (infoList == null || infoList.size() == 0 || infoList.size() <= position) return;
        getPermissions();
        Info       info   = infoList.get(position);
        HttpParams params = new HttpParams();
        params.put("fileID", info.getId());
        HttpUtils.get(this, "/info/downloadDB", params, (file, msg) -> {
            if (DbHelper.getInstance().loadFile2DB(file.getAbsolutePath()))
                PageUtils.showMessage(this, "下载指定数据库成功");
            else
                PageUtils.showMessage(this, msg);
        });
    }

    private void deleteItem(int position) {
        HttpParams params = new HttpParams();
        params.put("fileID", infoList.get(position).getId());
        HttpUtils.get(this, "/info/deleteById", params, webResult -> {
            if (webResult.getState() == WebResult.OK) {
                PageUtils.showMessage(this, "删除成功");
                infoList.remove(position);
                initListAdapter();
            } else {
                PageUtils.showMessage(this, webResult.getDetails());
            }
        }, null);
    }

    @Override
    public void showList() {
        DialogPlus dialog = showDialog();
        if (infoList == null) {
            HttpUtils.get(this, "/info/getList", null, webResult -> {
                infoList = (ArrayList<Info>) webResult.getData();
                initListAdapter();
            }, new Info(), dialog);
        } else {
            initListAdapter();
        }
    }

    private void initListAdapter() {
        mainListViewId.setAdapter(new CommonAdapter<Info>(this, R.layout.download_item, infoList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, Info item, int position) {
                helper.setText(R.id.file_Size, item.getFile_size() + "");
                helper.setText(R.id.create_date, item.getCreate_time().format(dtFormatter));
            }
        });
    }

    private DialogPlus showDialog() {
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setContentHolder(new ViewHolder(new ProgressBar(this)))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(R.color.grayBlue)
                .setOnDismissListener(dialog1 -> {
                    OkGo.getInstance().cancelAll();
                })
                .create();
        dialog.show();
        return dialog;
    }

    void getPermissions() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}