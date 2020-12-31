package my_manage.ui.common;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import my_manage.password_box.R;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

public final class changePasswordDialog extends MyBaseSwipeBackActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password_dialog);
        setTitle("修改密码");
    }


    public void okBtn_onClick(View view) {
        EditText oldPWD = findViewById(R.id.dialog_changePWDold);
        EditText newPWD = findViewById(R.id.dialog_changePWD_new);
        EditText new2PWD = findViewById(R.id.dialog_changePWD_Two);

        String oldStr=oldPWD.getText().toString().trim();
        String newStr=newPWD.getText().toString().trim();
        String new2Str=new2PWD.getText().toString().trim();
        if (StrUtils.isAnyBlank(oldStr, newStr, new2Str)) {
            PageUtils.showMessage(getBaseContext(),"输入的内容不能有空");
            return;
        }
        if(oldStr.equals(newStr)){
            PageUtils.showMessage(getBaseContext(),"新密码和旧密码一样，没有更改！");
            return;
        }
        if (!newStr.equalsIgnoreCase(new2Str)) {
            PageUtils.showMessage(getBaseContext(),"新密码不致！");
            return;
        }


        if (DbHelper.getInstance().changePassword(this,oldStr,new2Str)) {
            PageUtils.showMessage(getBaseContext(),"更改密码成功！");
        }else {
            PageUtils.showMessage(getBaseContext(),"更改密码失败！");
        }
        finish();
    }

    @Override
    public void showList() {

    }
}
