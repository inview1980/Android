package my_manage.password_box.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageViewPagerHome;
import my_manage.tool.PageUtils;

public class PasswordManageActivityListener {
    /**
     * 调用密码详情页面
     *
     * @param activity PasswordManageActivity类
     */
    public static void callPasswordManageItemDetails(PasswordManageActivity activity, int item) {
        Intent intent = new Intent(activity, PasswordManageViewPagerHome.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currentItem", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static <T extends Activity & IShowList> void resetDatabaseAndPassword(T activity) {
        String password = activity.getString(R.string.defaultPassword);

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_launcher_round);
        dialog.setTitle("重建资料和密码");
        dialog.setMessage("系统将把现有资料清空并重置密码，默认密码为:" + password);
        dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            if (PasswordDB.init().clearAndSave(password)) {
                Toast.makeText(activity,"重建资料和密码成功，新密码为：",Toast.LENGTH_SHORT).show();
                activity.showList();
            } else {
                Toast.makeText(activity,"重建资料和密码失败！",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton(R.string.cancel_cn, (dialog12, which) -> {
        });
        dialog.show();
    }

    public static <T extends Activity & IShowList> void deleteItem(T activity, int position) {
        //删除列表项...
        if (PasswordDB.init().getItems().remove(position) != null) {//这行代码必须有
            PasswordManageActivity.isDBChanged=true ;
            PageUtils.showMessage(activity,"删除成功");
            activity.showList();
        } else {
            PageUtils.showMessage(activity,"删除失败");
        }
    }
}
