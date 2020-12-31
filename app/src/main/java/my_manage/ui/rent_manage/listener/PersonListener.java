package my_manage.ui.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import my_manage.tool.StrUtils;
import my_manage.ui.rent_manage.RentalMainActivity;
import my_manage.ui.rent_manage.page.ShowPersonExpandActivity;

/**
 * 租户的新增、显示
 */
public final class PersonListener {
    public static void showPersonDetails(RentalMainActivity activity) {
        activity.startActivity(new Intent(activity, ShowPersonExpandActivity.class));
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param tel 电话号码
     */
    public static void telCall(Activity activity, String tel) {
        if (StrUtils.isBlank(tel)) return;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri    data   = Uri.parse("tel:" + tel);
        intent.setData(data);
        activity.startActivity(intent);
    }
}
