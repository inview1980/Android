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
import butterknife.OnItemSelected;
import my_manage.ui.password_box.R;
import my_manage.tool.database.DbBase;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

/**
 * 续合同
 */
public class ContinueContractActivity extends MyBaseSwipeBackActivity {

    @BindView(R.id.area)          TextView area;
    @BindView(R.id.start1Date)    TextView start1Date;
    @BindView(R.id.end1Date)      TextView end1Date;
    @BindView(R.id.continueMonth) Spinner  continueMonth;
    @BindView(R.id.start2Date)    TextView start2Date;
    @BindView(R.id.end2Date)      TextView end2Date;
    @BindView(R.id.remark)        EditText remark;
    @BindView(R.id.ok)            Button   ok;
    @BindView(R.id.cancel)        Button   cancel;
    private ShowRoomDetails showRoomDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_continue_rent_contract_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String tmp1   = intent.getStringExtra(ShowRoomDetails.class.getSimpleName());
        showRoomDetails = JSON.parseObject(tmp1, ShowRoomDetails.class);
        initToolbar(showRoomDetails);
        initValue();

        PageUtils.setUnderline(this);
    }

    private void initToolbar(ShowRoomDetails room) {
        String str1 = "续签合同:";
        if (room == null) {
            setTitle(str1);
        } else {
            setTitle(str1 + room.getRoomDetails().getCommunityName());
            setSubtitle(room.getRoomDetails().getRoomNumber());
        }
    }

    private void initValue() {
        if (showRoomDetails.getContractEndDate() == null) {
            //如果结束日期为空时，设置为当前日期
            start2Date.setText(DateUtils.date2String(Calendar.getInstance()));
        } else {
            Calendar date=Calendar.getInstance();
            date.setTime(showRoomDetails.getContractEndDate().getTime());
            date.add(Calendar.DATE,1);
            start2Date.setText(DateUtils.date2String(date));
        }
        end1Date.setText(DateUtils.date2String(showRoomDetails.getContractEndDate()));
        start1Date.setText(DateUtils.date2String(showRoomDetails.getRentalRecord().getContractSigningDate()));
        area.setText(String.format(Locale.getDefault(), "%.2f", showRoomDetails.getRoomDetails().getRoomArea()));
        remark.setText(showRoomDetails.getRentalRecord().getRemarks());

        continueMonth.setSelection(11);
        onItemSelected(null, null, 11, 0);
    }
    @OnClick(R.id.start2Date)
    void OnPayDateClick(View view) {
        // 初始化日期
        Calendar myCalendar = Calendar.getInstance();
        int      myYear     = myCalendar.get(Calendar.YEAR);
        int      month      = myCalendar.get(Calendar.MONTH);
        int      day        = myCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
            this.start2Date.setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth);
            int selectNo=continueMonth.getSelectedItemPosition();
            onItemSelected(null,null,selectNo,0);
        }, myYear, month, day);
        dpd.show();
    }

    @OnItemSelected(R.id.continueMonth)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获取选中的值,position数组下标
        int selectMonth = Integer.parseInt(getResources().getStringArray(R.array.monthNumber)[position]);
        Calendar date = DateUtils.string2Date(start2Date.getText().toString());
        date.add(Calendar.MONTH, selectMonth);
        date.add(Calendar.DATE,-1);
        end2Date.setText(DateUtils.date2String(date));
    }
    @OnClick({R.id.ok, R.id.cancel})
    void OnClick(View view) {
        try {
            if (view.getId() == R.id.ok) {
                RentalRecord rr = showRoomDetails.getRentalRecord();
                //续签合同月份数
                int payMonth = Integer.parseInt(continueMonth.getSelectedItem().toString());
                rr.setContractMonth(payMonth);
                //本次合同开始时间
                Calendar now = DateUtils.string2Date(start2Date.getText().toString());
                rr.setContractSigningDate(now);
                //备注
                rr.setRemarks(remark.getText().toString());
                //更新RentalRecord对象
                if ((DbBase.update(rr)) > 0) {
                    Toast.makeText(this, "物业费续费成功", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        } catch (NumberFormatException e) {}
    }

    @Override
    public void showList() {

    }
}
