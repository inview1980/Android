package my_manage.password_box.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;
import my_manage.password_box.R;
import my_manage.password_box.pojo.UserItem;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;

@NoArgsConstructor
public final class PasswordManageItemDetailsFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.pwd_ItemDetailsId)      EditText itemEdit;
    @BindView(R.id.pwd_AddressDetailsId)   EditText addressEdit;
    @BindView(R.id.pwd_UserNameDetailsId)  EditText userNameEdit;
    @BindView(R.id.pwd_PasswordDetailsId)  EditText passwordEdit;
    @BindView(R.id.pwd_RemarkDetailsId)    EditText remarkEdit;
    @BindView(R.id.pwd_ManageDet_Ok_BtnId) Button   okBtn;
    private                                UserItem userItem = new UserItem();
    private                                Unbinder bind;
    private                                boolean  isInited = false;

    public PasswordManageItemDetailsFragment(UserItem userItem) {
        Bundle bundle = new Bundle();
        if (userItem != null)
            bundle.putString("UserItem", JSON.toJSONString(userItem));
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.password_manage_item_details, container, false);
        bind = ButterKnife.bind(this, v);
        loadParameters();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }

    @OnTextChanged({R.id.pwd_ItemDetailsId, R.id.pwd_AddressDetailsId, R.id.pwd_UserNameDetailsId
                           , R.id.pwd_PasswordDetailsId, R.id.pwd_RemarkDetailsId})
    void onTextChanged() {
        if (isInited)
            okBtn.setEnabled(true);
    }


    /**
     * 读取上层窗口传递的参数
     */
    private void loadParameters() {
        try {
            if (getArguments() != null && getArguments().getString("UserItem") != null) {
                userItem = JSON.parseObject(getArguments().getString("UserItem"), UserItem.class);
            }
            if (userItem == null) userItem = new UserItem();
            itemEdit.setText(userItem.getItemName());
            addressEdit.setText(userItem.getAddress());
            userNameEdit.setText(userItem.getUserName());
            passwordEdit.setText(userItem.getPassword());
            remarkEdit.setText(userItem.getRemark());
            isInited = true;
        } catch (JSONException ignored) {
            userItem = new UserItem();
        }
    }

    @OnClick(R.id.pwd_ManageDet_Ok_BtnId)
    public void onClick(View view) {
        userItem.setItemName(itemEdit.getText().toString());
        userItem.setAddress(addressEdit.getText().toString());
        userItem.setUserName(userNameEdit.getText().toString());
        userItem.setPassword(passwordEdit.getText().toString());
        userItem.setRemark(remarkEdit.getText().toString());
        userItem.setSalt(StrUtils.getRandomString(getResources().getInteger(R.integer.defaultSaltLength)));
        if (DbHelper.getInstance().saveUserItem(userItem)) {
            PageUtils.showMessage(getContext(), "修改成功！");
        }

        getActivity().onBackPressed();
    }

}
