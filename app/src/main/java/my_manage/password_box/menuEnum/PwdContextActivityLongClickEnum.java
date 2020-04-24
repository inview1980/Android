package my_manage.password_box.menuEnum;

import my_manage.password_box.iface.IActivityMenu;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.tool.PageUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PasswordManageActivity类的菜单和菜单处理事件
 */
@AllArgsConstructor@Getter
public enum PwdContextActivityLongClickEnum implements IActivityMenu<PasswordManageActivity> {
    Edit(1, "编辑") {
        @Override
        public void run(PasswordManageActivity activity, int position) {
            if (position < 0 || position > PasswordDB.init().getItems().size()) return;
            PageUtils.callPasswordManageItemDetails(activity, position);
        }
    },
    Delete(2, "删除") {
        @Override
        public void run(PasswordManageActivity activity, int position) {
            //删除列表项...
            if (PasswordDB.init().getItems().remove(position) != null) {//这行代码必须有
               PasswordManageActivity.isDBChanged=true ;
                activity.showMessage("删除成功");
            } else {
                activity.showMessage("删除失败");
            }
            activity.showLst();
            activity.showMessage("删除此项");
        }
    };

    private int index;
    private String name;
}
