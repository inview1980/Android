package my_manage.password_box.menuEnum;

import android.util.Log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.password_box.iface.IActivityMenu;
import my_manage.rent_manage.RentalMainActivity;

@AllArgsConstructor
@Getter
public enum RentalMainOnOneClickEnum implements IActivityMenu<RentalMainActivity> {
    Add(1, "增加房源") {
        @Override
        public void run(RentalMainActivity activity, int position) {
            Log.i(activity.getPackageName(), "增加房源");
            RentalMainItemLongClickEnum.Add.run(activity, null, -1);
        }
    };
//    DeleteAll(2, "删除所有房源") {
//        @Override
//        public void run(RentalMainActivity activity, int position) {
//            Log.i(activity.getPackageName(), "删除所有房源");
//
//        }
//    };
    private int index;
    private String name;
}
