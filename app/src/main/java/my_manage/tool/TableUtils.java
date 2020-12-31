package my_manage.tool;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import lombok.val;
import my_manage.password_box.R;

/**
 * @author inview
 * @Date 2020/12/29 16:29
 * @Description :
 */
public class TableUtils {
    /**
     * 初始化 {@link LockTableView}控件，显示后返回该控件
     * @param viewGroup 装入该控件的容器
     * @param dataLst 数据源
     */
    public static LockTableView initTableView(Context context, ViewGroup viewGroup, ArrayList<ArrayList<String>> dataLst
            , BiConsumer<View, Integer> onLongClick) {
        return initTableView(context, viewGroup, dataLst, onLongClick, true);
    }
    /**
     * 初始化 {@link LockTableView}控件，显示后返回该控件
     * @param viewGroup 装入该控件的容器
     * @param dataLst 数据源
     * @return
     */
    public static LockTableView initTableView(Context context, ViewGroup viewGroup, ArrayList<ArrayList<String>> dataLst) {
        return initTableView(context, viewGroup, dataLst, null, true);
    }

    /**
     * 初始化 {@link LockTableView}控件，显示后返回该控件
     * @param context
     * @param viewGroup 装入该控件的容器
     * @param dataLst 数据源
     * @param isLockFristColumn 是否锁定第一列，默认锁定
     */
    public static LockTableView initTableView(Context context, ViewGroup viewGroup, ArrayList<ArrayList<String>> dataLst
            , BiConsumer<View, Integer> onLongClick, boolean isLockFristColumn) {
        val tableView = new LockTableView(context, viewGroup, dataLst);
        tableView
                .setLockFristColumn(isLockFristColumn) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(500) //列最大宽度
                .setMinColumnWidth(60) //列最小宽度
//                .setColumnWidth(1,60) //设置指定列文本宽度(从0开始计算,宽度单位dp)
                .setMinRowHeight(20)//行最小高度
                .setMaxRowHeight(60)//行最大高度
                .setTextViewSize(14) //单元格字体大小
                .setCellPadding(5)//设置单元格内边距(dp)
                .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                .setNullableString("") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    //设置横向滚动监听
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
                        Log.e("滚动值", "[" + x + "]" + "[" + y + "]");
                    }
                })
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    //设置横向滚动边界监听
                    @Override
                    public void onLeft(HorizontalScrollView view) {
                        Log.e("滚动边界", "滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
                        Log.e("滚动边界", "滚动到最右边");
                    }
                })
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    //下拉刷新、上拉加载监听
                    @Override
                    public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> dataLst) {
//                        Log.e("表格主视图",mXRecyclerView);
//                        Log.e("表格所有数据",mTableDatas);
                        //如需更新表格数据调用,部分刷新不会全部重绘
                        tableView.setTableDatas(dataLst);
                        //停止刷新
                        mXRecyclerView.refreshComplete();
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
//                        Log.e("表格主视图",mXRecyclerView);
//                        Log.e("表格所有数据",mTableDatas);
                        //如需更新表格数据调用,部分刷新不会全部重绘
                        tableView.setTableDatas(mTableDatas);
                        //停止刷新
                        mXRecyclerView.loadMoreComplete();
                        //如果没有更多数据调用
                        mXRecyclerView.setNoMore(true);
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View view, int i) {
                        if (onLongClick != null) onLongClick.accept(view, i);
                    }


                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        return tableView;
    }

}
