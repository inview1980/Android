package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import my_manage.adapter.RentalHouseAdapter;
import my_manage.password_box.R;
import my_manage.password_box.menuEnum.RentalRoomClickEnum;
import my_manage.password_box.menuEnum.RentalRoomLongClickEnum;
import my_manage.tool.EnumUtils;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RentalForHouse extends AppCompatActivity {
    private ListView listView;
    private List<ShowRoomDetails> sr;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        setTitle(title);
        if (title.contains("全部"))
            title = null;

        listView = findViewById(R.id.main_ListViewId);

        showList();
        //list中Item单击弹出菜单
        listView.setOnItemClickListener((adV, v, position, l) ->
                EnumUtils.menuInit(this, RentalRoomClickEnum.Edit, v, sr, position));
//        listView.setOnItemLongClickListener((AdapterView<?> adapterView, View view, int position, long l) ->
//                EnumUtils.menuInit(this, RentalRoomClickEnum.Edit, view, sr, position));

        findViewById(R.id.main_add_btn).setOnClickListener(v ->
                EnumUtils.menuInit(this, RentalRoomLongClickEnum.Add, v, sr, -1));
    }

    public void showList() {
        sr = DbHelper.getInstance().getRoomForHouse(title);
        //按日期排序，日期小的靠前
        sr.sort((t1, t2) -> {
            long i1 = t1.getEndDate() == null ? 0 : t1.getEndDate().getTimeInMillis();
            long i2 = t2.getEndDate() == null ? 0 : t2.getEndDate().getTimeInMillis();
            return Long.compare(i1, i2);
        });

        RentalHouseAdapter rh = new RentalHouseAdapter(this, sr, title);
        listView.setAdapter(rh);
    }
}
