package my_manage.ui.fuel.page;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.deadline.statebutton.StateButton;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.pojo.FuelRecord;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.fuel.listener.FuelListener;
import my_manage.ui.password_box.R;
import my_manage.ui.widght.DropEditText;
import my_manage.ui.widght.ParallaxSwipeBackActivity;

/**
 * @author inview
 * @Date 2020/11/24 11:07
 * @Description :
 */
public final class AddFuelRecordActivity extends ParallaxSwipeBackActivity implements IShowList {
    @BindView(R.id.toolbar)        Toolbar       toolbar;
    @BindView(R.id.date)           TextView      date;
    @BindView(R.id.totalMoney)     EditText      totalMoney;
    @BindView(R.id.rise)           EditText      rise;
    @BindView(R.id.original_price) EditText      originalPrice;
    @BindView(R.id.odometerNumber) EditText      odometerNumber;
    @BindView(R.id.station_name)   DropEditText  stationName;
    @BindView(R.id.ok)             StateButton   ok;
    private                        int           id  = 0;
    private                        DecimalFormat df4 = new DecimalFormat("###.##");
    private                        FuelRecord    fr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel_add);
        ButterKnife.bind(this);

        initToolBar();
        init();
    }

    private void initToolBar() {
        toolbar.setTitle("加油记录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void init() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        val          frList = DbHelper.getInstance().getFuelRecordList();
        List<String> names  = frList.stream().map(FuelRecord::getStationName).distinct().collect(Collectors.toList());
        stationName.setAdapter(new CommonAdapter<String>(this, android.R.layout.simple_list_item_1, names) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, String item, int position) {
                helper.setText(android.R.id.text1, item);
            }
        });

        Calendar calendar = Calendar.getInstance();
        if (id != 0) {
            //修改数据页面
            fr = frList.stream().filter(f -> f.getPrimary_id() == id).findFirst().orElse(new FuelRecord());
            calendar = fr.getTime();
            totalMoney.setText(df4.format(fr.getMoney()));
            rise.setText(df4.format(fr.getRise()));
            originalPrice.setText(df4.format(fr.getMarketPrice()));
            odometerNumber.setText(df4.format(fr.getOdometerNumber()));
            stationName.setText(fr.getStationName());
            stationName.setHint(fr.getStationName());
        } else if (names.size() > 0) {
            stationName.setText(names.get(0));
            stationName.setHint(names.get(0));
        }
        date.setText(DateUtils.string2DateString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0));
    }

    @Override
    public void showList() {

    }

    /**
     * 选择日期事件
     */
    @OnClick({R.id.date})
    void onDateClick(View view) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
            date.setText(DateUtils.string2DateString(year, monthOfYear, dayOfMonth, 0));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

//    @OnItemSelected(R.id.station_name_spinner)
//    void onItemSelected() {
//        if (stationNameSpinner.getSelectedItem() == null) return;
//        stationName.setText(stationNameSpinner.getSelectedItem().toString());
//    }

    @OnClick({R.id.ok})
    void onOkClick() {
        if (fr != null) {
            //判断数据是否更改，未更改则退出
            if (date.getText().toString().equals(DateUtils.date2String(fr.getTime()))
                    && df4.format(fr.getMoney()).equals(totalMoney.getText().toString())
                    && df4.format(fr.getRise()).equals(rise.getText().toString())
                    && df4.format(fr.getMarketPrice()).equals(originalPrice.getText().toString())
                    && String.valueOf(fr.getOdometerNumber()).equals(odometerNumber.getText().toString())
                    && fr.getStationName().equals(stationName.getText().toString())) {
                PageUtils.showMessage(this, "数据未变更，不预提交");
                return;
            }
        } else {
            fr = new FuelRecord();
        }
        try {
            Calendar calendar         = DateUtils.string2Date(date.getText().toString());
            double   moneyNo          = totalMoney.getText().toString().equals("") ? 0 : Double.parseDouble(totalMoney.getText().toString());
            double   riseNo           = rise.getText().toString().equals("") ? 0 : Double.parseDouble(rise.getText().toString());
            double   originalPriceNo  = originalPrice.getText().toString().equals("") ? 0 : Double.parseDouble(originalPrice.getText().toString());
            int      odometerNumberNo = odometerNumber.getText().toString().equals("") ? 0 : Integer.parseInt(odometerNumber.getText().toString());
            String   stationNameTxt   = stationName.getText().toString();

            fr.setTime(calendar);
            fr.setMoney(moneyNo);
            fr.setRise(riseNo);
            fr.setMarketPrice(originalPriceNo);
            fr.setOdometerNumber(odometerNumberNo);
            fr.setStationName(stationNameTxt);

            if (id > 0) {
                if (FuelListener.modifyFuelRecord(fr))
                    PageUtils.showMessage(this, "修改记录成功");
                else
                    PageUtils.showMessage(this, "修改记录失败");
            } else {
                if (FuelListener.addFuelRecord(fr))
                    PageUtils.showMessage(this, "添加记录成功");
                else
                    PageUtils.showMessage(this, "添加记录失败");
            }
            this.finishActivity(2020);
            this.finish();
        } catch (NumberFormatException e) {
            PageUtils.showMessage(this, "请输入有效数字");
        }
    }

}
