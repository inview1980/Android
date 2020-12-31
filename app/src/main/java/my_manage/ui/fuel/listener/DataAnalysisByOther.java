package my_manage.ui.fuel.listener;

import android.widget.FrameLayout;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.val;
import my_manage.pojo.FuelRecord;
import my_manage.tool.StrUtils;
import my_manage.tool.TableUtils;
import my_manage.tool.database.DbHelper;

/**
 * @author inview
 * @Date 2020/12/23 16:09
 * @Description :
 */
public class DataAnalysisByOther {
    private FrameLayout      frameLayout;
    private DialogFragment   dialogFragment;
    private List<Integer>    yearLst;
    private List<FuelRecord> fuelRecordList;

    public DataAnalysisByOther(FrameLayout frameLayout, DialogFragment dialogFragment) {
        this.frameLayout = frameLayout;
        this.dialogFragment = dialogFragment;
        fuelRecordList = DbHelper.getInstance().getFuelRecordList();
        yearLst = fuelRecordList.stream().map(fr -> fr.getTime().get(Calendar.YEAR)).distinct().sorted().collect(Collectors.toList());
    }

    /**
     * 统计每月的公里数
     */
    public void KM() {
        showTable(getKMData(this.fuelRecordList, yearLst));
    }

    /**
     * 统计每月的加油量
     */
    public void FuelNumber() {
        showTable(getData(fuelRecordList, yearLst, FuelRecord::getRise));
    }

    /**
     * 每月加油金额
     */
    public void money() {
        showTable(getData(fuelRecordList, yearLst, FuelRecord::getMoney));
    }

    /**
     * 计算12个月的平均数
     */
    private void showTable(ArrayList<ArrayList<String>> dataLst) {
        addHead(dataLst);
        addTitle(dataLst);
        addTotal(dataLst, false);
        TableUtils.initTableView(dialogFragment.getContext(), frameLayout, dataLst);
    }

    /**
     * 计算12个月的平均数
     */
    private void showTableNoTotal(ArrayList<ArrayList<String>> dataLst) {
        addHead(dataLst);
        addTitle(dataLst);
//        addTotal(dataLst, true);
        TableUtils.initTableView(dialogFragment.getContext(), frameLayout, dataLst);
    }


    /**
     * 统计给定的字段，统计横向为年，竖向为月的二维数组中，每月的数据和，填充到二维数组中
     * 2000年  2001年  2002年
     * 1月
     * 2月
     *
     * @param fuelRecordList 所有数据集合
     * @param yearLst        年的集合
     * @param supplier       FuelRecord类中Double或Integer字段
     * @return
     */
    private ArrayList<ArrayList<String>> getData(List<FuelRecord> fuelRecordList, List<Integer> yearLst, Function<? super FuelRecord, ? extends Double> supplier) {
        ArrayList<ArrayList<String>> resultLst = new ArrayList<>();
        for (int month = 0; month < 12; month++) {
            ArrayList<String> tmpLst = new ArrayList<>();
            for (final Integer year : yearLst) {
                final int finalMonth = month;
                double sum = fuelRecordList.stream().mapToDouble(fr -> {
                    if (fr.getTime().get(Calendar.YEAR) == year && fr.getTime().get(Calendar.MONTH) == finalMonth)
                        return supplier.apply(fr);
                    else
                        return 0;
                }).sum();
                tmpLst.add(StrUtils.df4.format(sum));
            }
            resultLst.add(tmpLst);
        }
        return resultLst;
    }

    private void addTotal(ArrayList<ArrayList<String>> kmData, boolean isAverage) {
        ArrayList<String> totalLst = new ArrayList<>();
        totalLst.add("合计");
        for (int i = 0; i < yearLst.size(); i++) {
            List<Double> tmpLst = new ArrayList<>();
            for (int month = 1; month < 13; month++) {
                Double item = Double.parseDouble(kmData.get(month).get(i + 1));
                tmpLst.add(item);
            }
            if (isAverage) {
                //计算12个月的平均数
                totalLst.add(StrUtils.df4.format(tmpLst.stream().mapToDouble(Double::doubleValue).sum() / 12));
            } else {
                //计算12个月的总数
                totalLst.add(StrUtils.df4.format(tmpLst.stream().mapToDouble(Double::doubleValue).sum()));
            }
        }
        kmData.add(totalLst);
    }

    private void addTitle(ArrayList<ArrayList<String>> kmData) {
        int month = 1;
        for (int i = 1; i < kmData.size(); i++) {
            kmData.get(i).add(0, month + "月");
            month++;
        }
    }

    private ArrayList<ArrayList<String>> getKMData(List<FuelRecord> fuelRecordList, List<Integer> yearLst) {
        ArrayList<ArrayList<String>> resultLst = new ArrayList<>();
        //计算每年、每个月的里程数
        ArrayList<Integer> tmp = new ArrayList<>();
        for (final Integer year : yearLst) {
            for (int month = 0; month < 12; month++) {
                final int finalI = month;
                List<Integer> num = fuelRecordList.stream()
                        .filter(fuel -> {
                            Calendar date = fuel.getTime();
                            return date.get(Calendar.YEAR) == year && date.get(Calendar.MONTH) == finalI;
                        }).map(FuelRecord::getOdometerNumber)
                        .sorted().collect(Collectors.toList());
                int total = (num.size() > 0) ? num.get(num.size() - 1) : 0;
                tmp.add(total);
            }
        }
        //计算每年、每个月的公里数
        for (int i = 1; i < tmp.size(); i++) {
            if (tmp.get(i) == 0)
                tmp.set(i, tmp.get(i - 1));
        }
        for (int i = tmp.size() - 1; i > 0; i--) {
            tmp.set(i, tmp.get(i) - tmp.get(i - 1));
        }
        //转换成表格数据
        for (int month = 0; month < 12; month++) {
            ArrayList<String> tmpLst = new ArrayList<>();
            for (int year = 0; year < yearLst.size(); year++) {
                tmpLst.add(tmp.get(12 * year + month) + "");
            }
            resultLst.add(tmpLst);
        }
        return resultLst;
    }


    private void addHead(ArrayList<ArrayList<String>> dataLst) {
        val head = new ArrayList<String>();
        head.add("");
        for (final Integer year : yearLst) {
            head.add(year + "年");
        }
        dataLst.add(0, head);
    }

    /**
     * 每月百公里油耗
     */
    public void fuelConsumption() {
        val riseLst = getData(fuelRecordList, yearLst, FuelRecord::getRise);
        val kmLst   = getKMData(fuelRecordList, yearLst);
        if (riseLst.size() != 12 || kmLst.size() != 12)
            return;
        val resultLst = new ArrayList<ArrayList<String>>();
        for (int rows = 0; rows < riseLst.size(); rows++) {
            ArrayList<String> row = new ArrayList<>();
            for (int column = 0; column < riseLst.get(rows).size(); column++) {
                double rise = Double.parseDouble(riseLst.get(rows).get(column));
                double km   = Double.parseDouble(kmLst.get(rows).get(column));
                row.add(km == 0 ? "0" : StrUtils.df4.format(rise / km * 100));
            }
            resultLst.add(row);
        }
        showTableNoTotal(resultLst);
    }

    public void saveMoney() {
        showTable(getData(fuelRecordList, yearLst, fr -> fr.getMarketPrice() * fr.getRise() - fr.getMoney()));
    }
}
