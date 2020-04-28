package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import my_manage.adapter_expandable_List.RoomDetailsExtendableListViewAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.menuEnum.RentalMainOnOneClickEnum;

public final class RentalForHouseActivity extends AppCompatActivity implements IShowList {
//    private ListView listView;
    private ExpandableListView listView;
    private List<ShowRoomDetails> sr;
    private String title;

    @Override
    protected void onResume() {
        showList();
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_listview);
setContentView(R.layout.expandable_listview);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        setTitle(title);
        if (title.contains("全部"))
            title = null;

//        listView = findViewById(R.id.main_ListViewId);
        listView = findViewById(R.id.main_expandable_listview);

        //list中Item单击弹出菜单
//        listView.setOnItemClickListener((adV, v, position, l) -> {
//            if (title != null && title.contains("删除")) {
//                //恢复删除的房源
//                recoverDel(position);
//            }else if (sr.get(position).getRentalRecord() == null) {
//                //未出租
//                EnumUtils.menuInit(this, RentalRoomNotRentedClickEnum.Show, v, sr, position);
//            } else {
//                //已出租
//                EnumUtils.menuInit(this, RentalRoomRentedClickEnum.Show, v, sr, position);
//            }
//        });
//
//        findViewById(R.id.main_add_btn).setOnClickListener(v ->
//                EnumUtils.menuInit(this, RentalRoomLongClickEnum.Add, v, sr, -1));
    }

    private void recoverDel(int position) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("恢复删除房源").setMessage("是否恢复已删除的房源？");
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
            if(sr!=null && position>=0 && sr.size()>position){
                if(DbHelper.getInstance().restoreDelete(sr.get(position))){
                    Toast.makeText(this, "恢复房源成功", Toast.LENGTH_SHORT).show();
                    showList();
                }else {
                    Toast.makeText(this, "恢复房源失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
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
        RoomDetailsExtendableListViewAdapter rh=new RoomDetailsExtendableListViewAdapter(this,sr,title);
//        RentalHouseAdapter rh = new RentalHouseAdapter(this, sr, title);
        listView.setAdapter(rh);
        listView.setOnGroupExpandListener(groupPosition -> {
            int count = rh.getGroupCount();
            for(int i = 0;i < count;i++){
                if (i!=groupPosition){
                    listView.collapseGroup(i);
                }
            }
        });
    }
}
