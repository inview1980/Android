package my_manage.ui.rent_manage.page;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import my_manage.ui.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.tool.database.DbBase;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

/**
 * 续租金
 */
public class ContinueRentActivity extends MyBaseSwipeBackActivity {
    @BindView(R.id.deposit) TextView        deposit;
    @BindView(R.id.remark)  EditText        remark;
    private                 ShowRoomDetails showRoomDetails;

    @BindView(R.id.area)            TextView area;
    @BindView(R.id.rent_endDate)    TextView rentEndDate;
    @BindView(R.id.continueMonth)   Spinner  continueMonth;
    @BindView(R.id.renting_endDate) TextView rentingEndDate;
    @BindView(R.id.monthlyRent)     EditText monthlyRent;
    @BindView(R.id.totalMoney)      TextView totalMoney;
    @BindView(R.id.rent_startDate)  TextView startDate;
    @BindView(R.id.payDate)         TextView payDate;
    @BindView(R.id.ok)              Button   ok;
    @BindView(R.id.cancel)          Button   cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_continue_rent_activity);

        Intent intent = getIntent();
        String tmp1   = intent.getStringExtra(ShowRoomDetails.class.getSimpleName());
        showRoomDetails = JSON.parseObject(tmp1, ShowRoomDetails.class);
        ButterKnife.bind(this);
        initToolbar(showRoomDetails);
        initValue();

        PageUtils.setUnderline(this);
    }

    private void initToolbar(ShowRoomDetails room) {
        String str1 = "续租:";
        if (room == null) {
            setTitle(str1);
        } else {
            setTitle(str1+room.getRoomDetails().getCommunityName());
            setSubtitle(room.getRoomDetails().getRoomNumber());
        }
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
        totalMoney.setText(String.format(Locale.getDefault(),"%.2f", mPrice * selectMonth ));
    }

    @OnClick(R.id.payDate)
    void OnPayDateClick(View view) {
        // 初始化日期
        Calendar myCalendar = Calendar.getInstance();
        int      myYear     = myCalendar.get(Calendar.YEAR);
        int      month      = myCalendar.get(Calendar.MONTH);
        int      day        = myCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
            this.payDate.setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth);
        }, myYear, month, day);
        dpd.show();
    }

    /**
     * 失去焦点关闭键盘
     */
    @OnFocusChange({R.id.remark, R.id.monthlyRent})
    void OnFocusChange(View view, boolean b) {
        PageUtils.closeInput(this, b);
        //计算总金额
        double mPrice = Double.parseDouble(monthlyRent.getText().toString());
        int    m      = Integer.parseInt(continueMonth.getSelectedItem().toString());
        totalMoney.setText((mPrice * m) + "");
    }

    @OnClick({R.id.ok, R.id.cancel})
    void OnClick(View view) {
        try {
            if (view.getId() == R.id.ok) {
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
                        Toast.makeText(this, "续租成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            finish();
        } catch (NumberFormatException e) {}
    }

    @Override
    public void showList() {

    }
}
