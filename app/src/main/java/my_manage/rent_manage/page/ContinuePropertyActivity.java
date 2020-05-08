package my_manage.rent_manage.page;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import my_manage.password_box.R;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;

public class ContinuePropertyActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)        Toolbar         toolbar;
    @BindView(R.id.area)           TextView        area;
    @BindView(R.id.pro_startDate)  TextView        proStartDate;
    @BindView(R.id.pro_endDate)    TextView        proEndDate;
    @BindView(R.id.continueMonth)  Spinner         continueMonth;
    @BindView(R.id.pro2_endDate)   TextView        pro2EndDate;
    @BindView(R.id.proPrice)       TextView        proPrice;
    @BindView(R.id.totalMoney)     TextView        totalMoney;
    @BindView(R.id.remark)         EditText        remark;
    @BindView(R.id.ok)             Button          ok;
    @BindView(R.id.cancel)         Button          cancel;
    @BindView(R.id.pro2_startDate) TextView        pro2StartDate;
    private                        ShowRoomDetails showRoomDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_continue_rent_property_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String tmp1   = intent.getStringExtra(ShowRoomDetails.class.getSimpleName());
        showRoomDetails = JSON.parseObject(tmp1, ShowRoomDetails.class);
        initToolbar(showRoomDetails);
        initValue();

        PageUtils.setUnderline(this);
    }

    private void initToolbar(ShowRoomDetails room) {
        String str1 = "续物业费:";
        if (room == null) {
            toolbar.setTitle(str1);
        } else {
            toolbar.setTitle(str1 + room.getRoomDetails().getCommunityName());
            toolbar.setSubtitle(room.getRoomDetails().getRoomNumber());
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initValue() {
        if (showRoomDetails.getPropertyCostEndDate() == null) {
            //如果结束日期为空时，设置为当前日期
            pro2StartDate.setText(DateUtils.date2String(Calendar.getInstance()));
        } else {
            Calendar date=Calendar.getInstance();
            date.setTime(showRoomDetails.getPropertyCostEndDate().getTime());
            date.add(Calendar.DATE,1);
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
        Calendar myCalendar = Calendar.getInstance();
        int      myYear     = myCalendar.get(Calendar.YEAR);
        int      month      = myCalendar.get(Calendar.MONTH);
        int      day        = myCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
            this.pro2StartDate.setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth);
            int selectNo=continueMonth.getSelectedItemPosition();
            onItemSelected(null,null,selectNo,0);
        }, myYear, month, day);
        dpd.show();
    }

    @OnItemSelected(R.id.continueMonth)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获取选中的值,position数组下标
        int selectMonth = Integer.parseInt(getResources().getStringArray(R.array.monthNumber)[position]);
        Calendar date = DateUtils.string2Date(pro2StartDate.getText().toString());
        date.add(Calendar.MONTH, selectMonth);
        date.add(Calendar.DATE,-1);
        pro2EndDate.setText(DateUtils.date2String(date));

        //计算物业费总额,保留2位小数
        double p = showRoomDetails.getRoomDetails().getPropertyPrice() * showRoomDetails.getRoomDetails().getRoomArea() * selectMonth;
        totalMoney.setText(String.format(Locale.getDefault(), "%.2f", p));
    }

    @OnClick({R.id.ok, R.id.cancel})
    void OnClick(View view) {
        try {
            if (view.getId() == R.id.ok) {
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
                if ((RentDB.update(rr)) > 0) {
                    Toast.makeText(this, "物业费续费成功", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        } catch (NumberFormatException e) {}
    }
}
