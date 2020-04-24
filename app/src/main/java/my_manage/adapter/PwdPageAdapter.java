package my_manage.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import my_manage.password_box.page.PasswordManageItemDetailsFragment;
import my_manage.password_box.pojo.UserItem;

public final class PwdPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> vList =new ArrayList<>();

    public PwdPageAdapter(FragmentManager fm, List<UserItem> vList) {
        super(fm);
        if(vList==null){
            //新增
            this.vList.add(new PasswordManageItemDetailsFragment(-1));
        }else {
            //修改
            for (int i = 0; i < vList.size(); i++) {
                this.vList.add(new PasswordManageItemDetailsFragment(i));
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

//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view==object;
//    }
}
