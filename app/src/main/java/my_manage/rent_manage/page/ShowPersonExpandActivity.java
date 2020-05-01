package my_manage.rent_manage.page;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import my_manage.adapter_expandable_List.PersonExtendableListViewAdapter;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.pojo.PersonDetails;

public class ShowPersonExpandActivity extends AppCompatActivity implements IShowList {
    private PersonExtendableListViewAdapter adapter;

    @BindView(R.id.main_expandable_listview) ExpandableListView listView;
    @BindView(R.id.main_expandable_add_btn)  ImageButton        mainExpandableAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);

        ButterKnife.bind(this);

//        listView.setOnItemClickListener(this);
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
