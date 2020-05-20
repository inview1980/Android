package my_manage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import my_manage.password_box.R;
import my_manage.password_box.page.dialog.Login_Activity;
import my_manage.rent_manage.MyService;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.tool.database.DbHelper;
import my_manage.tool.PageUtils;

public class MainActivity extends AppCompatActivity {
    public static String DBFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据库
        dbInit();

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("path", DBFilePath);
        startService(intent);
    }


    private void showLog(String s) {
        Log.i(PageUtils.Tag,s);
    }


    public void mainBtn_onClick(View view) {
        int btn = view.getId();//获取按键的值
        if (btn == R.id.main_pwdBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Login_Activity.class);
            startActivity(intent);
        }

//        ShareUtils.shareWechatFriend(this,new File(getExternalFilesDir(Environment.DIRECTORY_DCIM)+"/Camera/1581580850281.jpg"));
        if (btn == R.id.main_timeBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RentalMainActivity.class);
            startActivity(intent);
        }
    }

    private void dbInit() {
        //初始化数据库
        DBFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/" + getString(R.string.rentalFileName);
        DbHelper.getInstance().dbInit(this,DBFilePath);
    }
}
