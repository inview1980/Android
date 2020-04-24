package my_manage.password_box.page.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

public final class changePasswordDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password_dialog);

    }

    public void cancel_OnClick(View view) {
//        setResult(RESULT_CANCELED);
        finish();
    }

    public void okBtn_onClick(View view) {
        EditText oldPWD = findViewById(R.id.dialog_changePWDold);
        EditText newPWD = findViewById(R.id.dialog_changePWD_new);
        EditText new2PWD = findViewById(R.id.dialog_changePWD_Two);

        String oldStr=oldPWD.getText().toString().trim();
        String newStr=newPWD.getText().toString().trim();
        String new2Str=new2PWD.getText().toString().trim();
        if (StrUtils.isAnyBlank(oldStr, newStr, new2Str)) {
            showMessage("输入的内容不能有空");
            return;
        }
        if(oldStr.equals(newStr)){
            showMessage("新密码和旧密码一样，没有更改！");
            return;
        }
        if (!newStr.equalsIgnoreCase(new2Str)) {
            showMessage("新密码不致！");
            return;
        }


        if (PasswordDB.init().changePassword(oldStr,new2Str)) {
            showMessage("更改密码成功！");
        }else {
            showMessage("更改密码失败！");
        }
        finish();
    }

    private void showMessage(String msg) {
        PageUtils.showMessage(getBaseContext(),"ChangePwdDialog",msg);
    }
}
