package my_manage.ui.rent_manage.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.deadline.statebutton.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.PersonDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/8 16:31
 * @Description :
 */
public final class DialogFragmentInsertPerson extends MyDialogFragment {
    @BindView(R.id.toolbar)                      Toolbar     toolbar;
    @BindView(R.id.rental_addPerson_companyName) EditText    rentalAddPersonCompanyName;
    @BindView(R.id.rental_addPerson_name)        EditText    rentalAddPersonName;
    @BindView(R.id.rental_addPerson_tel)         EditText    rentalAddPersonTel;
    @BindView(R.id.rental_addPerson_code)        EditText    rentalAddPersonCode;
    @BindView(R.id.rental_addPerson_remark)      EditText    rentalAddPersonRemark;
    @BindView(R.id.rental_addPerson_ok)          StateButton rentalAddPersonOk;
    private Context context;
    private IShowList iShowList;

    public DialogFragmentInsertPerson(Context context,IShowList iShowList) {
        this.context=context;
        this.iShowList=iShowList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_person_dialog, container, false);
        bind = ButterKnife.bind(this, v);

        toolbar.setTitle("增加租户信息");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        rentalAddPersonOk.setEnabled(false);
        return v;
    }

    @OnTextChanged({R.id.rental_addPerson_companyName, R.id.rental_addPerson_name, R.id.rental_addPerson_tel
                           , R.id.rental_addPerson_code, R.id.rental_addPerson_remark})
    void onTextChanged() {
        rentalAddPersonOk.setEnabled(true);
    }

    @OnClick(R.id.rental_addPerson_ok)
    void onClick() {
        if (StrUtils.isNotBlank(rentalAddPersonName.getText().toString())) {
            PersonDetails personDetails = new PersonDetails(rentalAddPersonName.getText().toString());
            personDetails.setCord(rentalAddPersonCode.getText().toString());
            personDetails.setTel(rentalAddPersonTel.getText().toString());
            personDetails.setOther(rentalAddPersonRemark.getText().toString());
            personDetails.setCompany(rentalAddPersonCompanyName.getText().toString());

            if (DbBase.insert(personDetails) > 0) {
                //成功，刷新
                iShowList.showList();
                dismiss();
            }
        } else {
            PageUtils.showMessage(context, "用户名不能为空");
        }
    }
}
