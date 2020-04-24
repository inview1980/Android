package my_manage.password_box.page;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONException;
import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.pojo.UserItem;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class PasswordManageItemDetailsFragment extends Fragment implements View.OnClickListener {
    private UserItem userItem;
    private EditText itemEdit;
    private EditText addressEdit;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText remarkEdit;
    private Button okBtn;
    private int currentItem;

    public PasswordManageItemDetailsFragment(int currentItem) {
        Bundle bundle = new Bundle();
        bundle.putInt("currentItem", currentItem);
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.password_manage_item_details, container, false);
        init(v);
        return v;
    }


    private void init(View v) {
        itemEdit = v.findViewById(R.id.pwd_ItemDetailsId);
        addressEdit = v.findViewById(R.id.pwd_AddressDetailsId);
        userNameEdit = v.findViewById(R.id.pwd_UserNameDetailsId);
        passwordEdit = v.findViewById(R.id.pwd_PasswordDetailsId);
        remarkEdit = v.findViewById(R.id.pwd_RemarkDetailsId);
        okBtn=v.findViewById(R.id.pwd_ManageDet_Ok_BtnId);
        okBtn.setOnClickListener(this);
        v.findViewById(R.id.pwd_ManageDet_Cancel_BtnId).setOnClickListener(this);

        loadParameters();
        //增加编辑事件
        itemEdit.addTextChangedListener(new myTextWatch());
        addressEdit.addTextChangedListener(new myTextWatch());
        userNameEdit.addTextChangedListener(new myTextWatch());
        passwordEdit.addTextChangedListener(new myTextWatch());
        remarkEdit.addTextChangedListener(new myTextWatch());

    }


    /**
     * 读取上层窗口传递的参数
     */
    private void loadParameters() {
        try {
            if (getArguments() != null) {
                currentItem = getArguments().getInt("currentItem");
                userItem = (currentItem == -1) ? new UserItem() : PasswordDB.init().getItems().get(currentItem);
            }
            itemEdit.setText(userItem.getItemName());
            addressEdit.setText(userItem.getAddress());
            userNameEdit.setText(userItem.getUserName());
            passwordEdit.setText(userItem.getPassword());
            remarkEdit.setText(userItem.getRemark());
        } catch (JSONException ignored) {
            userItem = new UserItem();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pwd_ManageDet_Ok_BtnId) {//确定按键
            String ie = itemEdit.getText().toString();
            String ae = addressEdit.getText().toString();
            String ue = userNameEdit.getText().toString();
            String pe = passwordEdit.getText().toString();
            String re = remarkEdit.getText().toString();
            if (ie.equals(userItem.getItemName()) && ae.equals(userItem.getAddress()) && ue.equals(userItem.getUserName())
                    && pe.equals(userItem.getPassword()) && re.equals(userItem.getRemark())) {
                System.out.println("未修改");
                //无修改，按取消处理
            } else {
                userItem.setItemName(itemEdit.getText().toString());
                userItem.setAddress(addressEdit.getText().toString());
                userItem.setUserName(userNameEdit.getText().toString());
                userItem.setPassword(passwordEdit.getText().toString());
                userItem.setRemark(remarkEdit.getText().toString());



                if (currentItem == -1) {
                    PasswordDB.init().getItems().add(userItem);
                } else if (PasswordDB.init().getItems().size() >= currentItem) {
                    PasswordDB.init().getItems().set(currentItem, userItem);
                }
                PasswordManageActivity.isDBChanged=true;
            }
        }
        getActivity().onBackPressed();
    }


    class myTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            okBtn.setEnabled(true);
//            System.out.println("修改后事件:");
        }
    }
}
