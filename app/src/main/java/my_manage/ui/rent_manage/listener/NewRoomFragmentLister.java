package my_manage.ui.rent_manage.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.stream.IntStream;

import my_manage.password_box.R;
import my_manage.pojo.PersonDetails;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.RoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.tool.menuEnum.CastUtils;
import my_manage.ui.rent_manage.fragment.DialogFragmentInsertPerson;
import my_manage.ui.rent_manage.fragment.FragmentRoomDetails;

public final class NewRoomFragmentLister implements TextWatcher {
    private FragmentRoomDetails fragment;
    private View                view;

    private boolean isChange = false;

    public NewRoomFragmentLister(FragmentRoomDetails fragment, View view) {
        this.fragment = fragment;
        this.view = view;
    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.rental_editRoom_changeBtn) {
            PageUtils.setEnable(fragment, true);
            this.view.findViewById(R.id.rental_editRoom_changeBtn).setVisibility(View.GONE);
            fragment.getOkBtn().setVisibility(View.VISIBLE);
            fragment.getOkBtn().setEnabled(false);
            PageUtils.setEnable(fragment, true);
        } else if (view.getId() == R.id.rental_editRoom_okBtn && isChange) {
            updateRoomDetails();
        } else if (view.getId() == R.id.rental_editRoom_add) {
            //增加租户资料
//            PersonListener.addPerson(fragment.getActivity(), fragment);
            new DialogFragmentInsertPerson(fragment.getContext(),fragment).show(fragment.getFragmentManager(),"");
        }
    }

    public void onDateTextViewClick(View view) {
        //弹出日期选择框，选择日期
        if (fragment.getOkBtn().getVisibility() == View.VISIBLE && (view.getId() == R.id.rental_editRoom_beginDate
                || view.getId() == R.id.rental_editRoom_payDate || view.getId() == R.id.rental_editRoom_propertyBeginDate
                || view.getId() == R.id.rental_editRoom_contract_BeginDate)) {
            int months = 0;
            if (view.getId() == R.id.rental_editRoom_beginDate) {
                months = Integer.parseInt(fragment.getRentalMonth().getSelectedItem().toString());
            } else if (view.getId() == R.id.rental_editRoom_propertyBeginDate) {
                months = Integer.parseInt(fragment.getPropertyMonth().getSelectedItem().toString());
            } else if (view.getId() == R.id.rental_editRoom_contract_BeginDate) {
                months = Integer.parseInt(fragment.getContractMonth().getSelectedItem().toString());
            }
            // 初始化日期
            DateUtils.showDateDialog(fragment.getContext(), ((TextView) this.view.findViewById(view.getId()))::setText
                    , str -> {
                        isChange = true;
                        this.view.findViewById(R.id.rental_editRoom_okBtn).setEnabled(true);
                        fragment.onFocusChange();
                    }, months);
        }
    }

    private void updateRoomDetails() {
        //判断租户是否为空，空则退出
        PersonDetails pd = (PersonDetails) fragment.getPerson().getSelectedItem();
        if (pd.getPrimary_id() == 0) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(fragment.getContext());
            dialog.setTitle("租户未选中").setMessage("未选中租户，请选择租户后再保存")
                    .setPositiveButton(R.string.ok_cn, null)
                    .show();
            return;
        }
        RoomDetails room = new RoomDetails();
        room.setRecordId(fragment.getShowRoomDetails().getRoomDetails().getRecordId());//记录的ID
        room.setCommunityName(fragment.getCommunity().getText().toString());//小区名
        room.setRoomNumber(fragment.getRoomNumber().getText().toString());//房号
        room.setWaterMeter(fragment.getWaterMeter().getText().toString());//水表
        room.setElectricMeter(fragment.getMeterNumber().getText().toString());//电表

        Pair<Boolean, Double> pp = CastUtils.parseDouble(fragment.getPropertyPrice().getText().toString());//转换物业费单价为double
        if (pp.first) {room.setPropertyPrice(pp.second);}
        Pair<Boolean, Double> ra = CastUtils.parseDouble(fragment.getArea().getText().toString());//面积转换
        if (ra.first) {room.setRoomArea(ra.second);}

//        room.setManId(pd.getPrimary_id());
        //更改租户信息
        updatePerson(pd);
        //将记录也一起更改
        updateRecord(room, pd.getPrimary_id());

        DbBase.update(room);
        fragment.getActivity().onBackPressed();
    }

    private void updatePerson(PersonDetails pd) {
        if (pd == null) return;
        String tel     = fragment.getTel().getText().toString();
        String cord    = fragment.getManCard().getText().toString();
        String other   = fragment.getRentalEditRoomPersonRemark().getText().toString();
        String company = fragment.getRentalEditRoomCompanyName().getText().toString();
        if (tel.equals(pd.getTel()) && cord.equals(pd.getCord()) && other.equals(pd.getOther()) && company.equals(pd.getCompany())) {
            //没有更改，退出
            return;
        }
        pd.setTel(tel);
        pd.setCord(cord);
        pd.setOther(other);
        pd.setCompany(company);
        DbBase.update(pd);
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
        rr.setStartDate(DateUtils.string2Date(fragment.getBeginDate().getText().toString()));
        //房租时长
        Pair<Boolean, Integer> pm = CastUtils.parseInt(fragment.getRentalMonth().getSelectedItem().toString());
        if (pm.first)
            rr.setPayMonth(pm.second);
        //转换物业费成int
        Pair<Boolean, Double> rm = CastUtils.parseDouble(fragment.getRentalMoney().getText().toString());
        if (rm.first)
            rr.setMonthlyRent(rm.second);
        //物业费开始时间
        rr.setRealtyStartDate(DateUtils.string2Date(fragment.getPropertyDate().getText().toString()));
        //合同开始时间
        rr.setContractSigningDate(DateUtils.string2Date(fragment.getContractDate().getText().toString()));
        //合同时长
        pm = CastUtils.parseInt(fragment.getContractMonth().getSelectedItem().toString());
        if (pm.first)
            rr.setContractMonth(pm.second);
        //付款时间
        rr.setPaymentDate(DateUtils.string2Date(fragment.getPayDate().getText().toString()));
        //总金额
        rm = CastUtils.parseDouble(fragment.getTotalMoney().getText().toString());
        if (rm.first)
            rr.setTotalMoney(rm.second);
        //押金
        pm = CastUtils.parseInt(fragment.getDeposit().getText().toString());
        if (pm.first)
            rr.setDeposit(pm.second);
        //物业费时长
        pm = CastUtils.parseInt(fragment.getPropertyMonth().getSelectedItem().toString());
        if (pm.first)
            rr.setPropertyTime(pm.second);
        rr.setManID(manId);
        //是否包含物业费
        rr.setIsContainRealty(fragment.getIsContainRealty().isChecked());
        rr.setRemarks(fragment.getRemark().getText().toString());
        String c2 = JSON.toJSONString(rr);
        if (!compareStr.equalsIgnoreCase(c2)) {
            if (rr.getPrimary_id() == 0) {
                //新建记录
                DbBase.insert(rr);
                //获取插入数据的主键
                int max = DbHelper.getInstance().getRecords().stream()
                        .flatMapToInt(rentalRecord -> IntStream.of(rentalRecord.getPrimary_id())).max().getAsInt();
                room.setRecordId(max);
            } else {
                //内容已更改，需要更新数据库
                DbBase.update(rr);
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

}
