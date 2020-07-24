package my_manage.ui.rent_manage.page;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.ui.adapter_expandable_List.PersonExtendableListViewAdapter;
import my_manage.iface.IShowList;
import my_manage.ui.password_box.R;
import my_manage.tool.database.DbHelper;
import my_manage.pojo.PersonDetails;
import my_manage.ui.widght.MyBaseSwipeBackActivity;

/**
 * 显示租户
 */
public class ShowPersonExpandActivity extends MyBaseSwipeBackActivity implements IShowList {
    @BindView(R.id.main_expandable_listview) ExpandableListView listView;

    private                     PersonExtendableListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);

        ButterKnife.bind(this);

        setTitle("所有租户信息");

        //控制ExpandableListView只能打开一个组
        listView.setOnGroupExpandListener(groupPosition -> {
            int count = adapter.getGroupCount();
            for (int i = 0; i < count; i++) {
                if (i != groupPosition) {
                    listView.collapseGroup(i);
                }
            }
        });
        showList();
    }

    @Override
    public void showList() {
        List<PersonDetails> lst = DbHelper.getInstance().getPersonList(false);
        adapter = new PersonExtendableListViewAdapter<>(this, lst);
        listView.setAdapter(adapter);
    }

}
