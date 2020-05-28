package my_manage.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.deadline.statebutton.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import my_manage.password_box.R;
import my_manage.rent_manage.listener.PersonListener;
import my_manage.tool.database.DbBase;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

public final class PersonDetailsFramgent extends Fragment {
    @BindView(R.id.companyName) EditText        companyName;
    @BindView(R.id.name)        EditText        name;
    @BindView(R.id.codeNumber)  EditText        codeNumber;
    @BindView(R.id.tel)         EditText        tel;
    @BindView(R.id.remark)      EditText        remark;
    @BindView(R.id.modify)      StateButton     modify;
    @BindView(R.id.ok)          StateButton     ok;
    private                     ShowRoomDetails showRoomDetails;
    private                     Unbinder        bind;

    public PersonDetailsFramgent(ShowRoomDetails showRoomDetails) {
        if (showRoomDetails != null) {
            Bundle bundle = new Bundle();
            bundle.putString("ShowRoomDetails", JSON.toJSONString(showRoomDetails));
            setArguments(bundle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_show_person_details, container, false);
        bind = ButterKnife.bind(this, v);

        if (getArguments() != null) {
            String extra = getArguments().getString("ShowRoomDetails");
            if (StrUtils.isNotBlank(extra)) {
                showRoomDetails = StrUtils.isNotBlank(extra) ? JSON.parseObject(extra, ShowRoomDetails.class) : new ShowRoomDetails();
                setValue();
            }
        }

        return v;
    }

    private void setValue() {
        companyName.setText(showRoomDetails.getPersonDetails().getCompany());
        name.setText(showRoomDetails.getPersonDetails().getName());
        codeNumber.setText(showRoomDetails.getPersonDetails().getCord());
        tel.setText(showRoomDetails.getPersonDetails().getTel());
        remark.setText(showRoomDetails.getPersonDetails().getOther());
    }

    @OnClick(R.id.modify)
    void OnModifyClick() {
        PageUtils.setEnable(this, true);
        ok.setVisibility(View.VISIBLE);
        modify.setVisibility(View.GONE);
    }

    @OnClick(R.id.tel)
    void onTelClick() {
        PersonListener.telCall(getActivity(), tel.getText().toString());
    }

    @OnClick(R.id.ok)
    void OnOKClick() {
        if (showRoomDetails.getPersonDetails().getPrimary_id() != 0) {
            PersonDetails pd     = new PersonDetails();
            String        tmpStr = JSON.toJSONString(showRoomDetails.getPersonDetails());
            pd.setCompany(companyName.getText().toString());
            pd.setName(name.getText().toString());
            pd.setTel(tel.getText().toString());
            pd.setOther(remark.getText().toString());
            pd.setCord(codeNumber.getText().toString());
            pd.setPrimary_id(showRoomDetails.getPersonDetails().getPrimary_id());
            //判断有没有更改
            if (tmpStr.equalsIgnoreCase(JSON.toJSONString(pd)))
                return;
            if (DbBase.update(pd) > 0) {
                Toast.makeText(this.getContext(), "租户信息更改成功！", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }
}
