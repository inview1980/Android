package my_manage.rent_manage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.List;

import my_manage.adapter.RentalMainAdapter;
import my_manage.password_box.R;
import my_manage.password_box.menuEnum.RentalMainItemLongClickEnum;
import my_manage.password_box.menuEnum.RentalMainOnOneClickEnum;
import my_manage.tool.EnumUtils;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomForMain;

public final class RentalMainActivity extends AppCompatActivity {
    public static List<ShowRoomForMain> showRoomForMainList;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        dbInit();
        init();
        setTitle("房屋出租管理");
//        InputStream is=getResources().openRawResource(R.raw.db);
//        DbHelper.ExcelData ed = DbHelper.getInstance().readExcel(is);
//        System.out.println(ed.getRentalRecordList().get(1));
    }

    private void init() {
        flushView();
        listView.setOnItemClickListener((adV, v, position, l) ->
                RentalMainItemLongClickEnum.ShowRooms.run(this, showRoomForMainList, position));
        listView.setOnItemLongClickListener((AdapterView<?> adapterView, View view, int position, long l) ->
                EnumUtils.menuInit(this, RentalMainItemLongClickEnum.Add, view, showRoomForMainList, position));

        findViewById(R.id.main_add_btn).setOnClickListener(v ->
                EnumUtils.menuInit(this, RentalMainOnOneClickEnum.Add, v, -1));
    }

    public void flushView() {
        showRoomForMainList = DbHelper.getInstance().getShowRoomDesList();

//        Log.i(this.getLocalClassName(), tmpLst.toString());
        RentalMainAdapter adapter = new RentalMainAdapter(this, showRoomForMainList);
        listView = findViewById(R.id.main_ListViewId);
        listView.setAdapter(adapter);
    }

    private void dbInit() {
        //初始化数据库
        String filePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/" + getString(R.string.rentalFileName);
        RentDB.createCascadeDB(this, filePath);
        Log.i(this.getLocalClassName(), "路径：" + filePath);

        List<RoomDetails> rd = RentDB.getQueryAll(RoomDetails.class);
        if (rd != null && rd.size() != 0) return;
        //填充数据库内容
        InputStream is = getResources().openRawResource(R.raw.db);
        DbHelper.ExcelData ed = DbHelper.getInstance().readExcel(is);
        RentDB.insertAll(ed.getPersonDetailsList());
        RentDB.insertAll(ed.getRentalRecordList());
        RentDB.insertAll(ed.getRoomDetailsList());
    }


}
