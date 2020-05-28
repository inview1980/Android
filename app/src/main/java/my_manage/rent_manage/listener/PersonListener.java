package my_manage.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.tool.database.DbBase;
import my_manage.rent_manage.page.ShowPersonExpandActivity;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.tool.StrUtils;

/**
 * 租户的新增、显示
 */
public final class PersonListener {
    public static void showPersonDetails(RentalMainActivity activity) {
        activity.startActivity(new Intent(activity, ShowPersonExpandActivity.class));
    }

    /**
     * 弹出增加租户的窗口，并将结果存入数据库
     */
    public static void addPerson(Activity activity, IShowList flushList) {
        ViewHolder viewHolder = new ViewHolder(R.layout.add_person_dialog);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, v) -> {
                    try {
                        if (v.getId() == R.id.rental_addPerson_ok) {
                            EditText companyName = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_companyName);
                            EditText name        = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_name);
                            EditText tel         = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_tel);
                            EditText code        = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_code);
                            EditText other       = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_remark);
                            if (StrUtils.isNotBlank(name.getText().toString())) {
                                PersonDetails personDetails = new PersonDetails(name.getText().toString());
                                personDetails.setCord(code.getText().toString());
                                personDetails.setTel(tel.getText().toString());
                                personDetails.setOther(other.getText().toString());
                                personDetails.setCompany(companyName.getText().toString());

                                if (DbBase.insert(personDetails) > 0) {
                                    //成功，刷新
                                    flushList.showList();
                                    dialog1.dismiss();
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }).setHeader(android.R.layout.browser_link_context_header)
                .setExpanded(true, 1500)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder)
                .create();
        dialog.show();
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
