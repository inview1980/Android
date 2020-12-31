package my_manage.ui.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import my_manage.password_box.R;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/7 15:47
 * @Description :
 */
public final class DialogFragmentContinueProperty extends MyDialogFragment {
    @BindView(R.id.toolbar)        Toolbar         toolbar;
    @BindView(R.id.area)           TextView        area;
    @BindView(R.id.pro_startDate)  TextView        proStartDate;
    @BindView(R.id.pro_endDate)    TextView        proEndDate;
    @BindView(R.id.continueMonth)  Spinner         continueMonth;
    @BindView(R.id.pro2_startDate) TextView        pro2StartDate;
    @BindView(R.id.pro2_endDate)   TextView        pro2EndDate;
    @BindView(R.id.proPrice)       TextView        proPrice;
    @BindView(R.id.totalMoney)     TextView        totalMoney;
    @BindView(R.id.remark)         EditText        remark;
    private                        ShowRoomDetails showRoomDetails;

    public DialogFragmentContinueProperty(ShowRoomDetails showRoomDetails) {
        this.showRoomDetails = showRoomDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (showRoomDetails == null) dismiss();

        View v = inflater.inflate(R.layout.rental_continue_rent_property_activity, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("续物业费" );
        toolbar.setSubtitle(showRoomDetails.getRoomDetails().getCommunityName() + showRoomDetails.getRoomDetails().getRoomNumber());
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        initValue();

        return v;
    }

    private void initValue() {
        if (showRoomDetails.getPropertyCostEndDate() == null) {
            //如果结束日期为空时，设置为当前日期
            pro2StartDate.setText(DateUtils.date2String(Calendar.getInstance()));
        } else {
            Calendar date = Calendar.getInstance();
            date.setTime(showRoomDetails.getPropertyCostEndDate().getTime());
            date.add(Calendar.DATE, 1);
            pro2StartDate.setText(DateUtils.date2String(date));
        }
        proEndDate.setText(DateUtils.date2String(showRoomDetails.getPropertyCostEndDate()));
        proStartDate.setText(DateUtils.date2String(showRoomDetails.getRentalRecord().getRealtyStartDate()));
        area.setText(String.format(Locale.getDefault(), "%.2f", showRoomDetails.getRoomDetails().getRoomArea()));
        proPrice.setText(String.format(Locale.getDefault(), "%.2f", showRoomDetails.getRoomDetails().getPropertyPrice()));
        remark.setText(showRoomDetails.getRentalRecord().getRemarks());

        continueMonth.setSelection(11);
        onItemSelected(null, null, 11, 0);
    }

    @OnClick(R.id.pro2_startDate)
    void OnPayDateClick(View view) {
        // 初始化日期
        DateUtils.showDateDialog(getContext(),pro2StartDate::setText, str->{
            int selectNo = continueMonth.getSelectedItemPosition();
            onItemSelected(null, null, selectNo, 0);
        });
    }

    @OnItemSelected(R.id.continueMonth)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获取选中的值,position数组下标
        int      selectMonth = Integer.parseInt(getResources().getStringArray(R.array.monthNumber)[position]);
        Calendar date        = DateUtils.string2Date(pro2StartDate.getText().toString());
        date.add(Calendar.MONTH, selectMonth);
        date.add(Calendar.DATE, -1);
        pro2EndDate.setText(DateUtils.date2String(date));

        //计算物业费总额,保留2位小数
        double p = showRoomDetails.getRoomDetails().getPropertyPrice() * showRoomDetails.getRoomDetails().getRoomArea() * selectMonth;
        totalMoney.setText(StrUtils.df4.format(p));
    }

    @OnClick({R.id.ok})
    void OnClick(View view) {
        try {
            RentalRecord rr = showRoomDetails.getRentalRecord();
            //续租月份数
            int payMonth = Integer.parseInt(continueMonth.getSelectedItem().toString());
            rr.setPropertyTime(payMonth);
            //本次物业费开始时间
            Calendar now = DateUtils.string2Date(pro2StartDate.getText().toString());
            rr.setRealtyStartDate(now);
            //总金额
            rr.setPropertyCosts(Double.parseDouble(totalMoney.getText().toString()));
            //备注
            rr.setRemarks(remark.getText().toString());
            //更新RentalRecord对象
            if ((DbBase.update(rr)) > 0) {
                PageUtils.showMessage(getContext(), "物业费续费成功");
            }
            dismiss();
        } catch (NumberFormatException e) {
            PageUtils.Error(getClass().getSimpleName() + ":" + e.getLocalizedMessage());
        }
    }
}
