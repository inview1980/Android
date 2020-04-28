package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

import my_manage.adapter.RentalHistoryAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RoomHistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener , IShowList {
    private ListView listView;
    private String roomNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_listview);
        this.listView = findViewById(R.id.main_ListViewId);
        findViewById(R.id.main_add_btn).setVisibility(View.GONE);

        Intent intent = getIntent();
         roomNumber = intent.getStringExtra("roomNumber");
        String communityName = intent.getStringExtra("communityName");
        String area = intent.getStringExtra("area");
        setTitle(communityName+"\t"+roomNumber + ":租房记录\t面积："+area);

        showList();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, RoomDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isHistory", true);
        List<ShowRoomDetails> details = DbHelper.getInstance().getHistoryByRoomNumber(roomNumber);
        bundle.putString("ShowRoomDetails", JSONArray.toJSONString(details));
        bundle.putInt("currentItem", i);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showList() {
        RentalHistoryAdapter adapter = new RentalHistoryAdapter(this
                , DbHelper.getInstance().getHistoryByRoomNumber(roomNumber));
        listView.setAdapter(adapter);
    }
}
