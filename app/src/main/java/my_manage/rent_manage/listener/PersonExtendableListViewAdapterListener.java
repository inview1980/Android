package my_manage.rent_manage.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.rent_manage.page.ShowPersonDetailsActivity;
import my_manage.rent_manage.pojo.PersonDetails;

public final class PersonExtendableListViewAdapterListener {
    public <T extends Activity & IShowList> void onClick(T activity, List<PersonDetails> data, int position, View v) {
        if (v.getId() == R.id.rental_person_expandable_details) {
            showPersonDetails(activity, data.get(position).getPrimary_id());
        } else if (v.getId() == R.id.rental_person_expandable_delete) {
            delPerson(activity, data.get(position).getPrimary_id());
        } else if (v.getId() == R.id.rental_person_expandable_addperson) {
            PersonListener.addPerson(activity, activity);
        }
        activity.showList();
    }

    private <T extends Activity & IShowList> void delPerson(T activity, int primary_id) {
        if (primary_id == 0) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("删除").setMessage("确定要删除此租户的一切资料吗？");
        dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
            if (DbHelper.getInstance().delPersonById(primary_id) > 0) {
                activity.showList();
                Toast.makeText(activity, "删除租户资料成功！", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    /**
     * 弹出租户具体内容的窗口
     */
    public static <T extends Activity & IShowList> void showPersonDetails(T activity, int manId) {
        Intent                intent  = new Intent(activity, ShowPersonDetailsActivity.class);
        intent.putExtra("manId",manId);
        activity.startActivity(intent);
    }
}