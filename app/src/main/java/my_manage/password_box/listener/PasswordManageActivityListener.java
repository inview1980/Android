package my_manage.password_box.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageViewPagerHome;
import my_manage.password_box.pojo.UserItem;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;

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
        dialog.setMessage("系统将重置密码，默认密码为:" + password);
        dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            if (DbHelper.getInstance().resetPassword(activity,null)) {
                PageUtils.showMessage(activity,"重建资料和密码成功，新密码为："+password);
                activity.showList();
            } else {
                PageUtils.showMessage(activity,"重建资料和密码失败！");
            }
        });
        dialog.setNegativeButton(R.string.cancel_cn, (dialog12, which) -> {
        });
        dialog.show();
    }

    public static <T extends Activity & IShowList> void deleteItem(T activity, UserItem userItem) {
        //删除列表项...
        if (DbHelper.getInstance().delUserItem(userItem)) {//这行代码必须有
            PageUtils.showMessage(activity,"删除成功");
            activity.showList();
        } else {
            PageUtils.showMessage(activity,"删除失败");
        }
    }
}
