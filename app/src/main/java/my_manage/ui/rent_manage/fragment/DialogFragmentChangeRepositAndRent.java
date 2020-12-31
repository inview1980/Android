package my_manage.ui.rent_manage.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.deadline.statebutton.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import my_manage.iface.ISetValueByDouble;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.database.DbBase;
import my_manage.ui.widght.MyDialogFragment;

/**
 * 调整押金、租金窗口
 *
 * @author inview
 * @Date 2020/12/7 11:35
 * @Description :
 */
public final class DialogFragmentChangeRepositAndRent<T extends Activity & IShowList> extends MyDialogFragment {
    @BindView(R.id.toolbar)                     Toolbar           toolbar;
    @BindView(R.id.rental_change_dialog_lable1) TextView          rentalChangeDialogLable1;
    @BindView(R.id.rental_changeRent_oldNum)    TextView          rentalChangeRentOldNum;
    @BindView(R.id.rental_change_dialog_lable2) TextView          rentalChangeDialogLable2;
    @BindView(R.id.rental_changeRent_newNum)    EditText          newNum;
    @BindView(R.id.rental_changeRent_okBtn)     StateButton       rentalChangeRentOkBtn;
    private                                     ISetValueByDouble setValueFunc;
    private                                     ShowRoomDetails   sr;
    private                                     String            money;
    private                                     String            title;
    private                                     T                 activity;


    public DialogFragmentChangeRepositAndRent(T activity, ISetValueByDouble setValueFunc, ShowRoomDetails sr, String money, String title) {
        this.setValueFunc = setValueFunc;
        this.sr = sr;
        this.money = money;
        this.title = title;
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rental_change_dialog, container, false);
        bind = ButterKnife.bind(this, v);

        if (sr == null) dismiss();

        rentalChangeRentOldNum.setText(this.money);
        newNum.setText(this.money);
        rentalChangeDialogLable1.setText("原" + title + "为:");
        rentalChangeDialogLable2.setText("调整" + title + "为:");
        toolbar.setTitle("调整" + title);
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        rentalChangeRentOkBtn.setEnabled(false);
        return v;
    }


    @OnTextChanged(R.id.rental_changeRent_newNum)
    void onTextChanged() {
        rentalChangeRentOkBtn.setEnabled(true);
    }

    @OnClick(R.id.rental_changeRent_okBtn)
    void onClick() {
        try {
            if (StrUtils.isNotBlank(newNum.getText().toString())) {
                setValueFunc.setValue(Double.parseDouble(newNum.getText().toString()));
                if (sr.getRentalRecord().getPrimary_id() != 0 && DbBase.update(sr.getRentalRecord()) > 0) {
                    //成功，刷新
                    activity.showList();
                    Toast.makeText(activity, "调整" + title + "成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        } catch (Exception e) {
            PageUtils.Error(getClass().getSimpleName() + ":" + e.getLocalizedMessage());
        }
    }

}
