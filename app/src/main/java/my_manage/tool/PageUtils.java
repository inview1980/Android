package my_manage.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageViewPagerHome;

public final class PageUtils {
    public static void resetDatabaseAndPassword(Context context) {
        String password =context. getString(R.string.defaultPassword);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(R.mipmap.ic_launcher_round);
        dialog.setTitle("重建资料和密码");
        dialog.setMessage("系统将把现有资料清空并重置密码，默认密码为:" + password);
        dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            if (PasswordDB.init().clearAndSave(password)) {
                showMessage( context,"重建资料和密码成功，新密码为：" + password);
            } else {
                showMessage(context,"重建资料和密码失败！");
            }
        });
        dialog.setNegativeButton(R.string.cancel_cn, (dialog12, which) -> {
        });
        dialog.show();
    }

    private static void showMessage(Context context, String msg) {
        showMessage(context,"PageUtils",msg);
    }

    public static void showMessage(Context context, String tag, String msg) {
        Log.i( tag,msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 调用密码详情页面
     * @param activity PasswordManageActivity类
     */
    public static void callPasswordManageItemDetails(PasswordManageActivity activity, int item) {
        Intent intent = new Intent(activity, PasswordManageViewPagerHome.class);
//        String parameter = JSON.toJSONString(item);
        Bundle bundle=new Bundle();
        bundle.putInt("currentItem", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        //startActivityForResult(intent, activity.PASSWORD_MANAGE_ITEM_DETAILS_ACTIVITY_CODE);
    }

    public static void closeInput(Activity activity,boolean b){
        if (!b) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())//软键盘如果打开就关闭
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
