package my_manage.rent_manage.page;

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

import my_manage.password_box.R;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.StrUtils;

public final class NewRoom extends AppCompatActivity {
    private AutoCompleteTextView community;
    private EditText roomNumber;
    private EditText realtyMoney;//月租金
    private EditText propertyPrice;//物业费单价
    private EditText meterNumber;
    private EditText area;
    private Spinner person;
    private Button addBtn;
    private EditText tel;
    private EditText manCard;
    private CheckBox isContainRealty;
    private TextView beginDate;//房租开始时间
    private TextView endDate;//房租结束时间
    private TextView proBeginDate;//物业费开始时间
    private TextView proEndDate;
    private Button changeBtn;
    private Button okBtn;
    private Button cancelBtn;

    private void init() {
        community = findViewById(R.id.rental_editRoom_community);
        roomNumber = findViewById(R.id.rental_editRoom_roomNumber);
        realtyMoney = findViewById(R.id.rental_editRoom_realtyMoney);
        propertyPrice = findViewById(R.id.rental_editRoom_propertyPrice);
        meterNumber = findViewById(R.id.rental_editRoom_meterNumber);
        area = findViewById(R.id.rental_editRoom_area);
        person = findViewById(R.id.rental_editRoom_person);
        addBtn = findViewById(R.id.rental_editRoom_add);
        tel = findViewById(R.id.rental_editRoom_tel);
        manCard = findViewById(R.id.rental_editRoom_manCardNumber);
        isContainRealty = findViewById(R.id.rental_editRoom_isContainRealty);
        beginDate = findViewById(R.id.rental_editRoom_beginDate);
        endDate = findViewById(R.id.rental_editRoom_endDate);
        proBeginDate = findViewById(R.id.rental_editRoom_propertyBeginDate);
        proEndDate = findViewById(R.id.rental_editRoom_propertyEndDate);
        changeBtn = findViewById(R.id.rental_editRoom_changeBtn);
        okBtn = findViewById(R.id.rental_editRoom_okBtn);
        cancelBtn = findViewById(R.id.rental_editRoom_cancelBtn);
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
        Intent intent=getIntent();
        String extra = intent.getStringExtra("ShowRoomDetails");
        if (StrUtils.isNotBlank(extra)) {
            ShowRoomDetails room = JSON.parseObject(extra, ShowRoomDetails.class);
            if (room != null) {
                changeBtn.setVisibility(View.VISIBLE);
                setValue(room);
            }
            // TODO: 2020/4/22  
        }else {
            //新增
            setEnable(true);
            okBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setValue(ShowRoomDetails room) {
        community.setText(room.getCommunityName());
        roomNumber.setText(room.getRoomNumber());
        realtyMoney.setText(""+room.getRentalMoney());
        propertyPrice.setText(""+room.getPropertyPrice());
        meterNumber.setText(room.getMeterNumber());
        area.setText(""+room.getRoomArea());
//        person
        tel.setText(room.getPersonDetails()==null?"":room.getPersonDetails().getTel());
        manCard.setText(room.getPersonDetails()==null?"":room.getPersonDetails().getCord());
        isContainRealty.setChecked(room.getRentalRecord()==null?false:room.getRentalRecord().getIsContainRealty());
//        beginDate
//        endDate
//        proBeginDate
//        proEndDate
    }
}
