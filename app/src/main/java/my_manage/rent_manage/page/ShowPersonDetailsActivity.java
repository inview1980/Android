package my_manage.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deadline.statebutton.StateButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import my_manage.adapter_page.NewRoomPageAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.fragment.PersonDetailsFramgent;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.enums.ShowRoomType;
import my_manage.widght.ParallaxSwipeBackActivity;

public class ShowPersonDetailsActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, IShowList {
    @BindView(R.id.tab_title) TabLayout             tabTitle;
    @BindView(R.id.toolbar)   Toolbar               toolbar;
    @BindView(R.id.viewPage)  ViewPager             viewPage;
    private                   List<ShowRoomDetails> data;
    private                   int                   manId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_toolbar_tablayout_viewpage);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        initPage();
    }

    private void initPage() {
        Intent intent = getIntent();
        manId = intent.getIntExtra("manId", 0);

        viewPage.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabTitle.getTabAt(position).select();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public void showList() {
        data = DbHelper.getInstance().getShowRoomDesForPerson(manId);
        initTabLayout();
        List<Fragment> vList = new ArrayList<>();
        data.stream().forEach(item -> vList.add(new PersonDetailsFramgent(item)));
        viewPage.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return vList.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return vList.get(position);
            }
        });
        viewPage.setCurrentItem(0);
        tabTitle.getTabAt(0).select();
    }

    private void initTabLayout() {
        for (final ShowRoomDetails room : data) {
            tabTitle.addTab(tabTitle.newTab().setText(room.getRoomDetails().getRoomNumber()));
        }
        tabTitle.setOnTabSelectedListener(this);
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
