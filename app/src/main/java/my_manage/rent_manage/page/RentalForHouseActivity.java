package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import my_manage.adapter_expandable_List.RoomDetailsExtendableListViewAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.EnumUtils;
import my_manage.tool.menuEnum.RentalMainOnOneClickEnum;
import my_manage.tool.menuEnum.RentalRoomLongClickEnum;

public final class RentalForHouseActivity extends AppCompatActivity implements IShowList {
    private ExpandableListView                   listView;
    private List<ShowRoomDetails>                sr;
    private String                               title;
    private RoomDetailsExtendableListViewAdapter rh;

    @Override
    protected void onResume() {
        showList();
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        setTitle(title);
        if (title.contains("全部"))
            title = null;

        listView = findViewById(R.id.main_expandable_listview);

        //控制ExpandableListView只能打开一个组
        listView.setOnGroupExpandListener(groupPosition -> {
            int count = rh.getGroupCount();
            for (int i = 0; i < count; i++) {
                if (i != groupPosition) {
                    listView.collapseGroup(i);
                }
            }
        });
        //浮动按键的弹出菜单
        findViewById(R.id.main_expandable_add_btn).setOnClickListener(v ->
                EnumUtils.menuInit(this, RentalRoomLongClickEnum.Add, v, sr, -1));
    }


    public void showList() {
        if (RentalMainOnOneClickEnum.DeleteHouse.getName().equals(title)) {
            //显示已删除房源列表
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
