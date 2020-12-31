package my_manage.ui.rent_manage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.StrUtils;
import my_manage.tool.TableUtils;
import my_manage.tool.database.DbHelper;
import my_manage.ui.widght.FlowRadioGroup;
import my_manage.ui.widght.MyDialogFragment;

/**
 * @author inview
 * @Date 2020/12/29 16:23
 * @Description :
 */
public class DialogFragmentRentByMonth extends MyDialogFragment implements IShowList {
    @BindView(R.id.toolbar)     Toolbar        toolbar;
    @BindView(R.id.radioGroup)  FlowRadioGroup radioGroup;
    @BindView(R.id.frameLayout) FrameLayout    frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_analysis_main, container, false);
        bind = ButterKnife.bind(this, v);
        toolbar.setTitle("房租明细");
        toolbar.setNavigationOnClickListener(v1 -> dismiss());
        ((LinearLayout) v).removeView(radioGroup);

        setFullWindow();
        showList();
        return v;
    }

    @Override
    public void showList() {
        val rooms   = DbHelper.getInstance().getAllRoomList();
        //按小区排序
        rooms.sort((s1,s2)->
                s1.getRoomDetails().getCommunityName().compareTo(s2.getRoomDetails().getCommunityName()));
        val dataLst = new ArrayList<ArrayList<String>>();
        //增加标题
        dataLst.add(new ArrayList<>(Arrays.asList("小区","房间号","面积","物业费","月租")));
        for (final ShowRoomDetails showRoomDetails : rooms) {
            ArrayList<String> tmpLst = new ArrayList<>();
            Optional.ofNullable(showRoomDetails.getRoomDetails()).ifPresent(room -> {
                tmpLst.add(room.getCommunityName());
                tmpLst.add(room.getRoomNumber());
                tmpLst.add(StrUtils.df4.format(room.getRoomArea()));
                tmpLst.add(StrUtils.df4.format(room.getPropertyPrice()));
            });
            Optional.ofNullable(showRoomDetails.getRentalRecord()).ifPresent(rr -> {
                tmpLst.add(StrUtils.df4.format(rr.getMonthlyRent()));
            });
            dataLst.add(tmpLst);
        }
        //增加月租合计
        double total=rooms.stream().mapToDouble(sr->sr.getRentalRecord().getMonthlyRent()).sum();
        dataLst.add(new ArrayList<>(Arrays.asList("合计", "", "", "", StrUtils.df4.format(total))));
        frameLayout.removeAllViews();
        TableUtils.initTableView(getContext(),frameLayout,dataLst,null,false);
    }

}
