package my_manage.ui.car.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
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
import my_manage.pojo.CarMaintenanceRecord;
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
 * @Date 2020/11/27 14:25
 * @Description :
 */
public final class DialogFragmentCarModify extends MyDialogFragment {
    @BindView(R.id.toolbar)     Toolbar              toolbar;
    @BindView(R.id.tableLayout) TableLayout          tableLayout;
    @BindView(R.id.scrollView)  ScrollView           scrollView;
    private                     CarMaintenanceRecord car;
    private                     IShowList            show;

    public DialogFragmentCarModify(IShowList show, CarMaintenanceRecord car) {
        this.car = car;
        this.show = show;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car_main_list, container, false);
        bind = ButterKnife.bind(this, v);
        initToolBar();
        init(inflater, container);
        return v;
    }


    private void init(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.car_modify_details, container, false);
        scrollView.removeAllViews();
        scrollView.addView(view);
        new ViewHolder(view, this, car);
    }


    private void initToolBar() {
        if (car == null)
            toolbar.setTitle("添加车辆维护记录");
        else
            toolbar.setTitle("修改车辆维护记录");
        toolbar.setNavigationOnClickListener(v -> dismiss());
    }


    static
    class ViewHolder {
        @BindView(R.id.date)           TextView                date;
        @BindView(R.id.money)          EditText                money;
        @BindView(R.id.address)        DropEditText            address;
        @BindView(R.id.car_type_name)  DropEditText            carTypeName;
        @BindView(R.id.remark)         EditText                remark;
        @BindView(R.id.odometerNumber) EditText                odometerNumber;
        @BindView(R.id.ok)             StateButton             ok;
        private                        DialogFragmentCarModify activity;
        private                        CarMaintenanceRecord    car;

        ViewHolder(View view, DialogFragmentCarModify dialogFragmentCarModify, CarMaintenanceRecord car) {
            ButterKnife.bind(this, view);
            this.activity = dialogFragmentCarModify;
            this.car = car;
            val carLst = DbHelper.getInstance().getCarMaintenanceList();
            List<String> types = carLst.stream().map(CarMaintenanceRecord::getMaintenanceType)
                    .distinct().collect(Collectors.toList());
            carTypeName.setAdapter(new SpinnerStringAdapter(activity.getContext(), types));
            List<String> addressLst = carLst.stream().map(CarMaintenanceRecord::getAddress)
                    .distinct().collect(Collectors.toList());
            address.setAdapter(new SpinnerStringAdapter(activity.getContext(), addressLst));

            if (car != null) {
                date.setText(DateUtils.date2String(car.getDate()));
                address.setText(car.getAddress());
                money.setText(StrUtils.df4.format(car.getMoney()));
                carTypeName.setText(car.getMaintenanceType());
                remark.setText(car.getRemark());
                odometerNumber.setText(StrUtils.df4.format(car.getOdometerNumber()));
            } else {
                date.setText(DateUtils.date2String(Calendar.getInstance()));
            }
            ok.setEnabled(false);
        }

        @OnClick({R.id.date})
        void onDateClick() {
            DateUtils.showDateDialog(activity.getContext(),date::setText);
        }

        @OnClick(R.id.ok)
        void onClick() {
            try {
                if (car == null) car = new CarMaintenanceRecord();
                car.setRemark(remark.getText().toString());
                String moneyString = StrUtils.isBlank(money.getText().toString()) ? "0" : money.getText().toString();
                car.setMoney(Double.parseDouble(moneyString));
                car.setMaintenanceType(carTypeName.getText().toString());
                car.setAddress(address.getText().toString());
                car.setDate(DateUtils.string2Date(date.getText().toString()));
                String odometerNumberString = StrUtils.isBlank(odometerNumber.getText().toString()) ? "0" : odometerNumber.getText().toString();
                car.setOdometerNumber(Integer.parseInt(odometerNumberString));
                if (car.getPrimary_id() != 0) {
                    if (DbBase.update(car) > 0) {
                        PageUtils.showMessage(activity.getContext(), "更改成功");
                    } else {
                        PageUtils.showMessage(activity.getContext(), "更改失败");
                    }
                } else {
                    if (DbBase.insert(car) > 0) {
                        PageUtils.showMessage(activity.getContext(), "添加成功");
                    } else {
                        PageUtils.showMessage(activity.getContext(), "添加失败");
                    }
                }
            } catch (NumberFormatException e) {
                PageUtils.Error(activity.getClass().getSimpleName() + "数字转换错误");
            }
            activity.show.showList();
            activity.dismiss();
        }

        @OnTextChanged({R.id.date, R.id.address, R.id.money, R.id.car_type_name, R.id.remark})
        void onTextChanged() {
            ok.setEnabled(true);
        }
    }
}
