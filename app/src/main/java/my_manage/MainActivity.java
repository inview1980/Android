package my_manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.List;

import my_manage.password_box.R;
import my_manage.password_box.page.dialog.Login_Activity;
import my_manage.rent_manage.MyService;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.RoomDetails;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    public static String DBFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据库
        dbInit();

        context = this;
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("path", DBFilePath);
        startService(intent);
//        PwdDBChangeReceiver pwdDBChangeReceiver=new PwdDBChangeReceiver();
//        IntentFilter filter = new IntentFilter(".receiver.PwdDBChangeReceiver");
//        LocalBroadcastManager.getInstance(this).registerReceiver(pwdDBChangeReceiver,filter);
    }


    public void mainBtn_onClick(View view) {
        int btn = view.getId();//获取按键的值
        if (btn == R.id.main_pwdBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Login_Activity.class);
            startActivity(intent);
        }
        if (btn == R.id.main_timeBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RentalMainActivity.class);
            startActivity(intent);
        }
    }

    private void dbInit() {
        //初始化数据库
        DBFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/" + getString(R.string.rentalFileName);
        RentDB.createCascadeDB(this, DBFilePath);
        Log.i(this.getLocalClassName(), "路径：" + DBFilePath);

        List<RoomDetails> rd = DbHelper.getInstance().getRoomDetailsToList();
        if (rd != null && rd.size() != 0) return;
        //当数据库空时，填充数据库内容
        InputStream        is = getResources().openRawResource(R.raw.db);
        DbHelper.ExcelData ed = DbHelper.getInstance().readExcel(is);
        RentDB.insertAll(ed.getPersonDetailsList());
        RentDB.insertAll(ed.getRentalRecordList());
        RentDB.insertAll(ed.getRoomDetailsList());
    }
}
