package my_manage.ui.living_expenses.page;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.LivingExpenses;
import my_manage.pojo.RoomDetails;
import my_manage.tool.DateUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.TableUtils;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;
import my_manage.ui.living_expenses.fragment.DialogFragmentLivingAddOrUpdate;
import my_manage.ui.living_expenses.fragment.DialogFragmentLivingDataAnalysis;
import my_manage.ui.widght.MyBaseSwipeBackActivity;
import my_manage.ui.widght.SpinnerStringAdapter;

/**
 * @author inview
 * @Date 2020/12/28 11:38
 * @Description :
 */
public class ActivityLivingByType extends MyBaseSwipeBackActivity implements IShowList {
    @BindView(R.id.frameLayout)       FrameLayout          frameLayout;
    @BindView(R.id.statesBar)   TextView statesBar;
    @BindView(R.id.leftSpinner)  Spinner roomNumberSpinner;
    @BindView(R.id.rightSpinner) Spinner yearSpinner;
    private                      String  typeName;
    private                           List<LivingExpenses> livingExpensesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_spinner2_table_state);
        ButterKnife.bind(this);

        typeName = getIntent().getStringExtra("typeName");
        setTitle(typeName);
        livingExpensesList = DbHelper.getInstance().getLiveByType(this.typeName);

        initSpanner();
        showList();
    }


    private void initSpanner() {
        val roomNameLst = this.livingExpensesList.stream().map(LivingExpenses::getRoomNumber).distinct().collect(Collectors.toList());
        roomNameLst.add(0, "全部");
        roomNumberSpinner.setAdapter(new SpinnerStringAdapter(this, roomNameLst));

        val yearLst = this.livingExpensesList.stream().map(le -> le.getPaymentDate().get(Calendar.YEAR))
                .distinct().sorted().collect(Collectors.toList());
        val yearStrLst = new ArrayList<String>();
        yearStrLst.add("全部");
        yearLst.forEach(year -> yearStrLst.add(year + ""));
        yearSpinner.setAdapter(new SpinnerStringAdapter(this, yearStrLst));
    }

    @Override
    public void showList() {
        frameLayout.removeAllViews();

        List<LivingExpenses> tmpLst = null;
        if (roomNumberSpinner.getSelectedItemId() != 0) {
            tmpLst = this.livingExpensesList.stream()
                    .filter(le -> le.getRoomNumber().equals(roomNumberSpinner.getSelectedItem().toString()))
                    .collect(Collectors.toList());
        } else {
            tmpLst = this.livingExpensesList;
        }
        if (yearSpinner.getSelectedItemId() != 0) {
            tmpLst = tmpLst.stream()
                    .filter(le -> le.getPaymentDate().get(Calendar.YEAR) == Integer.parseInt(yearSpinner.getSelectedItem().toString()))
                    .collect(Collectors.toList());
        }
        ArrayList<ArrayList<String>> showData = getShowData(tmpLst);
        //显示合计
        statesBar.setText("金额合计：" + tmpLst.stream().mapToDouble(LivingExpenses::getTotalMoney).sum());
        //添加标题栏
        addTitle(showData);
        initTableView(showData);
    }

    @OnItemSelected({R.id.leftSpinner, R.id.rightSpinner})
    void onItemSelected() {
        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.living_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addRecord:
                new DialogFragmentLivingAddOrUpdate(this, typeName).show(getSupportFragmentManager(), "");
                break;
            case R.id.dataAnalysis:
                new DialogFragmentLivingDataAnalysis(typeName).show(getSupportFragmentManager(), "");
                break;
            case R.id.deleteAllRecord:
                dialogRemoveAllByItem();
                break;
            // TODO: 2020/12/28
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogRemoveAllByItem() {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        dialog.setTitle("删除")
                .setMessage("确定删除所有" + this.typeName + "记录？")
                .setPositiveButton("确定", (dialog1, which) -> {
                    if (DbBase.deleteWhere(LivingExpenses.class, "project", new String[]{this.typeName}) > 0) {
                        //刷新
                        PageUtils.showMessage(this, "删除记录成功");
                        finish();
                    }
                }).show();
    }

    private void initTableView(ArrayList<ArrayList<String>> dataLst) {
        TableUtils.initTableView(this, frameLayout, dataLst, (view, position) -> {
            if (position == 0) return;
            PopupMenu menu = new PopupMenu(ActivityLivingByType.this, view);
            menu.getMenu().add(0, 0, 0, "修改");
            menu.getMenu().add(0, 1, 1, "删除");
            menu.setOnMenuItemClickListener(item -> {
                onLongClick(item.getItemId(), livingExpensesList.get(position - 1));
                return false;
            });
            menu.show();
        }, false);
    }

    private void addTitle(ArrayList<ArrayList<String>> showData) {
        val head = new ArrayList<String>();
        head.add("小区");
        head.add("房间号");
        head.add("付款日期");
        head.add("金额");
        head.add("备注");
        showData.add(0, head);
    }

    private ArrayList<ArrayList<String>> getShowData(List<LivingExpenses> data) {
        ArrayList<ArrayList<String>> resultLst = new ArrayList<>();
        //建立房号，小区名的字典
        Map<String, String> roomDic = DbHelper.getInstance().getRoomDetailsAll().stream().collect(Collectors.toMap(RoomDetails::getRoomNumber, RoomDetails::getCommunityName));
        for (final LivingExpenses live : data) {
            val tmpLst = new ArrayList<String>();
            tmpLst.add(roomDic.get(live.getRoomNumber()));
            tmpLst.add(live.getRoomNumber());
            tmpLst.add(DateUtils.date2String(live.getPaymentDate()));
            tmpLst.add(StrUtils.df4.format(live.getTotalMoney()));
            tmpLst.add(live.getRemarks());
            resultLst.add(tmpLst);
        }
        return resultLst;
    }

    private boolean onLongClick(int itemId, LivingExpenses live) {
        if (itemId == 0) {
            //修改
            new DialogFragmentLivingAddOrUpdate(this::showList, this.typeName, live).show(getSupportFragmentManager(), "");
        } else if (itemId == 1) {
            //删除
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("删除").setMessage("确定要删除此项记录吗？");
            dialog.setPositiveButton(R.string.ok_cn, (dialog1, which) -> {
                if (DbHelper.getInstance().delLivingExpensesById(live.getPrimary_id()) > 0) {
                    livingExpensesList.remove(live);
                    showList();
                    Toast.makeText(this, "删除记录成功！", Toast.LENGTH_SHORT).show();
                }
            }).show();
        }
        return false;
    }
}
