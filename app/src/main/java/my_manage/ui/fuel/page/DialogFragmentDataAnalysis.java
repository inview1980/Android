package my_manage.ui.fuel.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.ui.fuel.listener.DataAnalysisByAll;
import my_manage.ui.fuel.listener.DataAnalysisByOther;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/7 16:52
 * @Description :
 */
public final class DialogFragmentDataAnalysis extends MyDialogFragment implements IShowList {
    @BindView(R.id.toolbar)     Toolbar     toolbar;
    @BindView(R.id.frameLayout) FrameLayout frameLayout;
//    @BindView(R.id.scrollView)  ScrollView  scrollView;
    private                     DataAnalysisByAll   listenerAll;
    private                     DataAnalysisByOther listenerOther;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_analysis_main, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("数据分析");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());

        listenerAll = new DataAnalysisByAll(this, frameLayout);
        listenerAll.all();
//        showList();

        setFullWindow();
        return v;
    }

    @Override
    public void showList() {
//        tableLayout.removeAllViews();


    }

    @OnCheckedChanged({R.id.radioAll, R.id.radioFuelConsumption, R.id.radioFuelNumber, R.id.radioKM, R.id.radioMoney, R.id.radioSaveMoney})
    void onCheckedChanged(CompoundButton cButton, boolean isChecked) {
        if (!isChecked)
            return;
        if (listenerOther == null)
            listenerOther = new DataAnalysisByOther(frameLayout, this);

        frameLayout.removeAllViews();
        switch (cButton.getId()) {
            case R.id.radioAll:
                listenerAll.all();
                break;
            case R.id.radioKM:
                listenerOther.KM();
                break;
            case R.id.radioFuelNumber:
                listenerOther.FuelNumber();
                break;
            case R.id.radioMoney:
                listenerOther.money();
                break;
            case R.id.radioFuelConsumption:
                listenerOther.fuelConsumption();
                break;
            case R.id.radioSaveMoney:
                listenerOther.saveMoney();
                break;
            default:
                break;
        }
    }


}
