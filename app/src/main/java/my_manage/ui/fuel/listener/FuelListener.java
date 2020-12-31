package my_manage.ui.fuel.listener;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import my_manage.iface.IShowList;
import my_manage.pojo.FuelRecord;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;

/**
 * @author inview
 * @Date 2020/11/24 9:25
 * @Description :
 */
public final class FuelListener {
    public static <T extends Activity & IShowList> void deleteFuelRecord(T activity, FuelRecord item) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(activity);
        dialog.setTitle("删除");
        dialog.setMessage("确定删除指定的加油记录？");
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if (DbHelper.getInstance().delFuelRecord(item.getPrimary_id())) {
                //刷新
                activity.showList();
                PageUtils.showMessage(activity, "删除记录成功");
            }
        }).show();
    }


    public static <T extends Activity & IShowList> void deleteAllFuelRecord(T activity) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(activity);
        dialog.setTitle("删除");
        dialog.setMessage("确定删除所有加油记录？");
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            if (DbHelper.getInstance().delAllFuelRecord(activity)) {
                //刷新
                activity.showList();
            PageUtils.showMessage(activity, "已删除所有加油记录！");
            }
        }).show();
    }


}
