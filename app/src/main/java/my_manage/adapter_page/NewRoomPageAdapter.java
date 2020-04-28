package my_manage.adapter_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import my_manage.rent_manage.fragment.NewRoomFragment;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public final class NewRoomPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> vList = new ArrayList<>();

    public NewRoomPageAdapter(@NonNull FragmentManager fm, List<ShowRoomDetails> vList, boolean isHistory) {
        super(fm);
        if (vList == null) {
            //新增
            this.vList.add(new NewRoomFragment(null, false));
        } else {
            //修改
            for (int i = 0; i < vList.size(); i++) {
                this.vList.add(new NewRoomFragment(vList.get(i), isHistory));
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.vList.get(position);
    }

    @Override
    public int getCount() {
        return vList.size();
    }
}
