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
import my_manage.tool.database.DbBase;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/7 16:17
 * @Description :
 */
public final class DialogFragmentContinueContract extends MyDialogFragment {
    @BindView(R.id.toolbar)       Toolbar         toolbar;
    @BindView(R.id.area)          TextView        area;
    @BindView(R.id.start1Date)    TextView        start1Date;
    @BindView(R.id.end1Date)      TextView        end1Date;
    @BindView(R.id.continueMonth) Spinner         continueMonth;
    @BindView(R.id.start2Date)    TextView        start2Date;
    @BindView(R.id.end2Date)      TextView        end2Date;
    @BindView(R.id.remark)        EditText        remark;
    private                       ShowRoomDetails showRoomDetails;

    public DialogFragmentContinueContract(ShowRoomDetails showRoomDetails) {
        this.showRoomDetails = showRoomDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (showRoomDetails == null) dismiss();

        View v = inflater.inflate(R.layout.rental_continue_rent_contract_activity, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("续签合同");
        toolbar.setSubtitle(showRoomDetails.getRoomDetails().getCommunityName()+showRoomDetails.getRoomDetails().getRoomNumber());
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        initValue();

        return v;
    }

    private void initValue() {
        if (showRoomDetails.getContractEndDate() == null) {
            //如果结束日期为空时，设置为当前日期
            start2Date.setText(DateUtils.date2String(Calendar.getInstance()));
        } else {
            Calendar date = Calendar.getInstance();
            date.setTime(showRoomDetails.getContractEndDate().getTime());
            date.add(Calendar.DATE, 1);
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
        DateUtils.showDateDialog(getContext(),start2Date::setText, str->{
            int selectNo = continueMonth.getSelectedItemPosition();
            onItemSelected(null, null, selectNo, 0);
        });
    }

    @OnItemSelected(R.id.continueMonth)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获取选中的值,position数组下标
        int      selectMonth = Integer.parseInt(getResources().getStringArray(R.array.monthNumber)[position]);
        Calendar date        = DateUtils.string2Date(start2Date.getText().toString());
        date.add(Calendar.MONTH, selectMonth);
        date.add(Calendar.DATE, -1);
        end2Date.setText(DateUtils.date2String(date));
    }

    @OnClick({R.id.ok})
    void OnClick(View view) {
        try {
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
                PageUtils.showMessage(getContext(), "合同续费成功");
            }
            dismiss();
        } catch (NumberFormatException e) {
            PageUtils.Error(getClass().getSimpleName() + ":" + e.getLocalizedMessage());
        }
    }
}
