package my_manage.ui.widght;

import android.content.Intent;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import my_manage.iface.IShowList;
import my_manage.tool.MenuUtils;
import my_manage.password_box.R;
import my_manage.ui.common.CheckPassword;

/**
 * @author inview
 * @Date 2020/7/6 11:39
 * @Description :退出此页面一段时间后需要登录
 */
public abstract class MyBaseActivity extends AppCompatActivity implements IShowList{
    protected static long    time   = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8080) {
            time = new Date().getTime();
        } else {
            //按退出键时
            time = 1;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        time = new Date().getTime();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //显示菜单中的图标
        MenuUtils.setIconVisibe(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUser(time);
    }

    /**
     * 如果超过时间，要求输入密码；如果数据已变更，重新载入数据
     *
     * @param time
     */
    private   void checkUser(long time) {
        long t = getResources().getInteger(R.integer.LockIntervalByMinute) * 60 * 1000;
//        long t = 10 * 1000;
        if (time == 0 || (new Date().getTime() - time) < t) {
            showList();
        } else {
            Intent intent = new Intent(this, CheckPassword.class);
            intent.putExtra("isLogin", false);
            startActivityForResult(intent, 8080);
        }
    }

    @Override
    public abstract void showList();
}
