package my_manage.password_box.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageViewPagerHome;
import my_manage.password_box.pojo.UserItem;
import my_manage.rent_manage.listener.RentalMainActivityListener;
import my_manage.tool.ExcelUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;

public class PasswordManageActivityListener {
    /**
     * 调用密码详情页面
     *
     * @param activity PasswordManageActivity类
     */
    public static void callPasswordManageItemDetails(Activity activity, int item, String title) {
        Intent intent = new Intent(activity, PasswordManageViewPagerHome.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currentItem", item);
        bundle.putString("title", title);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static <T extends Activity & IShowList> void resetDatabaseAndPassword(T activity) {
        String              password = activity.getString(R.string.defaultPassword);
        final EditText      et       = new EditText(activity);
        AlertDialog.Builder dialog   = new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_launcher_round)
                .setView(et)
                .setTitle("重建资料和密码")
                .setMessage("系统将重置密码为:" + password+"\n请输入原密码：")
                .setCancelable(false)    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
                .setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
                    if (DbHelper.getInstance().resetPassword(activity, et.getText().toString(), null)) {
                        PageUtils.showMessage(activity, "重建资料和密码成功，新密码为：" + password);
                        activity.showList();
                    } else {
                        PageUtils.showMessage(activity, "重建资料和密码失败！");
                    }
                });
        dialog.show();
    }

    public static <T extends Activity & IShowList> void deleteItem(T activity, UserItem userItem) {
        //删除列表项...
        if (DbHelper.getInstance().delUserItem(userItem)) {//这行代码必须有
            PageUtils.showMessage(activity, "删除成功");
            activity.showList();
        } else {
            PageUtils.showMessage(activity, "删除失败");
        }
    }

    public static <T extends Activity & IShowList> void onOptionsItemSelected(T activity, int id,String title) {
        switch (id) {
            case R.id.add:
                //增加信息
                PasswordManageActivityListener.callPasswordManageItemDetails(activity, -1, title);
                break;
            case R.id.modify:
                //调用更改密码窗口
                activity.startActivity(new Intent(activity, my_manage.password_box.page.dialog.changePasswordDialog.class));
                break;
            case R.id.resetPassword:
                //重建资料和密码成功
                PasswordManageActivityListener.resetDatabaseAndPassword(activity);
                break;
            case R.id.saveDB:
                //输出到xls文件
                ExcelUtils.getInstance().saveDB(activity);
                break;
            case R.id.rebuildingDB:
                new RentalMainActivityListener().rebuildingDB(activity);
                break;
        }
    }
}
