package my_manage.ui.fuel.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import my_manage.iface.IShowList;
import my_manage.pojo.FuelRecord;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.fuel.page.AddFuelRecordActivity;
import my_manage.ui.fuel.page.DataAnalysisShowActivity;
import my_manage.ui.fuel.page.FuelRecordMainActivity;
import my_manage.ui.password_box.R;

/**
 * @author inview
 * @Date 2020/11/24 9:25
 * @Description :
 */
public final class FuelListener {
    public static <T extends Activity & IShowList> void deleteFuelRecord(T activity, FuelRecord item) {
        Button     button = getButton(activity);
        DialogPlus dialog = dialogShow(activity, button);
        button.setOnClickListener(view -> {
            if (DbHelper.getInstance().delFuelRecord(item.getPrimary_id())) {
                //刷新
                activity.showList();
                PageUtils.showMessage(activity, "删除记录成功");
                dialog.dismiss();
            }
        });
    }

    public static <T extends Activity & IShowList> void addFuelRecordCallActivity(T activity) {
        activity.startActivityForResult(new Intent(activity, AddFuelRecordActivity.class), 2020);
    }

    public static boolean addFuelRecord( FuelRecord fr) {
        return DbBase.insert(fr)>0;
    }

    public static <T extends Activity & IShowList> void deleteAllFuelRecord(T activity) {
        Button     button = getButton(activity);
        DialogPlus dialog = dialogShow(activity, button);
        button.setOnClickListener(view -> {
            DbHelper.getInstance().delAllFuelRecord(activity);
            //刷新
            PageUtils.showMessage(activity, "已删除所有加油记录！");
            activity.showList();
            dialog.dismiss();
        });
    }

    public static <T extends Activity & IShowList> void modifyFuelRecordCallActivity(T activity, FuelRecord item) {
        Intent intent = new Intent(activity, AddFuelRecordActivity.class);
        intent.putExtra("id", item.getPrimary_id());
        activity.startActivityForResult(intent, 2020);
    }

    public static boolean modifyFuelRecord(FuelRecord fr){
        return DbBase.update(fr)>0;
    }

    /**
     * 数据分析
     * @param activity
     * @param <T>
     */
    public static <T extends Activity & IShowList> void  dataAnalysis(T activity) {
        // TODO: 2020/11/24  数据分析

        Intent intent=new Intent(activity, DataAnalysisShowActivity.class);
        activity.startActivityForResult(intent,2020);
    }

    private static <T extends Activity & IShowList> DialogPlus dialogShow(T activity, View view) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(view))
                .setExpanded(true, 200)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
        return dialog;
    }

    private static <T extends Activity & IShowList> Button getButton(T activity) {
        Button button = new Button(activity);
        button.setText("确定");
        button.setBackgroundColor(activity.getColor(R.color.blue));
        return button;
    }
}
