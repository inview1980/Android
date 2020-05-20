package my_manage.adapter_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import my_manage.password_box.fragment.PasswordManageItemDetailsFragment;
import my_manage.password_box.pojo.UserItem;

public final class PwdPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> vList =new ArrayList<>();

    public PwdPageAdapter(FragmentManager fm, List<UserItem> vList) {
        super(fm);
        if(vList==null){
            //新增
            this.vList.add(new PasswordManageItemDetailsFragment(null));
        }else {
            //修改
            for (int i = 0; i < vList.size(); i++) {
                this.vList.add(new PasswordManageItemDetailsFragment(vList.get(i)));
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return vList.get(position);
    }


    @Override
    public int getCount() {
        return vList.size();
    }

}
