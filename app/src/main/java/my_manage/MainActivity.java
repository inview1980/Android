package my_manage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.view.menu.MenuBuilder;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import my_manage.password_box.R;
import my_manage.password_box.page.dialog.Login_Activity;
import my_manage.rent_manage.MyService;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.tool.ContentUriUtil;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
    public static String   DBFilePath;
    private       TextView versionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据库
        dbInit();

        versionTxt = findViewById(R.id.version);
        versionTxt.setText("版本号:" + getVersionName(this));
        versionTxt.setOnLongClickListener(this);

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("path", DBFilePath);
        startService(intent);
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
        DBFilePath = getFilesDir().getAbsolutePath() + "/" + getString(R.string.rentalFileName);
        DbBase.createCascadeDB(this, DBFilePath);
        //初始化系统参数
        PageUtils.isApkInDebug(this);
//        DbHelper.getInstance().dbInit(this, DBFilePath);
    }

    public String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String         name    = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }
        return name;
    }

    @Override
    public boolean onLongClick(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.main_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        menu.show();
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.loadDefault2DB:
                DbHelper.getInstance().loadDefault2DB(this);
                break;
            case R.id.loadFile2DB:
                loadFile();
                break;
            default:
                break;
        }
        return false;
    }

    private void loadFile() {
        Intent i2 = new Intent(getApplicationContext(), FileChooser.class);
        i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
        startActivityForResult(i2, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 111 && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {//只有一个文件咯
                Uri    uri  = data.getData(); // 获取用户选择文件的URI
                String path = ContentUriUtil.getPath(this, uri);

                PageUtils.Log("选择的文件路径：" + path);
                DbHelper.getInstance().loadFile2DB(path);

                PageUtils.Log("读取指定文件并写入数据库成功");
            }

        }
    }
}
