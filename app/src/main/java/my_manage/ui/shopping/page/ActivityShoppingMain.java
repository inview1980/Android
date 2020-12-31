package my_manage.ui.shopping.page;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.ShoppingRecord;
import my_manage.tool.DateUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.TableUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyBaseSwipeBackActivity;
import my_manage.ui.widght.SpinnerStringAdapter;

public class ActivityShoppingMain extends MyBaseSwipeBackActivity implements IShowList {
    @BindView(R.id.leftSpinner)  Spinner      leftSpinner;
    @BindView(R.id.rightSpinner) Spinner      rightSpinner;
    @BindView(R.id.frameLayout)  FrameLayout  frameLayout;
    @BindView(R.id.statesBar)    TextView     statesBar;
    private                      List<String> propLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_spinner2_table_state);
        ButterKnife.bind(this);

        setTitle("购物清单");
        rightSpinner.setVisibility(View.GONE);
        initSpinner(DbHelper.getInstance().getShoppingToList());
    }

    private void initSpinner(List<ShoppingRecord> shoppingRecords) {
        propLst = shoppingRecords.stream().map(ShoppingRecord::getProject).distinct().sorted().collect(Collectors.toList());
        propLst.add(0, "全部");
        leftSpinner.setAdapter(new SpinnerStringAdapter(this, propLst));
    }

    @Override
    public void showList() {
        val                  shoppingRecordLst = DbHelper.getInstance().getShoppingToList();
        //按日期排序
        shoppingRecordLst.stream()
                .sorted((s1,s2)-> Double.compare(s1.getRecordDate().getTimeInMillis(),s2.getRecordDate().getTimeInMillis()))
                .collect(Collectors.toList());
        //是否选择第一项即"全部"
        List<ShoppingRecord> srLst             = null;
        if (leftSpinner.getSelectedItemPosition() != 0) {
            srLst = shoppingRecordLst.stream()
                    .filter(sr -> sr.getProject().equals(leftSpinner.getSelectedItem().toString())).collect(Collectors.toList());
        } else {
            srLst = shoppingRecordLst;
        }

        List<ArrayList<String>> dataLst = srLst.stream().map(sr -> {
            val tmpLst = new ArrayList<String>();
            tmpLst.add(sr.getProject());
            tmpLst.add(sr.getType());
            tmpLst.add(DateUtils.date2String(sr.getRecordDate()));
            tmpLst.add(StrUtils.df4.format(sr.getTotalMoney()));
            tmpLst.add(sr.getRemarks());
            return tmpLst;
        }).collect(Collectors.toList());
        addHead(dataLst);
        TableUtils.initTableView(this, frameLayout, new ArrayList<>(dataLst));

        double sum = srLst.stream().mapToDouble(ShoppingRecord::getTotalMoney).sum();
        statesBar.setText("合计:" + StrUtils.df4.format(sum));
    }

    private void addHead(List<ArrayList<String>> dataLst) {
        ArrayList<String> head = new ArrayList<>(Arrays.asList("项目", "规格", "日期", "金额", "备注"));
        dataLst.add(0, head);
    }

    @OnItemSelected(R.id.leftSpinner)
    void onItemSelected() {
        showList();
    }
}