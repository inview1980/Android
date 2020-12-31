package my_manage.ui.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.deadline.statebutton.StateButton;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.PayProperty;
import my_manage.pojo.RoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.DropEditText;
import my_manage.ui.widght.FlowRadioGroup;
import my_manage.ui.widght.MyDialogFragment;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/17 16:49
 * @Description :
 */
public final class DialogFragmentPayPropertyAdd extends MyDialogFragment {
    @BindView(R.id.toolbar)       Toolbar        toolbar;
    @BindView(R.id.community)     Spinner        community;
    @BindView(R.id.radioGroup)    FlowRadioGroup radioGroup;
    @BindView(R.id.payDate)       TextView       payDate;
    @BindView(R.id.area)          TextView       area;
    @BindView(R.id.startDate)     TextView       startDate;
    @BindView(R.id.propertyMonth) DropEditText   propertyMonth;
    @BindView(R.id.propertyPrice) EditText       propertyPrice;
    @BindView(R.id.money)         EditText       money;
    @BindView(R.id.remark)        EditText       remark;
    @BindView(R.id.ok)            StateButton    ok;
    private                       List<String>   communityLst;
    private                       PayProperty    payProperty;
    private                       RoomDetails    activityRoom;
    private                       IShowList      iShowList;

    /**
     * 新增数据时调用
     */
    public DialogFragmentPayPropertyAdd() {
        this.communityLst = DbHelper.getInstance().getCommunityNamesByNoDel();
    }

    /**
     * 更改数据时调用
     *
     * @param payProperty
     */
    public DialogFragmentPayPropertyAdd(IShowList iShowList, PayProperty payProperty) {
        this.iShowList = iShowList;
        this.payProperty = payProperty;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pay_property_add_or_update_details, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        initValue();

        return v;
    }

    private void initValue() {
        val monthLst = DbHelper.getInstance().getPayPropertyByRoomNumber(null).stream().map(PayProperty::getPayMonth)
                .distinct().sorted().collect(Collectors.toList());
        propertyMonth.setAdapter(new CommonAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, monthLst) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, Integer item, int position) {
                helper.setText(android.R.id.text1, item.toString());
            }
        });

        if (payProperty == null) {
            //新增
            isInsert();
        } else {
            isUpdate();
        }
        ok.setEnabled(false);
    }

    private void isUpdate() {
        toolbar.setTitle("物业缴费更改");
        community.setVisibility(View.GONE);
        activityRoom = DbBase.getInfoById(payProperty.getRoomNumber(), RoomDetails.class);
        radioGroup.removeAllViews();
        if (activityRoom != null)
            addRadioButton(activityRoom, false);
        area.setText(StrUtils.df4.format(activityRoom.getRoomArea()));
        payDate.setText(DateUtils.date2String(payProperty.getPayDate()));
        startDate.setText(DateUtils.date2String(payProperty.getStartDate(), payProperty.getPayMonth()));
        propertyMonth.setText(payProperty.getPayMonth() + "");
        propertyPrice.setText(StrUtils.df4.format(payProperty.getTotalMoney() / payProperty.getPayMonth()));
        money.setText(StrUtils.df4.format(payProperty.getTotalMoney()));
        remark.setText(payProperty.getRemarks());
    }

    private void isInsert() {
        toolbar.setTitle("新增物业缴费记录");
        community.setAdapter(new SpinnerStringAdapter(getContext(), communityLst));
        community.setSelection(0);
        propertyMonth.setText("12");
        Calendar now = Calendar.getInstance();
        payDate.setText(DateUtils.date2String(now));
        now.set(Calendar.MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH, 1);
        startDate.setText(DateUtils.date2String(now, 12));
    }

    @OnItemSelected(R.id.community)
    void onItemSelected(android.widget.AdapterView<?> adapterView, android.view.View view, int position, long i) {
        radioGroup.removeAllViews();
        val roomLst = DbHelper.getInstance().getRoomDetailsAll().stream()
                .filter(rd -> rd.getCommunityName().equals(communityLst.get(position)))
                .collect(Collectors.toList());
        for (final RoomDetails roomDetails : roomLst) {
            addRadioButton(roomDetails, true);
        }
        View view1 = radioGroup.getChildAt(0);
        if (view1 != null) {
            ((RadioButton) view1).setChecked(true);
            ((RadioButton) view1).callOnClick();
        }
    }

    private void addRadioButton(RoomDetails roomDetails, boolean enabled) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(roomDetails.getRoomNumber());
        radioButton.setTag(roomDetails);
        radioButton.setWidth(getResources().getDimensionPixelSize(R.dimen.pwdItemWidth));
        radioButton.setOnClickListener(this::onRadioClick);
        radioButton.setEnabled(enabled);
        radioGroup.addView(radioButton);
    }

    private void onRadioClick(View view) {
        RoomDetails rd = (RoomDetails) view.getTag();
        propertyPrice.setText(StrUtils.df4.format(rd.getPropertyPrice()));
        area.setText(StrUtils.df4.format(rd.getRoomArea()));
        activityRoom = rd;
        val lastPay = DbHelper.getInstance().getPayPropertyByRoomNumber(rd.getRoomNumber())
                .stream().sorted((l1, l2) -> Long.compare(l2.getStartDate().getTimeInMillis(), l1.getStartDate().getTimeInMillis()))
                .findFirst().orElse(new PayProperty());
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(Optional.ofNullable(lastPay.getStartDate()).orElse(Calendar.getInstance()).getTimeInMillis());
        lastDate.add(Calendar.MONTH, lastPay.getPayMonth());
        lastDate.add(Calendar.DAY_OF_YEAR, 1);
        startDate.setText(DateUtils.date2String(lastDate, Integer.parseInt(propertyMonth.getText().toString())));
        ok.setEnabled(false);
    }

    @OnClick({R.id.startDate, R.id.payDate})
    void OnPayDateClick(View view) {
        // 初始化日期
        if (view.getId() == R.id.payDate) {
            DateUtils.showDateDialog(getContext(), payDate::setText);
        } else {
            String month = propertyMonth.getText().toString();
            int    n     = StrUtils.isBlank(month) ? 1 : Integer.parseInt(month);
            DateUtils.showDateDialog(getContext(), startDate::setText, null, n);
        }
        ok.setEnabled(true);
    }

    /**
     * 交费月份数更改时，重新计算缴费期间
     */
    @OnTextChanged(R.id.propertyMonth)
    void onTextChanged() {
        String   month = propertyMonth.getText().toString();
        int      n     = StrUtils.isBlank(month) ? 1 : Integer.parseInt(month);
        Calendar c1    = DateUtils.string2Date(startDate.getText().toString());
        startDate.setText(DateUtils.date2String(c1, n));
        ok.setEnabled(true);
    }

    @OnTextChanged({R.id.propertyPrice, R.id.money, R.id.remark})
    void onEditChanged() {
        ok.setEnabled(true);
    }

    @OnClick(R.id.ok)
    void onOK() {
        PayProperty tmpPP = new PayProperty();
        tmpPP.setPayDate(DateUtils.string2Date(payDate.getText().toString()));
        tmpPP.setPayMonth(Integer.parseInt(propertyMonth.getText().toString()));
        tmpPP.setStartDate(DateUtils.string2Date(startDate.getText().toString()));
        tmpPP.setTotalMoney(Double.parseDouble(money.getText().toString()));
        tmpPP.setRemarks(remark.getText().toString());
        tmpPP.setRoomNumber(activityRoom.getRoomNumber());
        if (payProperty != null) {
            //更改
            tmpPP.setPrimary_id(payProperty.getPrimary_id());
            if (DbBase.update(tmpPP) > 0) {
                PageUtils.showMessage(getContext(), "更改成功。");
                iShowList.showList();
                dismiss();
            } else {
                PageUtils.showMessage(getContext(), "更改失败。");
            }
        } else {
            //新增
            if (DbBase.insert(tmpPP) > 0) {
                PageUtils.showMessage(getContext(), "新增成功。");
                dismiss();
            } else {
                PageUtils.showMessage(getContext(), "新增失败。");
            }
        }
    }
}
