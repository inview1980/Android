package my_manage.ui.widght;

import android.content.Context;

import androidx.annotation.NonNull;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.List;

import my_manage.password_box.R;

/**
 * 包装类，针对Spinner控件的adapter属性进行封装
 */
public class SpinnerStringAdapter extends CommonAdapter<String> {

    public SpinnerStringAdapter(@NonNull Context context, List<String> data) {
        super(context, R.layout.spanner_item_textview, data);
    }

    @Override
    public void onUpdate(BaseAdapterHelper helper, String item, int position) {
        helper.setText(android.R.id.text1, item);
    }
}
