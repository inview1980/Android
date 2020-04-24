package my_manage.rent_manage.page;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
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
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.menuEnum.CastUtils;

public final class NewRoom extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private AutoCompleteTextView community;
    private EditText roomNumber;
    private EditText rentalMoney;//月租金
    private EditText propertyPrice;//物业费单价
    private EditText meterNumber;
    private EditText area;
    private Spinner person;
    private EditText tel;
    private EditText manCard;
    private CheckBox isContainRealty;
    private TextView beginDate;//房租开始时间
    private EditText rentalMonth;//房租结束时间
    private TextView proBeginDate;//物业费开始时间
    private TextView payDate;//交费时间
    private EditText remark;//备注
    private EditText proMonth;
    private Button changeBtn;
    private Button okBtn;

    private ShowRoomDetails showRoomDetails;
    private boolean isChange = false;

    private void init() {
        community = findViewById(R.id.rental_editRoom_community);
        roomNumber = findViewById(R.id.rental_editRoom_roomNumber);
        rentalMoney = findViewById(R.id.rental_editRoom_realtyMoney);
        propertyPrice = findViewById(R.id.rental_editRoom_propertyPrice);
        meterNumber = findViewById(R.id.rental_editRoom_meterNumber);
        area = findViewById(R.id.rental_editRoom_area);
        person = findViewById(R.id.rental_editRoom_person);
        final Button addBtn = findViewById(R.id.rental_editRoom_add);
        tel = findViewById(R.id.rental_editRoom_tel);
        manCard = findViewById(R.id.rental_editRoom_manCardNumber);
        isContainRealty = findViewById(R.id.rental_editRoom_isContainRealty);
        beginDate = findViewById(R.id.rental_editRoom_beginDate);
        rentalMonth = findViewById(R.id.rental_editRoom_rentalMonth);
        proBeginDate = findViewById(R.id.rental_editRoom_propertyBeginDate);
        proMonth = findViewById(R.id.rental_editRoom_propertyMonth);
        changeBtn = findViewById(R.id.rental_editRoom_changeBtn);
        okBtn = findViewById(R.id.rental_editRoom_okBtn);
        final Button cancelBtn = findViewById(R.id.rental_editRoom_cancelBtn);
        payDate = findViewById(R.id.rental_editRoom_payDate);
        remark = findViewById(R.id.rental_editRoom_remark);

        changeBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        beginDate.setOnClickListener(this);
        proBeginDate.setOnClickListener(this);
        payDate.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        initSpinner();
        community.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        roomNumber.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        rentalMoney.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        propertyPrice.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        meterNumber.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        area.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        tel.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        manCard.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        rentalMonth.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        proMonth.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
        remark.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));

        community.addTextChangedListener(this);
        roomNumber.addTextChangedListener(this);
        rentalMoney.addTextChangedListener(this);
        propertyPrice.addTextChangedListener(this);
        meterNumber.addTextChangedListener(this);
        area.addTextChangedListener(this);
        tel.addTextChangedListener(this);
        manCard.addTextChangedListener(this);
        proMonth.addTextChangedListener(this);
        rentalMonth.addTextChangedListener(this);
        remark.addTextChangedListener(this);
        isContainRealty.setOnCheckedChangeListener((compoundButton, b) -> afterTextChanged(null));
        person.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                afterTextChanged(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSpinner() {
        List<PersonDetails> persons = DbHelper.getInstance().getPersonList();
        ManAdapter adapter = new ManAdapter(this, android.R.layout.simple_list_item_1, persons);
        person.setAdapter(adapter);
    }

    private void setEnable(boolean b) {
        community.setEnabled(b);
        roomNumber.setEnabled(b);
        rentalMoney.setEnabled(b);
        propertyPrice.setEnabled(b);
        meterNumber.setEnabled(b);
        area.setEnabled(b);
        person.setEnabled(b);
        tel.setEnabled(b);
        manCard.setEnabled(b);
        isContainRealty.setEnabled(b);
        beginDate.setEnabled(b);
        rentalMonth.setEnabled(b);
        proBeginDate.setEnabled(b);
        proMonth.setEnabled(b);
        payDate.setEnabled(b);
        remark.setEnabled(b);
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
                this.showRoomDetails = room;
            }
        } else {
            //新增
            setEnable(true);
            okBtn.setVisibility(View.VISIBLE);
        }
//        person.setOnItemClickListener((adapterView, view, i, l) -> afterTextChanged(null));

    }

    private void setValue(ShowRoomDetails room) {
        community.setText(room.getCommunityName());
        roomNumber.setText(room.getRoomNumber());
        rentalMoney.setText("" + room.getRentalMoney());
        propertyPrice.setText("" + room.getPropertyPrice());
        meterNumber.setText(room.getMeterNumber());
        area.setText("" + room.getRoomArea());

        tel.setText(room.getPersonDetails() == null ? "" : room.getPersonDetails().getTel());
        manCard.setText(room.getPersonDetails() == null ? "" : room.getPersonDetails().getCord());
        if (room.getRentalRecord() != null) {
            isContainRealty.setChecked(room.getRentalRecord().getIsContainRealty());
            beginDate.setText(date2String(room.getRentalRecord().getStartDate()));
            rentalMonth.setText(room.getRentalRecord().getPayMonth() + "");
            proBeginDate.setText(date2String(room.getRentalRecord().getRealtyStartDate()));
            proMonth.setText(room.getRentalRecord().getRealtyMonth() + "");
            payDate.setText(date2String(room.getRentalRecord().getPaymentDate()));
            remark.setText(room.getRentalRecord().getRemarks());
        }
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
            okBtn.setEnabled(false);
        } else if (view.getId() == R.id.rental_editRoom_okBtn && isChange) {
            updateRoomDetails();
            // TODO: 2020/4/24
        }
        if (view.getId() == R.id.rental_editRoom_beginDate || view.getId() == R.id.rental_editRoom_payDate
                || view.getId() == R.id.rental_editRoom_propertyBeginDate) {
            // 初始化日期
            Calendar myCalendar = Calendar.getInstance();
            int myYear = myCalendar.get(Calendar.YEAR);
            int month = myCalendar.get(Calendar.MONTH);
            int day = myCalendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, DatePickerDialog.BUTTON_POSITIVE, (view1, year, monthOfYear, dayOfMonth) -> {
                ((TextView) findViewById(view.getId())).setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth);
                isChange = true;
                okBtn.setEnabled(true);
            }, myYear, month, day);
            dpd.show();
        }
        if (view.getId() == R.id.rental_editRoom_add) {
            //增加租户资料
            addPerson();
        }
    }

    private void updateRoomDetails() {
        RoomDetails room = new RoomDetails();
        room.setCommunityName(community.getText().toString());
        room.setRoomNumber(roomNumber.getText().toString());
        if (StrUtils.isBlank(room.getCommunityName())) {
            showMyDialog("社区不能为空");
            return;
        }
        if (StrUtils.isBlank(room.getRoomNumber())) {
            showMyDialog("房号不能为空");
            return;
        }

        Pair<Boolean, Integer> rm = CastUtils.parseInt(rentalMoney.getText().toString());//转换物业费成int
        if (rm.first)
            room.setRentalMoney(rm.second);
        room.setMeterNumber(meterNumber.getText().toString());
        Pair<Boolean, Double> pp = CastUtils.parseDouble(propertyPrice.getText().toString());//转换物业费单价为double
        if (pp.first)
            room.setPropertyPrice(pp.second);
        Pair<Boolean, Double> ra = CastUtils.parseDouble(area.getText().toString());//面积转换
        if (ra.first)
            room.setRoomArea(ra.second);
        PersonDetails pd = (PersonDetails) person.getSelectedItem();
        room.setManId(pd.getPrimary_id());
        room.setRecordId(showRoomDetails.getRentalRecord().getPrimary_id());
        RentDB.update(room);
        //将记录也一起更改
        updateRecord(room, pd.getPrimary_id());
        finish();
    }

    private void showMyDialog(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("错误");
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
        }).show();
    }

    private void updateRecord(RoomDetails room, int manId) {
        RentalRecord rr = showRoomDetails.getRentalRecord();
        //将记录转换为JSON以方便后期进行比较，以判断是否更改
        String compareStr = JSON.toJSONString(rr);

        //房号
        rr.setRoomNumber(room.getRoomNumber());
        //房租开始时间
        rr.setStartDate(string2Date(beginDate.getText().toString()));
        //房租时长
        Pair<Boolean, Integer> pm = CastUtils.parseInt(rentalMonth.getText().toString());
        if (pm.first)
            rr.setPayMonth(pm.second);
        //物业费开始时间
        rr.setRealtyStartDate(string2Date(proBeginDate.getText().toString()));
        //付款时间
        rr.setPaymentDate(string2Date(payDate.getText().toString()));
        //物业费时长
        pm = CastUtils.parseInt(proMonth.getText().toString());
        if (pm.first)
            rr.setRealtyMonth(pm.second);
        rr.setManID(manId);
        //是否包含物业费
        rr.setIsContainRealty(isContainRealty.isChecked());
        rr.setRemarks(remark.getText().toString());
        String c2 = JSON.toJSONString(rr);
        if (!compareStr.equalsIgnoreCase(c2)) {
            //内容已更改，需要更新数据库
            RentDB.update(rr);
        }
    }

    private Calendar string2Date(String text) {
        if (StrUtils.isBlank(text)) return null;
        String[] ss = text.split("-");
        if (ss.length >= 3) {
            Pair<Boolean, Integer> year = CastUtils.parseInt(ss[0]);
            Pair<Boolean, Integer> month = CastUtils.parseInt(ss[1]);
            Pair<Boolean, Integer> day = CastUtils.parseInt(ss[2]);
            if (year.first && month.first && day.first) {
                Calendar c = Calendar.getInstance();
                c.set(year.second, month.second + 1, day.second);
                return c;
            }
        }
        return null;
    }


    private void addPerson() {
        ViewHolder viewHolder = new ViewHolder(R.layout.add_person_dialog);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setOnClickListener((dialog1, v) -> {
                    try {
                        if (v.getId() == R.id.rental_addPerson_ok) {
                            EditText name = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_name);
                            EditText tel = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_tel);
                            EditText code = viewHolder.getInflatedView().findViewById(R.id.rental_addPerson_code);
                            name.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
                            tel.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
                            code.setOnFocusChangeListener((a, b) -> PageUtils.closeInput(this, b));
                            if (StrUtils.isNotBlank(name.getText().toString())) {
                                PersonDetails personDetails = new PersonDetails(name.getText().toString());
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        isChange = true;
        okBtn.setEnabled(true);
    }
}
