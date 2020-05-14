package my_manage.rent_manage.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.adapter_page.NewRoomPageAdapter;
import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.enums.ShowRoomType;
import my_manage.widght.ParallaxSwipeBackActivity;

/**
 * 出租
 */
public final class RoomDetailsByToolbarActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener {
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
        tabTitle.setOnTabSelectedListener(this);
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
        }
        viewPage.setAdapter(new NewRoomPageAdapter(getSupportFragmentManager(), data, type));
        viewPage.setCurrentItem(currentItem);
        tabTitle.getTabAt(currentItem).select();
        viewPage.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabTitle.getTabAt(position).select();
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPage.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
