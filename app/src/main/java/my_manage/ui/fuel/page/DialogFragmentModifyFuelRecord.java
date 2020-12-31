package my_manage.ui.fuel.page;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.deadline.statebutton.StateButton;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.FuelRecord;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.DropEditText;
import my_manage.ui.widght.MyDialogFragment;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/7 17:03
 * @Description :
 */
public final class DialogFragmentModifyFuelRecord<T extends Activity & IShowList> extends MyDialogFragment {
    @BindView(R.id.toolbar)        Toolbar      toolbar;
    @BindView(R.id.date)           TextView     date;
    @BindView(R.id.totalMoney)     EditText     totalMoney;
    @BindView(R.id.rise)           EditText     rise;
    @BindView(R.id.original_price) EditText     originalPrice;
    @BindView(R.id.odometerNumber) EditText     odometerNumber;
    @BindView(R.id.station_name)   DropEditText stationName;
    @BindView(R.id.ok)             StateButton  ok;
    private                        FuelRecord   fr;
    private                        T            activity;
    private                        boolean      isNewData = false;

    public DialogFragmentModifyFuelRecord(FuelRecord fr, T activity) {
        this.fr = fr;
        this.activity = activity;
        if (fr == null) isNewData = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fuel_add, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("加油记录");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());

        init();

        return v;
    }

    private void init() {
        val          frList = DbHelper.getInstance().getFuelRecordList();
        List<String> names  = frList.stream().map(FuelRecord::getStationName).distinct().collect(Collectors.toList());
        stationName.setAdapter(new SpinnerStringAdapter(getContext(), names));

        Calendar calendar = Calendar.getInstance();
        if (fr != null) {
            //修改数据页面
            calendar = fr.getTime();
            totalMoney.setText(StrUtils.df4.format(fr.getMoney()));
            rise.setText(StrUtils.df4.format(fr.getRise()));
            originalPrice.setText(StrUtils.df4.format(fr.getMarketPrice()));
            odometerNumber.setText(StrUtils.df4.format(fr.getOdometerNumber()));
            stationName.setText(fr.getStationName());
            stationName.setHint(fr.getStationName());
        } else if (names.size() > 0) {
            stationName.setText(names.get(0));
            stationName.setHint(names.get(0));
        }
        date.setText(DateUtils.date2String(calendar));
        ok.setEnabled(false);
    }

    /**
     * 选择日期事件
     */
    @OnClick({R.id.date})
    void onDateClick() {
        DateUtils.showDateDialog(getContext(), date::setText);
        ok.setEnabled(true);
    }

    @OnTextChanged({R.id.totalMoney, R.id.rise, R.id.original_price, R.id.odometerNumber, R.id.station_name})
    void onTextChanged() {
        ok.setEnabled(true);
    }

    @OnClick({R.id.ok})
    void onOkClick() {
        fr = (fr == null) ? new FuelRecord() : fr;
        try {
            double   moneyNo          = totalMoney.getText().toString().equals("") ? 0 : Double.parseDouble(totalMoney.getText().toString());
            double   riseNo           = rise.getText().toString().equals("") ? 0 : Double.parseDouble(rise.getText().toString());
            double   originalPriceNo  = originalPrice.getText().toString().equals("") ? 0 : Double.parseDouble(originalPrice.getText().toString());
            int      odometerNumberNo = odometerNumber.getText().toString().equals("") ? 0 : Integer.parseInt(odometerNumber.getText().toString());

            fr.setTime(DateUtils.string2Date(date.getText().toString()));
            fr.setMoney(moneyNo);
            fr.setRise(riseNo);
            fr.setMarketPrice(originalPriceNo);
            fr.setOdometerNumber(odometerNumberNo);
            fr.setStationName(stationName.getText().toString());

            if (!isNewData) {
                if (DbBase.update(fr) > 0)
                    PageUtils.showMessage(getContext(), "修改记录成功");
                else
                    PageUtils.showMessage(getContext(), "修改记录失败");
            } else {
                if (DbBase.insert(fr) > 0)
                    PageUtils.showMessage(getContext(), "添加记录成功");
                else
                    PageUtils.showMessage(getContext(), "添加记录失败");
            }
            dismiss();
            activity.showList();
        } catch (NumberFormatException e) {
            PageUtils.showMessage(getContext(), "请输入有效数字");
        }
    }
}
