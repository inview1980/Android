package my_manage.ui.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import lombok.Getter;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.ui.rent_manage.listener.PersonListener;
import my_manage.tool.database.DbHelper;
import my_manage.ui.rent_manage.listener.NewRoomFragmentLister;
import my_manage.pojo.PersonDetails;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.enums.ShowRoomType;

/**
 * 房屋出租详情
 */
@Getter
public final class FragmentRoomDetails extends Fragment implements IShowList {
    @BindView(R.id.rental_editRoom_community)          TextView       community;
    @BindView(R.id.rental_editRoom_roomNumber)         TextView       roomNumber;
    @BindView(R.id.rental_editRoom_realtyMoney)        EditText       rentalMoney;//月租金
    @BindView(R.id.rental_editRoom_meterNumber)        EditText       meterNumber;
    @BindView(R.id.rental_editRoom_area)               EditText       area;
    @BindView(R.id.rental_editRoom_propertyPrice)      EditText       propertyPrice;//物业费单价
    @BindView(R.id.rental_editRoom_person)             Spinner        person;
    @BindView(R.id.rental_editRoom_tel)                EditText       tel;
    @BindView(R.id.rental_editRoom_manCardNumber)      EditText       manCard;
    @BindView(R.id.rental_editRoom_isContainRealty)    CheckBox       isContainRealty;
    @BindView(R.id.rental_editRoom_beginDate)          TextView       beginDate;//房租开始时间
    @BindView(R.id.rental_editRoom_rentalMonth)        Spinner        rentalMonth;//房租结束时间
    @BindView(R.id.rental_editRoom_propertyBeginDate)  TextView       propertyDate;//物业费开始时间
    @BindView(R.id.rental_editRoom_payDate)            TextView       payDate;//交费时间
    @BindView(R.id.rental_editRoom_remark)             EditText       remark;//备注
    @BindView(R.id.rental_editRoom_propertyMonth)      Spinner        propertyMonth;
    @BindView(R.id.rental_editRoom_changeBtn)          Button         changeBtn;
    @BindView(R.id.rental_editRoom_okBtn)              Button         okBtn;
    @BindView(R.id.rental_editRoom_add)                Button         addBtn;
    @BindView(R.id.rental_editRoom_deposit)            EditText       deposit;//押金
    @BindView(R.id.rental_editRoom_contract_BeginDate) TextView       contractDate;//合同开始时间
    @BindView(R.id.rental_editRoom_contract_Month)     Spinner        contractMonth;//合同时长，月
    @BindView(R.id.img_shrink)                         ImageView      imgShrink;
    @BindView(R.id.rental_editRoom_person_extend)      RelativeLayout PersonExtend;
    @BindView(R.id.img_shrink2)                        ImageView      imgShrink2;
    @BindView(R.id.rental_editRoom_person_remark)      EditText       rentalEditRoomPersonRemark;
    @BindView(R.id.rental_editRoom_waterMeter)         EditText       waterMeter;
    @BindView(R.id.totalMoney)                         EditText       totalMoney;
    @BindView(R.id.rentalEditRoomExtendTxt1)           TextView       rentalEditRoomExtendTxt1;
    @BindView(R.id.rental_editRoom_companyName)        EditText       rentalEditRoomCompanyName;
    @BindView(R.id.person_expand_layout)               TableLayout    personExpandLayout;
    @BindView(R.id.rentalEditRoomExtendTxt2)           TextView       rentalEditRoomExtendTxt2;
    @BindView(R.id.rental_editRoom_roomDetails_extend) RelativeLayout rentalEditRoomRoomDetailsExtend;
    @BindView(R.id.roomDetailsLayout)                  TableLayout    roomDetailsLayout;

    private ShowRoomDetails       showRoomDetails;
    private List<PersonDetails>   personDetailsList;
    private Unbinder              bind;
    private NewRoomFragmentLister lister;
    private boolean               otherVisibility;
    private boolean               personVisibility;


    public FragmentRoomDetails(ShowRoomDetails showRoomDetails, ShowRoomType type) {
        if (showRoomDetails != null) {
            Bundle bundle = new Bundle();
            bundle.putString("ShowRoomDetails", JSON.toJSONString(showRoomDetails));
            bundle.putInt("ShowRoomType", type.getIndex());
            setArguments(bundle);
        }
    }

    @OnFocusChange({R.id.rental_editRoom_propertyPrice, R.id.rental_editRoom_meterNumber, R.id.rental_editRoom_area,
                           R.id.rental_editRoom_tel, R.id.rental_editRoom_manCardNumber, R.id.rental_editRoom_remark
                           , R.id.rental_editRoom_waterMeter})
    void OnFocusChangeListener(View v, boolean b) {
        PageUtils.closeInput(getActivity(), b);
    }

    @OnClick({R.id.rental_editRoom_person_extend, R.id.rental_editRoom_roomDetails_extend})
    void OnLayoutExpand(View view) {
        if (view.getId() == R.id.rental_editRoom_person_extend) {
            //是否展开Person
            extendLayout(imgShrink, rentalEditRoomExtendTxt1, view.getId(), !personVisibility);
            personVisibility = !personVisibility;
        } else if (view.getId() == R.id.rental_editRoom_roomDetails_extend) {
            //是否展开Other
            extendLayout(imgShrink2, rentalEditRoomExtendTxt2, view.getId(), !otherVisibility);
            otherVisibility = !otherVisibility;
        }
    }

    @OnClick(R.id.rental_editRoom_tel)
    void onTelClick() {
        PersonListener.telCall(getActivity(), tel.getText().toString());
    }

    /**
     * 展开，收缩layout
     *
     * @param imgId 标题栏中放置向上、下的容器
     * @param txtId 标题栏中放置文字说明的容器
     * @param b     true展开
     */
    private void extendLayout(ImageView imgId, TextView txtId, int id, boolean b) {
        int state;
        if (b) {
            txtId.setText(R.string.click_close);
            imgId.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            state = View.VISIBLE;
        } else {
            txtId.setText(R.string.click_extend);
            imgId.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            state = View.GONE;
        }
        if (id == R.id.rental_editRoom_person_extend) {
            personExpandLayout.setVisibility(state);
        } else {
            roomDetailsLayout.setVisibility(state);
        }
    }

    private void extendLayout(boolean b1, boolean bb2) {
        extendLayout(imgShrink, rentalEditRoomExtendTxt1, R.id.rental_editRoom_person_extend, personVisibility = b1);
        extendLayout(imgShrink2, rentalEditRoomExtendTxt2, R.id.rental_editRoom_roomDetails_extend, otherVisibility = bb2);
    }


    /**
     * spinner选择事件，刷新相对应的日期
     */
    @OnItemSelected({R.id.rental_editRoom_rentalMonth, R.id.rental_editRoom_propertyMonth, R.id.rental_editRoom_contract_Month})
    public void onFocusChange() {
        if (showRoomDetails.getRentalRecord() == null) return;
        if (okBtn.getVisibility() == View.VISIBLE) {
            lister.afterTextChanged(null);
            changeDateLabel(rentalMonth, this.beginDate);
            changeDateLabel(propertyMonth, this.propertyDate);
            changeDateLabel(contractMonth, this.contractDate);
        }
    }

    /**
     * 自动注册点击事件
     */
    @OnClick({R.id.rental_editRoom_changeBtn, R.id.rental_editRoom_okBtn, R.id.rental_editRoom_add})
    void OnClick(View view) {
        lister.onButtonClick(view);
    }

    /**
     * 自动注册点击事件
     */
    @OnClick({R.id.rental_editRoom_beginDate, R.id.rental_editRoom_propertyBeginDate, R.id.rental_editRoom_payDate,
                     R.id.rental_editRoom_contract_BeginDate})
    void onDateTextViewClick(View view) {
        lister.onDateTextViewClick(view);
    }


    /**
     * 自动注册EditView中文本变化事件
     */
    @OnTextChanged({R.id.rental_editRoom_realtyMoney, R.id.rental_editRoom_deposit,
                           R.id.rental_editRoom_propertyPrice, R.id.rental_editRoom_meterNumber, R.id.rental_editRoom_area,
                           R.id.rental_editRoom_tel, R.id.rental_editRoom_manCardNumber,
                           R.id.rental_editRoom_remark, R.id.rental_editRoom_person_remark
                           , R.id.rental_editRoom_waterMeter, R.id.totalMoney})
    void OnTextChanged() {
        lister.afterTextChanged(null);
    }

    @OnItemSelected(R.id.rental_editRoom_person)
    void onPersonSpinnerItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        lister.afterTextChanged(null);
        PersonDetails pd = personDetailsList.get(i);
        setValueForPerson(pd);
    }

    private void init(View v) {
        showList();
        PageUtils.setUnderline(this);

        isContainRealty.setOnCheckedChangeListener((compoundButton, b) -> lister.afterTextChanged(null));
    }

    private void changeDateLabel(Spinner input, TextView showTxt) {
        try {
            if (StrUtils.isNotBlank(showTxt.getText().toString())) {
                val d1 = DateUtils.string2Date(showTxt.getText().toString());
                int i1 = Integer.parseInt(input.getSelectedItem().toString());
                showTxt.setText(DateUtils.date2String(d1, i1));
            }
        } catch (NumberFormatException e) {
        }
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
                ShowRoomType type = ShowRoomType.getType(getArguments().getInt("ShowRoomType", 0));
                if (type == ShowRoomType.Rent) {
                    //新增
                    //展开Other
                    extendLayout(false, true);
                    PageUtils.setEnable(this, true);
                    okBtn.setVisibility(View.VISIBLE);
                } else if (type == ShowRoomType.History) {
                    //显示历史记录，隐藏修改按钮
                    changeBtn.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    extendLayout(false, true);
                } else if (type == ShowRoomType.Person) {
                    //显示用户记录，隐藏修改按钮
                    changeBtn.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    extendLayout(true, false);
                } else {
                    changeBtn.setVisibility(View.VISIBLE);
                    extendLayout(false, true);
                }

                String extra = getArguments().getString("ShowRoomDetails");
                if (StrUtils.isNotBlank(extra)) {
                    ShowRoomDetails room = JSON.parseObject(extra, ShowRoomDetails.class);
                    if (room != null) {
                        PageUtils.setEnable(this, type == ShowRoomType.Rent);
                        setValue(room);
                        this.showRoomDetails = room;
                    }
                }
            } else {
                //新增
                PageUtils.setEnable(this, true);
                okBtn.setVisibility(View.VISIBLE);
            }
        } catch (
                JSONException ignored) {
            //新增
            PageUtils.setEnable(this, true);
            okBtn.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private void setValue(ShowRoomDetails room) {
        community.setText(room.getRoomDetails().getCommunityName());
        roomNumber.setText(room.getRoomDetails().getRoomNumber());
        propertyPrice.setText(String.format(Locale.getDefault(), "%.2f", room.getRoomDetails().getPropertyPrice()));
        meterNumber.setText(room.getRoomDetails().getElectricMeter());
        waterMeter.setText(room.getRoomDetails().getWaterMeter());
        area.setText(String.format(Locale.getDefault(), "%.2f", room.getRoomDetails().getRoomArea()));

        setValueForPerson(room.getPersonDetails());

        if (room.getRentalRecord() != null) {
            totalMoney.setText(String.format(Locale.getDefault(), "%.2f", room.getRentalRecord().getTotalMoney()));
            rentalMoney.setText(String.format(Locale.getDefault(), "%.2f", room.getRentalRecord().getMonthlyRent()));
            isContainRealty.setChecked(room.getRentalRecord().getIsContainRealty());
            payDate.setText(DateUtils.date2String(room.getRentalRecord().getPaymentDate()));
            beginDate.setText(DateUtils.date2String(room.getRentalRecord().getStartDate(), room.getRentalRecord().getPayMonth()));
            propertyDate.setText(DateUtils.date2String(room.getRentalRecord().getRealtyStartDate(), room.getRentalRecord().getPropertyTime()));
            contractDate.setText(DateUtils.date2String(room.getRentalRecord().getContractSigningDate(), room.getRentalRecord().getContractMonth()));
            rentalMonth.setSelection(findMonth(room.getRentalRecord().getPayMonth()));
            propertyMonth.setSelection(findMonth(room.getRentalRecord().getPropertyTime()));
            contractMonth.setSelection(findMonth(room.getRentalRecord().getContractMonth()));
            remark.setText(room.getRentalRecord().getRemarks());
            deposit.setText(room.getRentalRecord().getDeposit() + "");
        }
    }

    private int findMonth(int payMonth) {
        String[] lst = this.getResources().getStringArray(R.array.monthNumber);
        for (int i = 0; i < lst.length; i++) {
            if (lst[i].equals(StrUtils.df4.format(payMonth))) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }

    private void setValueForPerson(PersonDetails person) {
        if (StrUtils.isBlank(person.getName())) {
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
        rentalEditRoomPersonRemark.setText(person.getOther());
        rentalEditRoomCompanyName.setText(person.getCompany());
    }

    @Override
    public void showList() {
        this.personDetailsList = DbHelper.getInstance().getPersonList();
        person.setAdapter(new CommonAdapter<PersonDetails>(FragmentRoomDetails.this.getContext(),
                android.R.layout.simple_list_item_1,
                this.personDetailsList) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, PersonDetails item, int position) {
                helper.setText(android.R.id.text1, item.getName());
            }
        });
    }

}
