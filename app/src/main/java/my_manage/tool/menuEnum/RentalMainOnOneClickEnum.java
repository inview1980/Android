package my_manage.tool.menuEnum;

import android.content.Intent;
import android.util.Log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.iface.IActivityMenu;
import my_manage.rent_manage.RentalMainActivity;
import my_manage.rent_manage.page.RentalForHouseActivity;

@AllArgsConstructor
@Getter
public enum RentalMainOnOneClickEnum implements IActivityMenu<RentalMainActivity> {
    Add(1, "增加房源") {
        @Override
        public void run(RentalMainActivity activity, int position) {
            Log.i(activity.getPackageName(), "增加房源");
            RentalMainItemLongClickEnum.Add.run(activity, null, -1);
        }
    },
    DeleteHouse(2, "显示已删除房源") {
        @Override
        public void run(RentalMainActivity activity, int position) {
            Intent intent = new Intent(activity, RentalForHouseActivity.class);
            intent.putExtra("title", this.getName());
            activity.startActivity(intent);
        }
    };
    private int index;
    private String name;
}
