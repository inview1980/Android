package my_manage.ui.living_expenses.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.apache.log4j.varia.FallbackErrorHandler;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.LivingExpenses;
import my_manage.pojo.RoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyDialogFragment;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/28 16:20
 * @Description :
 */
public class DialogFragmentLivingAddOrUpdate extends MyDialogFragment {
    @BindView(R.id.toolbar)           Toolbar        toolbar;
    @BindView(R.id.typeSpanner)       Spinner        typeSpanner;
    @BindView(R.id.communitySpanner)  Spinner        communitySpanner;
    @BindView(R.id.roomNumberSpanner) Spinner        roomNumberSpanner;
    @BindView(R.id.payDate)           TextView       payDate;
    @BindView(R.id.payMoney)          EditText       payMoney;
    @BindView(R.id.remark)            EditText       remark;
    @BindView(R.id.ok)                Button         ok;
    private                           String         typeName;
    private                           List<String>   communityLst;
    private                           List<String>   roomLst;
    private                           LivingExpenses livingExpenses;
    private                           IShowList      iShowList;

    public DialogFragmentLivingAddOrUpdate(IShowList iShowList, String typeName, LivingExpenses livingExpenses) {
        this.typeName = typeName;
        this.iShowList = iShowList;
        this.livingExpenses = livingExpenses;
    }

    public DialogFragmentLivingAddOrUpdate(IShowList iShowList, String typeName) {
        this.iShowList = iShowList;
        this.typeName = typeName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.live_add_item, container, false);
        bind = ButterKnife.bind(this, v);
        if (livingExpenses == null)
            toolbar.setTitle("增加记录");
        else
            toolbar.setTitle("修改记录");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());

        init();
        ok.setEnabled(false);
        return v;
    }

    private void init() {
        List<LivingExpenses> liveLst = DbHelper.getInstance().getAllLive();
        val                  typeLst = liveLst.stream().map(LivingExpenses::getProject).distinct().sorted().collect(Collectors.toList());
        typeSpanner.setAdapter(new SpinnerStringAdapter(getContext(), typeLst));
        if (StrUtils.isNotBlank(this.typeName)) {
            int index = typeLst.indexOf(this.typeName);
            typeSpanner.setSelection(index == -1 ? 0 : index,true);
        }

        communityLst = DbHelper.getInstance().getCommunityNames();
        communitySpanner.setAdapter(new SpinnerStringAdapter(getContext(), communityLst));

        payDate.setText(DateUtils.date2String(Calendar.getInstance()));
        if (livingExpenses != null) {
            String communityName = DbBase.getQueryByTime(RoomDetails.class, "roomNumber", new Object[]{livingExpenses.getRoomNumber()})
                    .get(0).getCommunityName();
            int index = communityLst.indexOf(communityName);
            communitySpanner.setSelection(index,true);
            //初始化roomNumberSpanner控件
            onCommunityItemSelected(index);
            int roomIndex = roomLst.indexOf(livingExpenses.getRoomNumber());
            roomNumberSpanner.setSelection(roomIndex, true);
            payDate.setText(DateUtils.date2String(livingExpenses.getPaymentDate()));
            payMoney.setText(StrUtils.df4.format(livingExpenses.getTotalMoney()));
            remark.setText(livingExpenses.getRemarks());
        }

        communitySpanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onCommunityItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        typeSpanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ok.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        roomNumberSpanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ok.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void onCommunityItemSelected(int position) {
        roomLst = DbHelper.getInstance().getRoomDetailsAll().stream()
                .filter(room -> room.getCommunityName().equals(communityLst.get(position)))
                .map(RoomDetails::getRoomNumber).collect(Collectors.toList());
        roomNumberSpanner.setAdapter(new SpinnerStringAdapter(getContext(), roomLst));
    }

    @OnClick(R.id.payDate)
    void onDateClick() {
        // 初始化日期
        DateUtils.showDateDialog(getContext(), payDate::setText);
    }

    @OnTextChanged({R.id.payMoney, R.id.remark, R.id.payDate})
    void onTextChange() {
        ok.setEnabled(true);
    }


    @OnClick(R.id.ok)
    void onOkClick() {
        // TODO: 2020/12/28
        LivingExpenses leTmp = LivingExpenses.builder().project(typeSpanner.getSelectedItem().toString())
                .roomNumber(roomNumberSpanner.getSelectedItem().toString())
                .paymentDate(DateUtils.string2Date(payDate.getText().toString()).getTimeInMillis())
                .totalMoney((int) Double.parseDouble(payMoney.getText().toString()) * 100)
                .remarks(remark.getText().toString())
                .build();
        if (livingExpenses == null) {
            //新增
            if (DbBase.insert(leTmp) > 0) {
                PageUtils.showMessage(getContext(), "新增成功。");
                iShowList.showList();
                dismiss();
            } else {
                PageUtils.showMessage(getContext(), "新增失败。");
            }
        } else {
            //更改
            leTmp.setPrimary_id(livingExpenses.getPrimary_id());
            if (DbBase.update(leTmp) > 0) {
                PageUtils.showMessage(getContext(), "更改成功。");
                iShowList.showList();
                dismiss();
            } else {
                PageUtils.showMessage(getContext(), "更改失败。");
            }
        }
    }
}
