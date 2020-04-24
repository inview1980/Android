package my_manage.rent_manage.page;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Calendar;
import java.util.List;

import my_manage.adapter.ManAdapter;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;

public final class NewRoom extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView community;
    private EditText roomNumber;
    private EditText realtyMoney;//月租金
    private EditText propertyPrice;//物业费单价
    private EditText meterNumber;
    private EditText area;
    private Spinner person;
    private EditText tel;
    private EditText manCard;
    private CheckBox isContainRealty;
    private TextView beginDate;//房租开始时间
    private TextView endDate;//房租结束时间
    private TextView proBeginDate;//物业费开始时间
    private TextView proEndDate;
    private Button changeBtn;
    private Button okBtn;

    private void init() {
        community = findViewById(R.id.rental_editRoom_community);
        roomNumber = findViewById(R.id.rental_editRoom_roomNumber);
        realtyMoney = findViewById(R.id.rental_editRoom_realtyMoney);
        propertyPrice = findViewById(R.id.rental_editRoom_propertyPrice);
        meterNumber = findViewById(R.id.rental_editRoom_meterNumber);
        area = findViewById(R.id.rental_editRoom_area);
        person = findViewById(R.id.rental_editRoom_person);
        final Button addBtn = findViewById(R.id.rental_editRoom_add);
        tel = findViewById(R.id.rental_editRoom_tel);
        manCard = findViewById(R.id.rental_editRoom_manCardNumber);
        isContainRealty = findViewById(R.id.rental_editRoom_isContainRealty);
        beginDate = findViewById(R.id.rental_editRoom_beginDate);
        endDate = findViewById(R.id.rental_editRoom_endDate);
        proBeginDate = findViewById(R.id.rental_editRoom_propertyBeginDate);
        proEndDate = findViewById(R.id.rental_editRoom_propertyEndDate);
        changeBtn = findViewById(R.id.rental_editRoom_changeBtn);
        okBtn = findViewById(R.id.rental_editRoom_okBtn);
        final Button cancelBtn = findViewById(R.id.rental_editRoom_cancelBtn);

        changeBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        beginDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        proBeginDate.setOnClickListener(this);
        proEndDate.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        initSpinner();
        community.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        roomNumber.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        realtyMoney.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        propertyPrice.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        meterNumber.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        area.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        tel.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
        manCard.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
    }

    private void initSpinner() {
        List<PersonDetails> persons = DbHelper.getInstance().getPersonList();
        ManAdapter adapter = new ManAdapter(this, android.R.layout.simple_list_item_1, persons);
        person.setAdapter(adapter);
    }

    private void setEnable(boolean b) {
        community.setEnabled(b);
        roomNumber.setEnabled(b);
        realtyMoney.setEnabled(b);
        propertyPrice.setEnabled(b);
        meterNumber.setEnabled(b);
        area.setEnabled(b);
        person.setEnabled(b);
        tel.setEnabled(b);
        manCard.setEnabled(b);
        isContainRealty.setEnabled(b);
        beginDate.setEnabled(b);
        endDate.setEnabled(b);
        proBeginDate.setEnabled(b);
        proEndDate.setEnabled(b);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_room_details);

        init();
        Intent intent = getIntent();
        String extra = intent.getStringExtra("ShowRoomDetails");
        if (StrUtils.isNotBlank(extra)) {
            ShowRoomDetails room = JSON.parseObject(extra, ShowRoomDetails.class);
            if (room != null) {
                changeBtn.setVisibility(View.VISIBLE);
                setValue(room);
            }
            // TODO: 2020/4/22  
        } else {
            //新增
            setEnable(true);
            okBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setValue(ShowRoomDetails room) {
        community.setText(room.getCommunityName());
        roomNumber.setText(room.getRoomNumber());
        realtyMoney.setText("" + room.getRentalMoney());
        propertyPrice.setText("" + room.getPropertyPrice());
        meterNumber.setText(room.getMeterNumber());
        area.setText("" + room.getRoomArea());
//        person
        tel.setText(room.getPersonDetails() == null ? "" : room.getPersonDetails().getTel());
        manCard.setText(room.getPersonDetails() == null ? "" : room.getPersonDetails().getCord());
        if (room.getRentalRecord() != null) {
            isContainRealty.setChecked(room.getRentalRecord().getIsContainRealty());
            beginDate.setText(date2String(room.getRentalRecord().getStartDate()));
            endDate.setText(date2String(room.getRentalRecord().getStartDate(), room.getRentalRecord().getPayMonth()));
            proBeginDate.setText(date2String(room.getRentalRecord().getRealtyStartDate()));
            proEndDate.setText(date2String(room.getRentalRecord().getRealtyStartDate(), room.getRentalRecord().getRealtyMonth()));
        }
    }

    private String date2String(Calendar startDate, int payMonth) {
        if (startDate == null) return "";
        startDate.add(Calendar.MONTH, payMonth);
        return date2String(startDate);
    }

    private String date2String(Calendar date) {
        if (date == null) return "";
        return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rental_editRoom_cancelBtn) {
            finish();
        }
        if (view.getId() == R.id.rental_editRoom_changeBtn) {
            setEnable(true);
            changeBtn.setVisibility(View.GONE);
            okBtn.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.rental_editRoom_okBtn) {
            // TODO: 2020/4/24
        }
        if (view.getId() == R.id.rental_editRoom_beginDate || view.getId() == R.id.rental_editRoom_endDate
                || view.getId() == R.id.rental_editRoom_propertyBeginDate || view.getId() == R.id.rental_editRoom_propertyEndDate) {
            // 初始化日期
            Calendar myCalendar = Calendar.getInstance();
            int myYear = myCalendar.get(Calendar.YEAR);
            int month = myCalendar.get(Calendar.MONTH);
            int day = myCalendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.BUTTON_POSITIVE, (view1, year, monthOfYear, dayOfMonth) ->
                    ((TextView) findViewById(view.getId())).setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth), myYear, month, day);
            dpd.show();
        }
        if (view.getId() == R.id.rental_editRoom_add) {
            //增加租户资料
            addPerson();
        }
    }

    private void addPerson() {
        ViewHolder viewHolder=new ViewHolder(R.layout.add_person_dialog);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setOnClickListener((dialog1, v) -> {
                    try {
                        if (v.getId() == R.id.rental_addPerson_ok) {
                            EditText name = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_name);
                            EditText tel = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_tel);
                            EditText code = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_code);
                            name.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
                            tel.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
                            code.setOnFocusChangeListener((a,b)->PageUtils.closeInput(this,b));
                            if (StrUtils.isNotBlank(name.getText().toString())) {
                                PersonDetails personDetails=new PersonDetails(name.getText().toString());
                                personDetails.setCord(code.getText().toString());
                                personDetails.setTel(tel.getText().toString());

                                if (RentDB.insert(personDetails) > 0) {
                                    //成功，刷新
                                    initSpinner();
                                    dialog1.dismiss();
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }).setHeader(android.R.layout.browser_link_context_header)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setContentHolder(viewHolder)
                .create();
        dialog.show();
    }
}
