package my_manage.ui.fuel.page;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.ui.password_box.R;
import my_manage.ui.widght.ParallaxSwipeBackActivity;

/**
 * @author inview
 * @Date 2020/11/25 12:35
 * @Description :
 */
public final class DataAnalysisShowActivity extends ParallaxSwipeBackActivity {
    @BindView(R.id.toolbar)     Toolbar             toolbar;
    @BindView(R.id.tableLayout) TableLayout         tableLayout;
    private                     Map<String, String> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_analysis_details);
        ButterKnife.bind(this);

        initToolBar();
    }

    @Override
    public void showList() {
        dataMap = getDataAnalysisInfo();
        for (final Map.Entry<String, String> entry : dataMap.entrySet()) {
            View     view = LayoutInflater.from(this).inflate(R.layout.table_row_style, tableLayout, false);
            TextView t1   = view.findViewById(R.id.text1);
            TextView t2   = view.findViewById(R.id.text2);
            t1.setText(entry.getKey());
            t2.setText(entry.getValue());
            if(entry.getKey().startsWith("-")){
                t1.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
                view.findViewById(R.id.tableRow_style).setBackgroundColor(getColor(R.color.blue));
            }
            tableLayout.addView(view);
        }
    }

    private Map<String, String> getDataAnalysisInfo() {
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++) {
            result.put("key:" + i, "value:" + i);
            if (i % 5 == 0) result.put("-" + i, "");
        }
        return result;
    }

    private void initToolBar() {
        toolbar.setTitle("数据分析");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }
}
