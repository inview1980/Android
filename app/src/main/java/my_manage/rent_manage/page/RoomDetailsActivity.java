package my_manage.rent_manage.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

import my_manage.adapter_page.NewRoomPageAdapter;
import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class RoomDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_manage_details_viewpager);

        Bundle  bundle         = getIntent().getExtras();
        int     currentItem    = bundle.getInt("currentItem", 0);
        boolean isHistory      = bundle.getBoolean("isHistory",false);
        boolean isRentRoom = bundle.getBoolean("isRentRoom",false);

        ViewPager vp = findViewById(R.id.pwd_manage_viewPager);
        if (isRentRoom) {
            setTitle("出租");

        } else if (isHistory) {
            //为出租记录
            setTitle("出租记录");
        } else {
            setTitle("详情");
        }
        String tmp = bundle.getString("ShowRoomDetails");
        try {
            List<ShowRoomDetails> data = JSONArray.parseArray(tmp, ShowRoomDetails.class);
            vp.setAdapter(new NewRoomPageAdapter(getSupportFragmentManager(), data, isHistory,isRentRoom));
            vp.setCurrentItem(currentItem);
        } catch (Exception e) {
        }
    }
}
