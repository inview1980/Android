package my_manage.password_box.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;
import my_manage.password_box.R;
import my_manage.pojo.UserItem;
import my_manage.pojo.show.MenuData;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;
import my_manage.tool.enums.MenuTypesEnum;

@NoArgsConstructor
public final class PasswordManageItemDetailsFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.pwd_ItemDetailsId)      EditText       itemEdit;
    @BindView(R.id.pwd_AddressDetailsId)   EditText       addressEdit;
    @BindView(R.id.pwd_UserNameDetailsId)  EditText       userNameEdit;
    @BindView(R.id.pwd_PasswordDetailsId)  EditText       passwordEdit;
    @BindView(R.id.pwd_RemarkDetailsId)    EditText       remarkEdit;
    @BindView(R.id.pwd_ManageDet_Ok_BtnId) Button         okBtn;
    @BindView(R.id.types)                  Spinner        types;
    private                                UserItem       userItem;
    private                                Unbinder       bind;
    private                                boolean        isInited = false;
    private                                List<MenuData> passwordTypeList;

    public PasswordManageItemDetailsFragment(UserItem userItem,String title) {
        Bundle bundle = new Bundle();
        if (userItem != null) {
            bundle.putString("UserItem", JSON.toJSONString(userItem));
        }
        bundle.putString("title", title);
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.password_manage_item_details, container, false);
        bind = ButterKnife.bind(this, v);
        //初始化类型列表
        passwordTypeList = DbHelper.getInstance().getMenuTypes(getContext(),MenuTypesEnum.PasswordType);
        types.setAdapter(new CommonAdapter<MenuData>(getActivity(), android.R.layout.simple_list_item_1, passwordTypeList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, MenuData item, int position) {
                helper.setText(android.R.id.text1, item.getTitle());
            }
        });
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
            String title=getArguments().getString("title");
            if (userItem == null) {
                userItem = new UserItem();
                int i=passwordTypeList.stream().map(MenuData::getTitle).collect(Collectors.toList()).indexOf(title);
                types.setSelection(i);
            } else {
                for (int i = 0; i < passwordTypeList.size(); i++) {
                    if(passwordTypeList.get(i).getPrimary_id()==userItem.getTypeNameId()){
                        types.setSelection(i);
                        break;
                    }
                }
                itemEdit.setText(userItem.getItemName());
                addressEdit.setText(userItem.getAddress());
                userNameEdit.setText(userItem.getUserName());
                passwordEdit.setText(userItem.getPassword());
                remarkEdit.setText(userItem.getRemark());
            }
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
        userItem.setTypeNameId(passwordTypeList.get(types.getSelectedItemPosition()).getPrimary_id());
        if (DbHelper.getInstance().saveUserItem(userItem)) {
            PageUtils.showMessage(getContext(), "修改成功！");
        }

        getActivity().onBackPressed();
    }

}
