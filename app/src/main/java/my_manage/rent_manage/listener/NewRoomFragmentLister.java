package my_manage.rent_manage.listener;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.stream.IntStream;

import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.fragment.NewRoomFragment;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.tool.StrUtils;
import my_manage.tool.menuEnum.CastUtils;

public final class NewRoomFragmentLister implements View.OnClickListener, TextWatcher {
    private NewRoomFragment fragment;
    private View            view;

    private boolean isChange = false;

    public NewRoomFragmentLister(NewRoomFragment fragment, View view) {
        this.fragment = fragment;
        this.view = view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rental_editRoom_cancelBtn) {
            fragment.getActivity().onBackPressed();
        }
        if (view.getId() == R.id.rental_editRoom_changeBtn) {
            fragment.setEnable(true);
            this.view.findViewById(R.id.rental_editRoom_changeBtn).setVisibility(View.GONE);
            fragment.getOkBtn().setVisibility(View.VISIBLE);
            fragment.getOkBtn().setEnabled(false);
        } else if (view.getId() == R.id.rental_editRoom_okBtn && isChange) {
            updateRoomDetails();
        }
        //弹出日期选择框，选择日期
        if (view.getId() == R.id.rental_editRoom_beginDate || view.getId() == R.id.rental_editRoom_payDate
                || view.getId() == R.id.rental_editRoom_propertyBeginDate) {
            // 初始化日期
            Calendar myCalendar = Calendar.getInstance();
            int      myYear     = myCalendar.get(Calendar.YEAR);
            int      month      = myCalendar.get(Calendar.MONTH);
            int      day        = myCalendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(fragment.getActivity(), DatePickerDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
                ((TextView) this.view.findViewById(view.getId())).setText(year + "-" + (1 + monthOfYear) + "-" + dayOfMonth);
                isChange = true;
                this.view.findViewById(R.id.rental_editRoom_okBtn).setEnabled(true);
            }, myYear, month, day);
            dpd.show();
        }
        //增加出租户资料
        if (view.getId() == R.id.rental_editRoom_add) {
            //增加租户资料
             PersonExtendableListViewAdapterListener.addPerson(fragment.getActivity(),fragment);
        }
    }

    private void updateRoomDetails() {
        //判断租户是否为空，空则退出
        PersonDetails pd = (PersonDetails) fragment.getPerson().getSelectedItem();
        if(pd.getPrimary_id()==0){
            androidx.appcompat.app.AlertDialog.Builder dialog=new androidx.appcompat.app.AlertDialog.Builder(fragment.getContext());
            dialog.setTitle("租户未选中").setMessage("未选中租户，请选择租户后再保存")
                    .setPositiveButton(R.string.ok_cn,null)
                    .show();
            return;
        }
        RoomDetails room = new RoomDetails();
        room.setRecordId(fragment.getShowRoomDetails().getRecordId());//记录的ID
        room.setCommunityName(fragment.getCommunity().getText().toString());//小区名
        room.setRoomNumber(fragment.getRoomNumber().getText().toString());//房号
        room.setWaterMeter(fragment.getWaterMeter().getText().toString())   ;//水表
        room.setElectricMeter(fragment.getMeterNumber().getText().toString());//电表
        if (StrUtils.isBlank(room.getCommunityName())) {
            showMyDialog("社区不能为空");
            return;
        }
        if (StrUtils.isBlank(room.getRoomNumber())) {
            showMyDialog("房号不能为空");
            return;
        }
        Pair<Boolean, Integer> rm = CastUtils.parseInt(fragment.getRentalMoney().getText().toString());//转换物业费成int
        if (rm.first) {room.setRentalMoney(rm.second);}
        Pair<Boolean, Double> pp = CastUtils.parseDouble(fragment.getPropertyPrice().getText().toString());//转换物业费单价为double
        if (pp.first) {room.setPropertyPrice(pp.second);}
        Pair<Boolean, Double> ra = CastUtils.parseDouble(fragment.getArea().getText().toString());//面积转换
        if (ra.first) {room.setRoomArea(ra.second);}

        room.setManId(pd.getPrimary_id());
        //更改租户信息
        updatePerson(pd);
        //将记录也一起更改
        updateRecord(room, pd.getPrimary_id());

        RentDB.update(room);
        fragment.getActivity().onBackPressed();
    }

    private void updatePerson(PersonDetails pd) {
        if (pd == null) return;
        String tel   = fragment.getTel().getText().toString();
        String cord  = fragment.getManCard().getText().toString();
        String other = fragment.getRentalEditRoomPersonRemark().getText().toString();
        if (tel.equals(pd.getTel()) && cord.equals(pd.getCord()) && other.equals(pd.getOther())) {
            //没有更改，退出
            return;
        }
        pd.setTel(tel);
        pd.setCord(cord);
        pd.setOther(other);
        RentDB.update(pd);
    }

    private void updateRecord(RoomDetails room, int manId) {
        if (fragment.getShowRoomDetails().getRentalRecord() == null)
            fragment.getShowRoomDetails().setRentalRecord(new RentalRecord());
        RentalRecord rr = fragment.getShowRoomDetails().getRentalRecord();
        //将记录转换为JSON以方便后期进行比较，以判断是否更改
        String compareStr = JSON.toJSONString(rr);

        //房号
        rr.setRoomNumber(room.getRoomNumber());
        //房租开始时间
        rr.setStartDate(string2Date(fragment.getBeginDate().getText().toString()));
        //房租时长
        Pair<Boolean, Integer> pm = CastUtils.parseInt(fragment.getRentalMonth().getText().toString());
        if (pm.first)
            rr.setPayMonth(pm.second);
        //物业费开始时间
        rr.setRealtyStartDate(string2Date(fragment.getProBeginDate().getText().toString()));
        //付款时间
        rr.setPaymentDate(string2Date(fragment.getPayDate().getText().toString()));
        //物业费时长
        pm = CastUtils.parseInt(fragment.getProMonth().getText().toString());
        if (pm.first)
            rr.setRealtyMonth(pm.second);
        rr.setManID(manId);
        //是否包含物业费
        rr.setIsContainRealty(fragment.getIsContainRealty().isChecked());
        rr.setRemarks(fragment.getRemark().getText().toString());
        String c2 = JSON.toJSONString(rr);
        if (!compareStr.equalsIgnoreCase(c2)) {
            if (rr.getPrimary_id() == 0) {
                //新建记录
                RentDB.insert(rr);
                //获取插入数据的主键
                int max = DbHelper.getInstance().getRecords().stream()
                        .flatMapToInt(rentalRecord -> IntStream.of(rentalRecord.getPrimary_id())).max().getAsInt();
                room.setRecordId(max);
            } else {
                //内容已更改，需要更新数据库
                RentDB.update(rr);
            }
        }
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
        fragment.getOkBtn().setEnabled(true);
    }


    private void showMyDialog(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.fragment.getContext());
        dialog.setTitle("错误");
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.ok_cn, (dialogInterface, i) -> {
        }).show();
    }

    private Calendar string2Date(String text) {
        if (StrUtils.isBlank(text)) return null;
        String[] ss = text.split("-");
        if (ss.length >= 3) {
            Pair<Boolean, Integer> year  = CastUtils.parseInt(ss[0]);
            Pair<Boolean, Integer> month = CastUtils.parseInt(ss[1]);
            Pair<Boolean, Integer> day   = CastUtils.parseInt(ss[2]);
            if (year.first && month.first && day.first) {
                Calendar c = Calendar.getInstance();
                c.set(year.second, month.second + 1, day.second);
                return c;
            }
        }
        return null;
    }


}
