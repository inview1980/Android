package my_manage.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import lombok.Getter;
import my_manage.adapter.ManAdapter;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.listener.NewRoomFragmentLister;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

/**
 * 房屋出租详情
 */
@Getter
public final class NewRoomFragment extends Fragment {
    @BindView(R.id.rental_editRoom_community)             AutoCompleteTextView community;
    @BindView(R.id.rental_editRoom_roomNumber)            EditText             roomNumber;
    @BindView(R.id.rental_editRoom_realtyMoney)           EditText             rentalMoney;//月租金
    @BindView(R.id.rental_editRoom_meterNumber)           EditText             meterNumber;
    @BindView(R.id.rental_editRoom_area)                  EditText             area;
    @BindView(R.id.rental_editRoom_propertyPrice)         EditText             propertyPrice;//物业费单价
    @BindView(R.id.rental_editRoom_person)                Spinner              person;
    @BindView(R.id.rental_editRoom_tel)                   EditText             tel;
    @BindView(R.id.rental_editRoom_manCardNumber)         EditText             manCard;
    @BindView(R.id.rental_editRoom_isContainRealty)       CheckBox             isContainRealty;
    @BindView(R.id.rental_editRoom_beginDate)             TextView             beginDate;//房租开始时间
    @BindView(R.id.rental_editRoom_rentalMonth)           EditText             rentalMonth;//房租结束时间
    @BindView(R.id.rental_editRoom_propertyBeginDate)     TextView             proBeginDate;//物业费开始时间
    @BindView(R.id.rental_editRoom_payDate)               TextView             payDate;//交费时间
    @BindView(R.id.rental_editRoom_rentalEndDate)         TextView             rentalEndDate;
    @BindView(R.id.rental_editRoom_property_costsEndDate) TextView             proEndDate;
    @BindView(R.id.rental_editRoom_remark)                EditText             remark;//备注
    @BindView(R.id.rental_editRoom_propertyMonth)         EditText             proMonth;
    @BindView(R.id.rental_editRoom_changeBtn)             Button               changeBtn;
    @BindView(R.id.rental_editRoom_okBtn)                 Button               okBtn;
    @BindView(R.id.rental_editRoom_add)                   Button               addBtn;
    @BindView(R.id.rental_editRoom_cancelBtn)             Button               cancelBtn;
    @BindView(R.id.rental_editRoom_deposit)               EditText             deposit;//押金
    @BindView(R.id.rental_editRoom_contract_BeginDate)    TextView             contractBeginDate;//合同开始时间
    @BindView(R.id.rental_editRoom_Contract_EndDate)      TextView             contractEndDate;//合同结束时间
    @BindView(R.id.rental_editRoom_contract_Month)        EditText             contractMonth;//合同时长，月

    private ShowRoomDetails       showRoomDetails;
    private List<PersonDetails>   personDetailsList;
    private Unbinder              bind;
    private NewRoomFragmentLister lister;


    public NewRoomFragment(ShowRoomDetails showRoomDetails, boolean isHistory) {
        if (showRoomDetails != null) {
            Bundle bundle = new Bundle();
            bundle.putString("ShowRoomDetails", JSON.toJSONString(showRoomDetails));
            bundle.putBoolean("isHistory", isHistory);
            setArguments(bundle);
        }
    }

    @OnFocusChange({R.id.rental_editRoom_community, R.id.rental_editRoom_roomNumber, R.id.rental_editRoom_rentalMonth,
                           R.id.rental_editRoom_propertyPrice, R.id.rental_editRoom_meterNumber, R.id.rental_editRoom_area,
                           R.id.rental_editRoom_tel, R.id.rental_editRoom_manCardNumber, R.id.rental_editRoom_remark})
    void OnFocusChangeListener(View v, boolean b) {
        PageUtils.closeInput(getActivity(), b);
    }

    /**
     * 设置当失去焦点时，刷新相对应的日期
     */
    @OnFocusChange({R.id.rental_editRoom_rentalMonth, R.id.rental_editRoom_propertyMonth, R.id.rental_editRoom_contract_Month})
    void onFocusChange() {
        if (showRoomDetails.getRentalRecord() == null) return;
        changeDateLable(rentalMonth, this.beginDate, rentalEndDate);
        changeDateLable(proMonth, this.proBeginDate, proEndDate);
        changeDateLable(contractMonth, this.contractBeginDate, contractEndDate);
    }

    /**
     * 自动注册点击事件
     */
    @OnClick({R.id.rental_editRoom_changeBtn, R.id.rental_editRoom_okBtn, R.id.rental_editRoom_cancelBtn,
                     R.id.rental_editRoom_beginDate, R.id.rental_editRoom_propertyBeginDate, R.id.rental_editRoom_payDate,
                     R.id.rental_editRoom_add})
    void OnClick(View view) {
        lister.onClick(view);
    }

    /**
     * 自动注册EditView中文本变化事件
     */
    @OnTextChanged({R.id.rental_editRoom_community, R.id.rental_editRoom_rentalMonth, R.id.rental_editRoom_realtyMoney,
                           R.id.rental_editRoom_propertyPrice, R.id.rental_editRoom_meterNumber, R.id.rental_editRoom_area,
                           R.id.rental_editRoom_tel, R.id.rental_editRoom_manCardNumber, R.id.rental_editRoom_propertyMonth,
                            R.id.rental_editRoom_remark, R.id.rental_editRoom_contract_Month})
    void OnTextChanged() {
        lister.afterTextChanged(null);
    }

    private void init(View v) {
        initSpinner();

        isContainRealty.setOnCheckedChangeListener((compoundButton, b) -> lister.afterTextChanged(null));
        person.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (okBtn.getVisibility() == View.VISIBLE) {
                    lister.afterTextChanged(null);
                    PersonDetails pd = personDetailsList.get(i);
                    setValueForPerson(pd);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void changeDateLable(EditText input, TextView showTxt, TextView textView) {
        try {
            if (StrUtils.isNotBlank(showTxt.getText().toString())) {
                int      i1 = Integer.parseInt(input.getText().toString());
                String[] s1 = showTxt.getText().toString().split("-");
                if (s1.length < 3) return;
                Calendar renD = Calendar.getInstance();
                renD.set(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]) - 1, Integer.parseInt(s1[2]));
                renD.add(Calendar.MONTH, i1);
                renD.add(Calendar.DAY_OF_YEAR, -1);
                textView.setText(renD.get(Calendar.YEAR) + "-" + (renD.get(Calendar.MONTH) + 1) + "-" + renD.get(Calendar.DAY_OF_MONTH));
            }
        } catch (NumberFormatException e) {
        }
    }

    public void initSpinner() {
        this.personDetailsList = DbHelper.getInstance().getPersonList();
        ManAdapter adapter = new ManAdapter(getActivity(), android.R.layout.simple_list_item_1, this.personDetailsList);
        person.setAdapter(adapter);
    }

    public void setEnable(boolean b) {
        community.setEnabled(b);
        roomNumber.setEnabled(b);
        rentalMoney.setEnabled(b);
        propertyPrice.setEnabled(b);
        meterNumber.setEnabled(b);
        area.setEnabled(b);
        person.setEnabled(b);
        tel.setEnabled(b);
        manCard.setEnabled(b);
        isContainRealty.setEnabled(b);
        beginDate.setEnabled(b);
        rentalMonth.setEnabled(b);
        proBeginDate.setEnabled(b);
        proMonth.setEnabled(b);
        payDate.setEnabled(b);
        remark.setEnabled(b);
        deposit.setEnabled(b);
        contractMonth.setEnabled(b);
        contractBeginDate.setEnabled(b);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rental_room_details, container, false);
        bind = ButterKnife.bind(this, v);
        //初始化事件监听器
        lister = new NewRoomFragmentLister(this, v);

        init(v);
        //读取传递的参数
        try {
            if (getArguments() != null) {
                boolean isHistory = getArguments().getBoolean("isHistory");
                if (isHistory) {
                    //显示历史记录，隐藏修改按钮
                    changeBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.GONE);
                } else {
                    changeBtn.setVisibility(View.VISIBLE);
                }

                String extra = getArguments().getString("ShowRoomDetails");
                if (StrUtils.isNotBlank(extra)) {
                    ShowRoomDetails room = JSON.parseObject(extra, ShowRoomDetails.class);
                    if (room != null) {
                        setEnable(false);
                        setValue(room);
                        this.showRoomDetails = room;
                    }
                }
            } else {
                //新增
                setEnable(true);
                okBtn.setVisibility(View.VISIBLE);
            }
        } catch (JSONException ignored) {
            //新增
            setEnable(true);
            okBtn.setVisibility(View.VISIBLE);
        }
        return v;
    }

    private void setValue(ShowRoomDetails room) {
        community.setText(room.getCommunityName());
        roomNumber.setText(room.getRoomNumber());
        rentalMoney.setText("" + room.getRentalMoney());
        propertyPrice.setText("" + room.getPropertyPrice());
        meterNumber.setText(room.getMeterNumber());
        area.setText("" + room.getRoomArea());

        setValueForPerson(room.getPersonDetails());

        if (room.getRentalRecord() != null) {
            isContainRealty.setChecked(room.getRentalRecord().getIsContainRealty());
            beginDate.setText(date2String(room.getRentalRecord().getStartDate()));
            rentalMonth.setText(room.getRentalRecord().getPayMonth() + "");
            proBeginDate.setText(date2String(room.getRentalRecord().getRealtyStartDate()));
            proMonth.setText(room.getRentalRecord().getRealtyMonth() + "");
            payDate.setText(date2String(room.getRentalRecord().getPaymentDate()));
            remark.setText(room.getRentalRecord().getRemarks());
            rentalEndDate.setText(date2String(room.getRentalEndDate()));
            proEndDate.setText(date2String(room.getPropertyCostEndDate()));
            deposit.setText(room.getRentalRecord().getDeposit() + "");
            contractBeginDate.setText(date2String(room.getRentalRecord().getContractSigningDate()));
            contractMonth.setText(room.getRentalRecord().getContractMonth() + "");
            contractEndDate.setText(date2String(room.getContractEndDate()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }

    private void setValueForPerson(PersonDetails person) {
        if (person == null || StrUtils.isBlank(person.getName())) {
            this.person.setSelection(this.personDetailsList.size() - 1);
            return;
        }

        for (int i = 0; i < this.personDetailsList.size(); i++) {
            if (this.personDetailsList.get(i).equals(person)) {
                this.person.setSelection(i);
                break;
            }
        }
        tel.setText(person.getTel());
        manCard.setText(person.getCord());
    }

    private String date2String(Calendar date) {
        if (date == null) return "";
        return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
    }
}
