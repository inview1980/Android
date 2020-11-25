package my_manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;

import my_manage.tool.ExcelUtils;
import my_manage.tool.http.HttpUtils;
import my_manage.ui.password_box.R;
import my_manage.ui.password_box.page.DownloadActivity;
import my_manage.ui.password_box.page.PasswordManageTotalActivity;
import my_manage.ui.rent_manage.RentalMainActivity;
import my_manage.tool.ContentUriUtil;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.fuel.page.FuelRecordMainActivity;
import web.WebResult;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
    private       TextView versionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        versionTxt = findViewById(R.id.version);
        versionTxt.setText("版本号:" + getVersionName(this));
        versionTxt.setOnLongClickListener(this);
//        val ss=FontUtils.getInstance().getSpannableString(versionTxt,"&#xe64e;加载默认数据到数据库");
//        ss.setSpan(new RelativeSizeSpan(1.5f),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        versionTxt.setText(ss);

    }


    public void mainBtn_onClick(View view) {
        int btn = view.getId();//获取按键的值
        if (btn == R.id.main_pwdBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, PasswordManageTotalActivity.class);
            startActivity(intent);
        }

        if (btn == R.id.main_timeBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RentalMainActivity.class);
            startActivity(intent);
        }

        if(btn==R.id.refuel_record_Btn){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FuelRecordMainActivity.class);
            startActivity(intent);
        }
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
                if (DbHelper.getInstance().loadDefault2DB(this))
                    PageUtils.showMessage(this, "读取默认数据库成功");
                else
                    PageUtils.showMessage(this, "读取默认数据库失败");
                break;
            case R.id.loadFile2DB:
                loadFile();
                break;
            case R.id.downloadDB:
                startActivity(new Intent(this, DownloadActivity.class));
                break;
            case R.id.updateDB:
                updateDB();
//                startActivity(new Intent(this, my_manage.ui.common.Login_Activity.class));
                break;
            default:
                break;
        }
        return false;
    }

    private void updateDB() {
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setContentHolder(new ViewHolder(new ProgressBar(this)))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(R.color.grayBlue)
                .setOnDismissListener(dialog1 -> {
                    OkGo.getInstance().cancelAll();
                })
                .create();
        dialog.show();
        //先测试是否已登录
        HttpUtils.post(this, "/isLogged", null, result -> {
            if (result.getState() == WebResult.OK) {
                //先将数据库保存到xls文件，成功后再上传
                if (ExcelUtils.getInstance().saveDB(this, false)) {
                    HttpParams params = new HttpParams("saveFile", new File(ExcelUtils.getInstance().outFilePath));
                    HttpUtils.post(this, "/info/updateDB", params,
                            webResult -> PageUtils.showMessage(this, webResult.getDetails()), null,dialog);
                }
            }
        }, null,dialog);
    }

    private void loadFile() {
        String path = getExternalFilesDir(null).getAbsolutePath()
                + "/" + getString(R.string.rentalFileNameBackup) + "."
                + getString(R.string.extensionName);
        File   file   = new File(path);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(this, "my_manage.ui.password_box.fileprovider", file);
            intent.setDataAndType(uri, "*/*");
        } else
            intent.setType("*/*");//文件类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String title = "请选择一个指定后缀名的数据库文件：" + getString(R.string.extensionName);
        startActivityForResult(Intent.createChooser(intent, title), 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 111 && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {//只有一个文件咯
                Uri    uri  = data.getData(); // 获取用户选择文件的URI
                String path = ContentUriUtil.getPath(this, uri);
                //排除非本应用的指定后缀名的文件
                if (null != path && !path.toLowerCase().endsWith(getString(R.string.extensionName).toLowerCase())) {
                    PageUtils.showMessage(this, "请选择指定后缀名的文件");
                    return;
                }
                if (DbHelper.getInstance().loadFile2DB(path)) {
                    PageUtils.showMessage(this, "读取指定文件并写入数据库成功");
                } else
                    PageUtils.showMessage(this, "读取指定失败");
            }
        }
        if (requestCode == 8080 && resultCode == 8080) {
            //登录成功后
            updateDB();
        }
    }
}
