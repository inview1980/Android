package my_manage.ui.rent_manage.page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
//import my_manage.ui.adapter_page.NewRoomPageAdapter;
import my_manage.password_box.R;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.enums.ShowRoomType;
import my_manage.ui.rent_manage.fragment.FragmentRoomDetails;

/**
 * 出租
 */
public final class RoomDetailsByToolbarActivity extends AppCompatActivity {
    @BindView(R.id.tab_title) TabLayout             tabTitle;
    @BindView(R.id.toolbar)   Toolbar               toolbar;
    @BindView(R.id.viewPage)  ViewPager             viewPage;
    private                   List<ShowRoomDetails> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_toolbar_tablayout_viewpage);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        initPage();
    }

    private void initTabLayout(ShowRoomType type) {
        for (final ShowRoomDetails room : data) {
            if (type == ShowRoomType.History) {
                Calendar date = room.getRentalRecord().getStartDate();
                if (date != null)
                    tabTitle.addTab(tabTitle.newTab().setText(date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)));
                else
                    tabTitle.addTab(tabTitle.newTab().setText(room.getRoomDetails().getRoomNumber()));
            } else if (type == ShowRoomType.Person) {
                Calendar date = room.getRentalRecord().getStartDate();
                tabTitle.addTab(tabTitle.newTab().setText(date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)));
            } else {
                tabTitle.addTab(tabTitle.newTab().setText(room.getRoomDetails().getRoomNumber()));
            }
        }
        PageUtils.addOnTabSelectedListenerByTabLayout(tabTitle, viewPage);
    }

    private void initPage() {
        Bundle       bundle      = getIntent().getExtras();
        ShowRoomType type        = ShowRoomType.getType(bundle.getInt("ShowRoomType", 0));
        int          currentItem = bundle.getInt("currentItem", 0);

        String tmp = bundle.getString("ShowRoomDetails");
        try {
            data = JSONArray.parseArray(tmp, ShowRoomDetails.class);
            initTabLayout(type);
        } catch (Exception e) {
            PageUtils.Error(getLocalClassName() + ":" + e.getLocalizedMessage());
        }

        //初始化ViewPage
        List<Fragment> vList = new ArrayList<>();
        if (type == ShowRoomType.Rent) {
            //出租
            vList.add(new FragmentRoomDetails(data.get(0), type));
        } else {
            data.forEach(item -> vList.add(new FragmentRoomDetails(item, type)));
        }
        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return vList.get(position);
            }

            @Override
            public int getCount() {
                return vList.size();
            }
        });
//        viewPage.setAdapter(new NewRoomPageAdapter(getSupportFragmentManager(), data, type));
        viewPage.setCurrentItem(currentItem);
        tabTitle.getTabAt(currentItem).select();
    }

    @OnPageChange(R.id.viewPage)
    void onPageSelected(int position) {
        tabTitle.getTabAt(position).select();
    }

}
