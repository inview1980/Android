package my_manage.rent_manage.page;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.adapter_expandable_List.PersonExtendableListViewAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.PersonDetails;

public class ShowPersonExpandActivity extends AppCompatActivity implements IShowList {
    @BindView(R.id.toolbar)     Toolbar                         toolbar;
    @BindView(R.id.main_expandable_listview) ExpandableListView listView;
    @BindView(R.id.main_expandable_add_btn)  ImageButton        mainExpandableAddBtn;

    private                     PersonExtendableListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);

        ButterKnife.bind(this);

        toolbar.setTitle("所有租户信息");
        toolbar.setNavigationOnClickListener(v -> finish());
        mainExpandableAddBtn.setVisibility(View.GONE);

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
