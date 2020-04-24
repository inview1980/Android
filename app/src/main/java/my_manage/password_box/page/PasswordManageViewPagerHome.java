package my_manage.password_box.page;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import my_manage.password_box.R;
import my_manage.adapter.PwdPageAdapter;
import my_manage.password_box.database.PasswordDB;


public final class PasswordManageViewPagerHome extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_manage_details_viewpager);

        Bundle bundle=getIntent().getExtras();
        int currentItem =bundle.getInt("currentItem",0);
        Log.i(this.getLocalClassName(), "currentItem:" + currentItem);
        //找到控件
        ViewPager vp = findViewById(R.id.pwd_manage_viewPager);
        if (-1 == currentItem) {
            //==-1时，为新增
            vp.setAdapter(new PwdPageAdapter(getSupportFragmentManager(), null));
            vp.setCurrentItem(0);
            setTitle("新建帐号和密码");
        } else {
            vp.setAdapter(new PwdPageAdapter(getSupportFragmentManager(), PasswordDB.init().getItems()));
            vp.setCurrentItem(currentItem);
            setTitle("更改");
        }
    }

}
