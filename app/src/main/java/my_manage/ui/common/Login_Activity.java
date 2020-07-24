package my_manage.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.deadline.statebutton.StateButton;
import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import my_manage.tool.http.HttpUtils;
import my_manage.tool.PageUtils;
import my_manage.ui.password_box.R;
import my_manage.ui.widght.ParallaxSwipeBackActivity;
import tools.SecretUtils;
import web.WebResult;


public class Login_Activity extends ParallaxSwipeBackActivity {

    @BindView(R.id.toolbar)   Toolbar          toolbar;
    @BindView(R.id.username)  EditText         username;
    @BindView(R.id.password)  EditText         password;
    @BindView(R.id.login)     StateButton      login;
    @BindView(R.id.loading)   ProgressBar      loading;
    @BindView(R.id.container) ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        toolbar.setTitle("登录服务器");
    }

    @OnEditorAction(R.id.password)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClick(null);
        }
        return false;
    }

    @OnTextChanged({R.id.username, R.id.password})
    void afterTextChanged() {
        String psd = password.getText().toString();
        login.setEnabled(psd.trim().length() > 0);
    }

    @OnClick(R.id.login)
    public void onClick(View view) {
        login.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        userLogin();
    }

    @SuppressLint("CheckResult")
    private void userLogin() {
        String     userName   = username.getText().toString();
        String     pwd        = password.getText().toString();
        HttpParams httpParams = new HttpParams("username", userName);
        httpParams.put("password", SecretUtils.getPasswordEncryptString(pwd));
        HttpUtils.post(this, "/login", httpParams, webResult -> {
            if (webResult.getState() == WebResult.OK) {
                PageUtils.Log(String.valueOf(webResult.getDetails()));
                setResult(8080);
                finish();
            } else if (webResult.getState() == WebResult.USER_NULL) {
                PageUtils.showMessage(this,webResult.getDetails());
                username.setSelected(true);
            } else if (webResult.getState() == WebResult.PASSWORD_ERROR) {
                PageUtils.showMessage(this,webResult.getDetails());
                password.setSelected(true);
            }
            loading.setVisibility(View.GONE);
            login.setEnabled(true);
        });
    }

    @Override
    public void showList() {

    }
}