package my_manage.password_box.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import my_manage.adapter.UserItemAdapter;
import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.menuEnum.PwdContentMenuEnum;
import my_manage.password_box.menuEnum.PwdContextActivityLongClickEnum;
import my_manage.tool.EnumUtils;


public final class PasswordManageActivity extends AppCompatActivity {
    private ListView listView;
    @SuppressLint("StaticFieldLeak")
    public static boolean isDBChanged = false;

    @Override
    protected void onStart() {
        super.onStart();
        isNeedSaveDB();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);

        init();
    }

    private void init() {
        listView = findViewById(R.id.main_ListViewId);
        setTitle("密码明细");

        //listView单击事件
        listView.setOnItemClickListener((adV, v, position, l) -> PwdContextActivityLongClickEnum.Edit.run(this, position));

        //ListView所有 item长按事件，弹出菜单
        listView.setOnItemLongClickListener((AdapterView<?> adapterView, View view, int position, long l) ->
                EnumUtils.menuInit(this, PwdContextActivityLongClickEnum.Delete, view, position));

        //按键单击事件，弹出菜单
        findViewById(R.id.main_add_btn).setOnClickListener(v ->
                EnumUtils.menuInit(this, PwdContentMenuEnum.Add, v, -1));
        showLst();
    }

    /**
     * 重新载入列表
     */
    public void showLst() {
        UserItemAdapter adapter = new UserItemAdapter(this, PasswordDB.init().getItems());
        Log.i(this.getLocalClassName(), PasswordDB.init().getItems().toString());
        listView.setAdapter(adapter);

        isNeedSaveDB();
    }

    @Override
    protected void onDestroy() {
        isNeedSaveDB();
        super.onDestroy();
    }

    private void isNeedSaveDB() {
        //保存数据库文件
        if (isDBChanged) {
            if (PasswordDB.init().save()) {
                isDBChanged = false;
                showMessage("保存数据库成功");
            } else {
                showMessage("保存数据库失败");
            }
        }
    }


    public void showMessage(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        Log.i(this.getLocalClassName(), msg);
    }


    //region Description
    //    @Override
//    public void onClick(View view) {
//        EnumUtils.menuInit(this, PwdContentMenuEnum.Add, view, -1);
//        //初始化菜单
//        PopupMenu clickMenu = new PopupMenu(this, view);
//        for (PwdContentMenuEnum value : PwdContentMenuEnum.values()) {
//            clickMenu.getMenu().add(0, value.getIndex(), Menu.NONE, value.getName());
//        }
//        // menu的item点击事件
//        clickMenu.setOnMenuItemClickListener(item -> {
//            PwdContentMenuEnum.getEnum(item.getItemId()).run(this, -1);
//            return false;
//        });
//        clickMenu.show();
//    }


//    @Override
//    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//        return EnumUtils.menuInit(this, PwdContextActivityLongClickEnum.Delete, view, position);
//    }
    //endregion

//    /**
//     * 接收应用内的广播，当passwordDB内的数据变更时
//     */
//    private class PwdDBChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            System.out.println("已收到广播");
//            if (intent != null) {
//                UserItem userItem = JSON.parseObject(intent.getStringExtra("userItem"), UserItem.class);
//                int currentItem = intent.getIntExtra("currentItem", -1);
//                if (currentItem == -1) {
//                    PasswordDB.init().getItems().add(userItem);
//                    isDBChanged = true;
//                } else if (PasswordDB.init().getItems().size() >= currentItem) {
//                    PasswordDB.init().getItems().set(currentItem, userItem);
//                    isDBChanged = true;
//                }
//                showLst();//重新载入列表
//            }
//        }
//    }
}
