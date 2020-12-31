package my_manage.ui.rent_manage.page;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.rent_manage.fragment.FramgentPersonDetails;
import my_manage.pojo.show.ShowRoomDetails;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ShowPersonDetailsActivity extends AppCompatActivity implements IShowList {
    @BindView(R.id.tab_title) TabLayout tabTitle;
    @BindView(R.id.toolbar)   Toolbar   toolbar;
    @BindView(R.id.viewPage)  ViewPager viewPage;
    private                   int       manId;

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

        PageUtils.addOnTabSelectedListenerByTabLayout(tabTitle,viewPage);
    }

    @OnPageChange(R.id.viewPage)
    void onPageSelected(int position) {
        if (tabTitle.getTabCount() > position)
            tabTitle.getTabAt(position).select();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public void showList() {
        List<ShowRoomDetails> data = DbHelper.getInstance().getShowRoomDesForPerson(manId);
        data.forEach(room -> tabTitle.addTab(tabTitle.newTab().setText(room.getRoomDetails().getRoomNumber())));

        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            List<Fragment> vList = data.stream().map(FramgentPersonDetails::new).collect(Collectors.toList());

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
    }

}
