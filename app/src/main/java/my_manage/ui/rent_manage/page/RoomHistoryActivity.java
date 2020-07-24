package my_manage.ui.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.iface.IShowList;
import my_manage.ui.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.enums.ShowRoomType;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

public final class RoomHistoryActivity extends MyBaseSwipeBackActivity implements AdapterView.OnItemClickListener, IShowList {
    @BindView(R.id.main_ListViewId) ListView       listView;
    @BindView(R.id.main_viewId)     RelativeLayout mainViewId;
    private                         String         roomNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_listview);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        roomNumber = intent.getStringExtra("roomNumber");
        String communityName = intent.getStringExtra("communityName");
        String area          = intent.getStringExtra("area");
        setTitle(communityName);
        setSubtitle(roomNumber + ":租房记录\t面积：" + area);

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
        listView.setAdapter(new CommonAdapter<ShowRoomDetails>(this,
                //布局文件
                R.layout.rental_history_list_item,
                //指定房号的历史记录
                DbHelper.getInstance().getHistoryByRoomNumber(roomNumber)) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, ShowRoomDetails item, int position) {
                helper.setText(R.id.rental_history_item_man, item.getPersonDetails().getName())
                        .setText(R.id.rental_history_item_startDate, DateUtils.date2String(item.getRentalRecord().getStartDate(),
                                item.getRentalRecord().getPayMonth()));
            }
        });
    }
}
