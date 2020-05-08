package my_manage.rent_manage.listener;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.page.ShowPersonExpandActivity;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

public final class PersonListener {
    public static void showPersonDetails(RentalMainActivity activity) {
        activity.startActivity(new Intent(activity, ShowPersonExpandActivity.class));
    }

    /**
     * 弹出增加租户的窗口，并将结果存入数据库
     *
     * @param activity
     */
    public static void addPerson(Activity activity, IShowList flushList) {
        ViewHolder viewHolder = new ViewHolder(R.layout.add_person_dialog);
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setOnClickListener((dialog1, v) -> {
                    try {
                        if (v.getId() == R.id.rental_addPerson_ok) {
                            EditText name  = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_name);
                            EditText tel   = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_tel);
                            EditText code  = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_code);
                            EditText other = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_remark);
                            name.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(activity, b));
                            tel.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(activity, b));
                            code.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(activity, b));
                            other.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(activity, b));
                            if (StrUtils.isNotBlank(name.getText().toString())) {
                                PersonDetails personDetails = new PersonDetails(name.getText().toString());
                                personDetails.setCord(code.getText().toString());
                                personDetails.setTel(tel.getText().toString());
                                personDetails.setOther(other.getText().toString());

                                if (RentDB.insert(personDetails) > 0) {
                                    //成功，刷新
                                    flushList.showList();
                                    dialog1.dismiss();
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }).setHeader(android.R.layout.browser_link_context_header)
                .setExpanded(true, 1300)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder)
                .create();
        dialog.show();
    }
}
