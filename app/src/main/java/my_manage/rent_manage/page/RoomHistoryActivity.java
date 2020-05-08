package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.adapter.RentalHistoryAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.enums.ShowRoomType;

public final class RoomHistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IShowList {
    @BindView(R.id.toolbar)         Toolbar        toolbar;
    @BindView(R.id.main_ListViewId) ListView       listView;
    @BindView(R.id.main_add_btn)    ImageButton    mainAddBtn;
    @BindView(R.id.main_viewId)     RelativeLayout mainViewId;
    private                         String         roomNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);

        mainAddBtn.setVisibility(View.GONE);

        Intent intent = getIntent();
        roomNumber = intent.getStringExtra("roomNumber");
        String communityName = intent.getStringExtra("communityName");
        String area          = intent.getStringExtra("area");
        toolbar.setTitle(communityName);
        toolbar.setSubtitle(roomNumber + ":租房记录\t面积：" + area);
        toolbar.setNavigationOnClickListener(v -> finish());

        showList();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, RoomDetailsByToolbarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("ShowRoomType", ShowRoomType.History.getIndex());
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
