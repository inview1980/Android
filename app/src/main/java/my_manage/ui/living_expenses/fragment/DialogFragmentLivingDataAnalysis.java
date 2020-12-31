package my_manage.ui.living_expenses.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import lombok.val;
import my_manage.password_box.R;
import my_manage.pojo.LivingExpenses;
import my_manage.tool.StrUtils;
import my_manage.tool.TableUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.MyDialogFragment;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/30 16:19
 * @Description :
 */
public class DialogFragmentLivingDataAnalysis extends MyDialogFragment {
    @BindView(R.id.toolbar)      Toolbar              toolbar;
    @BindView(R.id.leftSpinner)  Spinner              leftSpinner;
    @BindView(R.id.rightSpinner) Spinner              rightSpinner;
    @BindView(R.id.frameLayout)  FrameLayout          frameLayout;
    @BindView(R.id.statesBar)    TextView             statesBar;
    private                      String               typeName;
    private                      List<LivingExpenses> allLiveLst;

    public DialogFragmentLivingDataAnalysis(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tool_spinner2_table_state, container, false);
        bind = ButterKnife.bind(this, v);

        statesBar.setVisibility(View.GONE);
        toolbar.setTitle("数据分析");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());

        init();
        return v;
    }

    private void init() {
        this.allLiveLst = DbHelper.getInstance().getAllLive();
        val types = this.allLiveLst.stream().map(LivingExpenses::getProject).distinct().collect(Collectors.toList());
        leftSpinner.setAdapter(new SpinnerStringAdapter(getContext(), types));
        if (StrUtils.isNotBlank(this.typeName)) {
            leftSpinner.setSelection(types.indexOf(this.typeName));
        }
    }

    @OnItemSelected(R.id.leftSpinner)
    void onTypeSpinnerSelected() {
        val rooms = this.allLiveLst.stream().map(LivingExpenses::getRoomNumber).distinct().sorted().collect(Collectors.toList());
        rightSpinner.setAdapter(new SpinnerStringAdapter(getContext(), rooms));
    }

    @OnItemSelected(R.id.rightSpinner)
    void onRoomSpinnerSelected() {
        //获取有数据的年份，从大到小排序
        val years = this.allLiveLst.stream().filter(live -> live.getProject().equals(leftSpinner.getSelectedItem().toString()))
                .map(live -> live.getPaymentDate().get(Calendar.YEAR))
                .distinct().sorted().collect(Collectors.toList());
        Collections.reverse(years);

        val dataLst = new ArrayList<ArrayList<String>>();
        for (int month = 0; month < 12; month++) {
            val row = new ArrayList<String>();
            for (final Integer year : years) {
                final int finalMonth = month;
                double sum = this.allLiveLst.stream()
                        .filter(live -> live.getProject().equals(leftSpinner.getSelectedItem().toString())
                                && live.getPaymentDate().get(Calendar.YEAR) == year
                                && live.getPaymentDate().get(Calendar.MONTH) == finalMonth)
                        .mapToDouble(LivingExpenses::getTotalMoney)
                        .sum();
                row.add(StrUtils.df4.format(sum));
            }
            dataLst.add(row);
        }
        val total = new ArrayList<String>();
        years.forEach(year -> {
            double sum = this.allLiveLst.stream().filter(live -> live.getProject().equals(leftSpinner.getSelectedItem().toString())
                    && live.getPaymentDate().get(Calendar.YEAR) == year).mapToDouble(LivingExpenses::getTotalMoney).sum();
            total.add(StrUtils.df4.format(sum));
        });
        dataLst.add(total);
        addHead(dataLst, years);
        addFirst(dataLst);
        TableUtils.initTableView(getContext(), frameLayout, dataLst);
    }

    private void addFirst(ArrayList<ArrayList<String>> dataLst) {
        dataLst.get(0).add(0, "");
        for (int i = 1; i < 13; i++) {
            dataLst.get(i).add(0, i + "月");
        }
        dataLst.get(dataLst.size() - 1).add(0, "合计");
    }

    private void addHead(ArrayList<ArrayList<String>> dataLst, List<Integer> years) {
        val head = new ArrayList<String>();
        years.forEach(year -> head.add(year + "年"));
        dataLst.add(0, head);
    }
}
