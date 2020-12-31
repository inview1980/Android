package my_manage.ui.fuel.listener;

import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;
import my_manage.password_box.R;
import my_manage.pojo.FuelRecord;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.fuel.page.FuelRecordMainActivity;

/**
 * @author inview
 * @Date 2020/12/23 15:43
 * @Description :
 */
public class DataAnalysisByAll implements View.OnClickListener {
    private FrameLayout    frameLayout;
    private DialogFragment dialogFragment;
    private TableLayout    tableLayout;
    private String         payForString          = "总花费(元)：";
    private String         OdometerNumberString  = "总公里数(km)：";
    private String         FuelNumberString      = "总加油量(L)：";
    private String         FuelConsumptionString = "平均油耗(L/100km)：";
    private String         SaveMoneyString       = "节省费用(元)：";

    public DataAnalysisByAll(DialogFragment dialogFragment, FrameLayout frameLayout) {
        this.dialogFragment = dialogFragment;
        this.frameLayout = frameLayout;
        tableLayout = getTableLayout();
        init();
    }

    private void init() {
        Map<String, Double> dataMap = getDataAnalysisInfo();
        for (final Map.Entry<String, Double> entry : dataMap.entrySet()) {
            View view;
            if (entry.getKey().startsWith("-") || Character.isDigit(entry.getKey().charAt(0))) {
                //每年的数据
                view = LayoutInflater.from(dialogFragment.getContext()).inflate(R.layout.table_row_value_style, tableLayout, false);
                TextView t1 = view.findViewById(R.id.text1);
                TextView t2 = view.findViewById(R.id.text2);
                t1.setText(entry.getKey());
                t2.setText(StrUtils.df4.format(entry.getValue()));
                if (entry.getKey().startsWith("-")) {
                    t1.setVisibility(View.GONE);
                    t2.setVisibility(View.GONE);
                }
                view.setOnClickListener(this);
            } else {
                //分类总数据,彩虹字体
                view = LayoutInflater.from(dialogFragment.getContext()).inflate(R.layout.table_row_title_style, tableLayout, false);
                TextView t1 = (TextView) view.findViewById(R.id.title1);
                TextView t2 = (TextView) view.findViewById(R.id.title2);
                t1.setText(entry.getKey());
                t2.setText(StrUtils.df4.format(entry.getValue()));
            }
            tableLayout.addView(view);
        }
    }

    private TableLayout getTableLayout() {
        val                       layout = new TableLayout(dialogFragment.getContext());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        int                       dim    = dialogFragment.getResources().getDimensionPixelOffset(R.dimen.rental_main_gridview_margin_3);
        params.setMargins(dim,dim,dim, 1);
        layout.setColumnStretchable(1, true);
        params.width = TableLayout.LayoutParams.MATCH_PARENT;
        params.height = TableLayout.LayoutParams.WRAP_CONTENT;
        layout.setLayoutParams(params);
        return layout;
    }

    public void all() {
        frameLayout.addView(tableLayout);
    }

    private Map<String, Double> getDataAnalysisInfo() {
        Map<String, Double> result = new LinkedHashMap<>();
        val                 frList = DbHelper.getInstance().getFuelRecordList();
        if (frList == null || frList.size() == 0) return new LinkedHashMap<>();

        //有记录的年份集合，从2018年开始
        val years = frList.stream().map(fr -> fr.getTime().get(Calendar.YEAR))
                .distinct().sorted().collect(Collectors.toList());

        //里程
        everyYearOdometerNumber(result, frList, years);
        //加油量
        everyYearFuelNumber(result, frList, years);
        //花费
        everyYearPayMoney(result, frList, years);
        //平均油耗
        ereryYearFuelConsumption(result, years);
        //节省费用
        ereryYearSaveMoney(result, frList, years);
        return result;
    }

    /**
     * 节省费用
     *
     * @param result
     * @param frList 所有加油记录
     * @param years  有记录的年份集合，从2018年开始
     */
    private void ereryYearSaveMoney(Map<String, Double> result, List<FuelRecord> frList, List<Integer> years) {
        double total = frList.stream().mapToDouble(fr -> fr.getMarketPrice() * fr.getRise() - fr.getMoney()).sum();
        result.put(SaveMoneyString, total);
        years.forEach(year -> {
            double ts = frList.stream().filter(fr -> fr.getTime().get(Calendar.YEAR) == year)
                    .mapToDouble(fr -> fr.getMarketPrice() * fr.getRise() - fr.getMoney()).sum();
            result.put(year + "年" + SaveMoneyString, ts);
        });
        result.put("-" + result.size(), 0.0);
    }

    /**
     * 每年平均油耗
     *
     * @param result
     * @param years  有记录的年份集合，从2018年开始
     */
    private void ereryYearFuelConsumption(Map<String, Double> result, List<Integer> years) {
        try {
            //提取总加油量
            double fuelNum = result.get(FuelNumberString);
            //提取总里程
            double odometer = result.get(OdometerNumberString);
            result.put(FuelConsumptionString, fuelNum / odometer * 100);
            years.forEach(year -> {
                //提取年加油量
                double fuel = result.get(year + "年" + FuelNumberString);
                //提取年里程
                double odomet = result.get(year + "年" + OdometerNumberString);
                result.put(year + "年" + FuelConsumptionString, fuel / odomet * 100);
            });
            result.put("-" + result.size(), 0.0);
        } catch (NumberFormatException e) {
            PageUtils.showMessage(dialogFragment.getContext(), "读取数据错误！");
        }
    }

    /**
     * 每年加油费用
     *
     * @param result
     * @param frList 所有加油记录
     * @param years  有记录的年份集合，从2018年开始
     */
    private void everyYearPayMoney(Map<String, Double> result, List<FuelRecord> frList, List<Integer> years) {
        result.put(payForString, frList.stream().mapToDouble(FuelRecord::getMoney).sum());
        years.forEach(year -> {
            double sum = frList.stream().filter(fr -> fr.getTime().get(Calendar.YEAR) == year).mapToDouble(FuelRecord::getMoney).sum();
            result.put(year + "年" + payForString, sum);
        });
        result.put("-" + result.size(), 0.0);
    }

    /**
     * 每年的加油量
     *
     * @param result
     * @param frList 所有加油记录
     * @param years  有记录的年份集合，从2018年开始
     */
    private void everyYearFuelNumber(Map<String, Double> result, List<FuelRecord> frList, List<Integer> years) {
        result.put(FuelNumberString, frList.stream().mapToDouble(FuelRecord::getRise).sum());
        years.forEach(year -> {
            double sum = frList.stream().filter(fr -> fr.getTime().get(Calendar.YEAR) == year).mapToDouble(FuelRecord::getRise).sum();
            result.put(year + "年" + FuelNumberString, sum);
        });
        result.put("-" + result.size(), 0.0);
    }

    /**
     * 每年的公里数
     *
     * @param result
     * @param frList 所有加油记录
     * @param years  有记录的年份集合，从2018年开始
     */
    private void everyYearOdometerNumber(Map<String, Double> result, List<FuelRecord> frList, List<Integer> years) {
        double lastYearMax = 0.0;
        result.put(OdometerNumberString, (double) frList.stream().max((t1, t2) -> Integer.compare(t1.getOdometerNumber(), t2.getOdometerNumber()))
                .orElse(new FuelRecord()).getOdometerNumber());

        for (final Integer year : years) {
            int max = frList.stream().filter(fr -> fr.getTime().get(Calendar.YEAR) == year)
                    .max((t1, t2) -> Integer.compare(t1.getOdometerNumber(), t2.getOdometerNumber()))
                    .orElse(new FuelRecord()).getOdometerNumber();
            result.put(year + "年" + OdometerNumberString, (max - lastYearMax));
            lastYearMax = max;
        }
        result.put("-" + result.size(), 0.0);
    }


    @Override
    public void onClick(View v) {
        String txt = ((TextView) (v.findViewById(R.id.text1))).getText().toString();
        //首字符为数字
        Intent intent = new Intent(dialogFragment.getContext(), FuelRecordMainActivity.class);
        try {
            int year = Integer.parseInt(txt.substring(0, 4));
            intent.putExtra("year", year);
        } catch (NumberFormatException e) {
            PageUtils.Error(this.getClass().getSimpleName() + "转换数字：年 失败");
        }
        dialogFragment.startActivity(intent);
    }
}
