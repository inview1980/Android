package my_manage.ui.rent_manage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyDialogFragment;

/**
 * 续租金
 *
 * @author inview
 * @Date 2020/12/7 14:45
 * @Description :
 */
public class DialogFragmentContinueRent<T extends Activity & IShowList> extends MyDialogFragment {
    @BindView(R.id.toolbar)         Toolbar         toolbar;
    @BindView(R.id.area)            TextView        area;
    @BindView(R.id.rent_startDate)  TextView        startDate;
    @BindView(R.id.rent_endDate)    TextView        rentEndDate;
    @BindView(R.id.continueMonth)   Spinner         continueMonth;
    @BindView(R.id.payDate)         TextView        payDate;
    @BindView(R.id.renting_endDate) TextView        rentingEndDate;
    @BindView(R.id.monthlyRent)     EditText        monthlyRent;
    @BindView(R.id.deposit)         TextView        deposit;
    @BindView(R.id.totalMoney)      TextView        totalMoney;
    @BindView(R.id.remark)          EditText        remark;
    private                         ShowRoomDetails showRoomDetails;
    private                         T               activity;

    public DialogFragmentContinueRent(T activity, ShowRoomDetails showRoomDetails) {
        this.activity = activity;
        this.showRoomDetails = showRoomDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (showRoomDetails == null) dismiss();

        View v = inflater.inflate(R.layout.rental_continue_rent_activity, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("续租");
        toolbar.setSubtitle(showRoomDetails.getRoomDetails().getCommunityName() + showRoomDetails.getRoomDetails().getRoomNumber());
        toolbar.setNavigationOnClickListener(v1 -> dismiss());

        initValue();

        return v;
    }


    private void initValue() {
        rentEndDate.setText(DateUtils.date2String(showRoomDetails.getRentalEndDate()));
        area.setText(showRoomDetails.getRoomDetails().getRoomArea() + "");
        monthlyRent.setText(showRoomDetails.getRentalRecord().getMonthlyRent() + "");
        deposit.setText(showRoomDetails.getRentalRecord().getDeposit() + "");
        remark.setText(showRoomDetails.getRentalRecord().getRemarks());
        payDate.setText(DateUtils.date2String(Calendar.getInstance()));

        startDate.setText(DateUtils.date2String(showRoomDetails.getRentalRecord().getStartDate()));
        continueMonth.setSelection(2);
        onItemSelected(null, null, 2, 0);
    }

    @OnItemSelected(R.id.continueMonth)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获取选中的值,position数组下标
        int selectMonth = Integer.parseInt(getResources().getStringArray(R.array.monthNumber)[position]);
        if (showRoomDetails.getRentalEndDate() != null) {
            Calendar d1 = showRoomDetails.getRentalEndDate();
            d1.add(Calendar.MONTH, selectMonth);
            rentingEndDate.setText(DateUtils.date2String(d1));
        }
        double mPrice = Double.parseDouble(monthlyRent.getText().toString());
        totalMoney.setText(String.format(Locale.getDefault(), "%.2f", mPrice * selectMonth));
    }

    @OnClick(R.id.payDate)
    void OnPayDateClick(View view) {
        // 初始化日期
        DateUtils.showDateDialog(this.getContext(),payDate::setText);
    }

    @OnClick({R.id.ok})
    void OnClick(View view) {
        try {
            //深度复制对象RentalRecord
            String       cObj = JSON.toJSONString(showRoomDetails.getRentalRecord());
            RentalRecord rr   = JSON.parseObject(cObj, RentalRecord.class);
            rr.setPrimary_id(0);
            //续租月份数
            int payMonth = Integer.parseInt(continueMonth.getSelectedItem().toString());
            rr.setPayMonth(payMonth);
            //月租
            rr.setMonthlyRent(Double.parseDouble(monthlyRent.getText().toString()));
            //本次租期开始时间
            Calendar now = DateUtils.string2Date(rentEndDate.getText().toString());
            now.add(Calendar.DATE, 1);
            rr.setStartDate(now);
            //付款日期
            rr.setPaymentDate(DateUtils.string2Date(payDate.getText().toString()));
            //总金额
            rr.setTotalMoney(Double.parseDouble(totalMoney.getText().toString()));
            //备注
            rr.setRemarks(remark.getText().toString());
            //插入RentalRecord对象的id
            int newId;
            if ((newId = DbHelper.getInstance().insert(rr)) > 0) {
                showRoomDetails.getRoomDetails().setRecordId(newId);
                if (StrUtils.isNotBlank(showRoomDetails.getRoomDetails().getRoomNumber()) &&
                        DbBase.update(showRoomDetails.getRoomDetails()) > 0) {
                    PageUtils.showMessage(getContext(), "续租成功");
                    activity.showList();
                }
            }
            dismiss();
        } catch (NumberFormatException e) {
            PageUtils.Error(getClass().getSimpleName() + ":" + e.getLocalizedMessage());
        }
    }

}
