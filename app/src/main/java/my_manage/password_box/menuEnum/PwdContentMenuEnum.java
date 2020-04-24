package my_manage.password_box.menuEnum;

import android.content.Intent;

import my_manage.password_box.iface.IActivityMenu;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.tool.PageUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PwdContentMenuEnum implements IActivityMenu<PasswordManageActivity> {
    Add(1, "增加") {
        @Override
        public void run(PasswordManageActivity activity, int position) {
            PageUtils.callPasswordManageItemDetails(activity, -1);
        }
    },
    ModifyPassword(3, "修改数据库密码") {
        @Override
        public void run(PasswordManageActivity activity, int position) {
            //调用更改密码窗口
            Intent intent = new Intent(activity, my_manage.password_box.page.dialog.changePasswordDialog.class);
            activity.startActivity(intent);
        }
    },
    ResetPassword(4, "重建资料和密码") {
        @Override
        public void run(PasswordManageActivity activity, int position) {
            PageUtils.resetDatabaseAndPassword(activity);
            activity.showLst();
        }
    };

    private int index;
    private String name;
}
